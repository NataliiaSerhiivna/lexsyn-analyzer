package lab1.error;

import lab1.model.CharClass;
import lab1.model.ErrorType;
import lab1.model.State;
import lab1.model.Token;
import lab1.model.TokenType;

import java.util.AbstractMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ErrorExplainer {

    private static final Set<CharClass> OPERAND_START = EnumSet.of(CharClass.DIGIT, CharClass.LETTER, CharClass.OPEN_BRACKET);

    public Map.Entry<ErrorType, String> explain(State state, CharClass cls, Optional<Token> lastToken) {

        if (state == State.S) {
            if (cls == CharClass.MUL_DIV) {
                return entry(ErrorType.INVALID_START, "вираз не може починатись з операції * або /");
            }
            if (cls == CharClass.CLOSE_BRACKET) {
                return entry(ErrorType.INVALID_START, "вираз не може починатись із закритої дужки");
            }
            if (cls == CharClass.DOT) {
                return entry(ErrorType.INVALID_START, "вираз не може починатись із десяткової крапки");
            }
            return entry(ErrorType.UNKNOWN_SYMBOL, "неочікуваний символ на початку виразу");
        }
//
//
        if ((state == State.C || state == State.CD || state == State.CDC) && cls == CharClass.LETTER) {
            return entry(ErrorType.INVALID_IDENTIFIER_CHAR, "неочікуваний символ на початку виразу");
        }

        if (cls == CharClass.COMMA) {
            if (state == State.FNOB || state == State.AS) {
                return entry(ErrorType.MISSING_ARGUMENT, "пропущено аргумент функції перед комою");
            }
            if (state == State.OP) {
                return entry(ErrorType.MISSING_OPERAND, "кома після оператора, " + chooseOperandWord(lastToken));
            }
            if (state == State.OB) {
                return entry(ErrorType.MISSING_OPERAND, "кома одразу після відкриваючої дужки");
            }
        }

        if (state == State.AS) {
            if (cls == CharClass.CLOSE_BRACKET) {
                return entry(ErrorType.MISSING_ARGUMENT, "пропущено аргумент функції, знайдено ')'");
            }
            if (cls == CharClass.MUL_DIV) {
                return entry(ErrorType.MISSING_ARGUMENT, "аргумент функції не може починатись з операції * або /");
            }
        }

        if (state == State.OP) {
            if (cls == CharClass.PLUS_MINUS || cls == CharClass.MUL_DIV) {
                return entry(ErrorType.DOUBLE_OPERATOR, "оператор після іншого оператора");
            }
            if (cls == CharClass.CLOSE_BRACKET) {
                return entry(ErrorType.MISSING_OPERAND, chooseOperandWord(lastToken) + " після оператора");
            }
        }

        if (state == State.CDC && cls == CharClass.DOT) {
            return entry(ErrorType.SECOND_DECIMAL_DOT, "друга десяткова крапка в числі");
        }
        if (state == State.CD) {
            return entry(ErrorType.MISSING_DIGIT_AFTER_DOT, "відсутня цифра після десяткової крапки");
        }

        // --- '*' або '/' одразу після відкриваючої дужки ---
        if ((state == State.OB || state == State.FNOB) && cls == CharClass.MUL_DIV) {
            return entry(ErrorType.MUL_DIV_AFTER_OPEN_BRACKET, "операція * або / одразу після відкриваючої дужки");
        }

        // --- Порожні "звичайні" дужки (не виклик функції) ---
        if (state == State.OB && cls == CharClass.CLOSE_BRACKET) {
            return entry(ErrorType.EMPTY_BRACKETS, "порожні дужки");
        }

        // --- Відсутність операції між закритою дужкою і наступним операндом ---
        if (state == State.CB && OPERAND_START.contains(cls)) {
            return entry(ErrorType.MISSING_OPERATOR_BETWEEN,
                    "відсутня операція перед наступним операндом");
        }

        return entry(ErrorType.UNKNOWN_SYMBOL, "неочікуваний символ");
    }

    public String chooseOperandWord(Optional<Token> lastToken) {
        if (lastToken.isEmpty()) {
            return "очікувався операнд";
        }
        TokenType type = lastToken.get().type();
        if (type == TokenType.CONST) {
            return "очікувалась змінна";
        }
        if (type == TokenType.VAR || type == TokenType.FUNCTION) {
            return "очікувалось число";
        }
        return "очікувався операнд";
    }

    private Map.Entry<ErrorType, String> entry(ErrorType type, String message) {
        return new AbstractMap.SimpleEntry<>(type, message);
    }
}
