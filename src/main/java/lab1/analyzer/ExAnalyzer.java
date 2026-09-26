package lab1.analyzer;

import lab1.bracket.BracketChecker;
import lab1.error.ErrorExplainer;
import lab1.finitestate.TransitionTable;
import lab1.lexer.CharClassifier;
import lab1.lexer.TokenBuilder;
import lab1.model.AnalysisError;
import lab1.model.AnalysisResult;
import lab1.model.CharClass;
import lab1.model.ErrorType;
import lab1.model.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExAnalyzer {

    private final TransitionTable table = new TransitionTable();
    private final ErrorExplainer explainer = new ErrorExplainer();

    public AnalysisResult analyze(String expression) {
        BracketChecker brackets = new BracketChecker();
        TokenBuilder tokenBuilder = new TokenBuilder();
        List<AnalysisError> fsmErrors = new ArrayList<>();

        State state = State.S;
        State lastErrorState = null;

        int i = 0;
        int n = expression.length();
        while (i < n) {
            char ch = expression.charAt(i);
            CharClass cls = CharClassifier.classify(ch);

            if (cls == CharClass.SPACE) {
                i++;
                continue;
            }

            if (cls == CharClass.UNKNOWN) {
                fsmErrors.add(new AnalysisError(i, ErrorType.UNKNOWN_SYMBOL, "неможливий символ '" + ch + "'"));
                tokenBuilder.forceFlush(state, i);
                i++;
                continue;
            }

            if (cls == CharClass.OPEN_BRACKET || cls == CharClass.CLOSE_BRACKET) {
                boolean consumed = brackets.process(ch, i, state == State.V_FN);
                if (consumed) {
                    i++;
                    continue;
                }
            }
            if (cls == CharClass.COMMA && !brackets.insideFunctionCall()) {
                fsmErrors.add(new AnalysisError(i, ErrorType.COMMA_OUTSIDE_FUNCTION,
                        "кома не у функції"));
                i++;
                continue;
            }
            Optional<State> next = table.getNextState(state, cls);

            if (next.isEmpty()) {
                if (state != lastErrorState) {
                    Map.Entry<ErrorType, String> explanation =
                            explainer.explain(state, cls, tokenBuilder.lastOperandToken());
                    fsmErrors.add(new AnalysisError(i, explanation.getKey(), explanation.getValue()));
                    lastErrorState = state;
                }
                if (state == State.AS && cls == CharClass.CLOSE_BRACKET) {
                    state = State.CB;
                }
                i++;
                continue;
            }

            State nextState = next.get();
            tokenBuilder.onSuccessfulTransition(state, nextState, ch, i);
            state = nextState;
            lastErrorState = null;
            i++;
        }
        tokenBuilder.forceFlush(state, n);

        brackets.finalizeCheck(n);

        List<AnalysisError> allErrors = new ArrayList<>(fsmErrors);
        allErrors.addAll(brackets.getErrors());

        if (!table.isAccepting(state)) {
            allErrors.add(buildUnterminatedError(state, tokenBuilder, n));
        }
        allErrors.sort((a, b) -> Integer.compare(a.position(), b.position()));
        boolean valid = allErrors.isEmpty();

        return new AnalysisResult(
                expression,
                tokenBuilder.getTokens(),
                allErrors,
                brackets.getMatchedPairs(),
                valid
        );
    }

    private AnalysisError buildUnterminatedError(State state, TokenBuilder tokenBuilder, int endPos) {
        String reason;
        boolean appendOperandWord;
        if (state == State.OP) {
            reason = "після оператора";
            appendOperandWord = true;
        } else if (state == State.AS) {
            reason = "після коми, очікувався аргумент функції";
            appendOperandWord = false;
        } else if (state == State.OB || state == State.FNOB) {
            reason = "після відкриваючої дужки";
            appendOperandWord = false;
        } else if (state == State.CD) {
            reason = "одразу після десяткової крапки";
            appendOperandWord = false;
        } else if (state == State.S) {
            reason = "- вираз порожній";
            appendOperandWord = false;
        } else {
            reason = "неочікувано";
            appendOperandWord = false;
        }

        String message = "вираз не завершений " + reason;
        if (appendOperandWord) {
            message += ", " + explainer.chooseOperandWord(tokenBuilder.lastOperandToken());
        }
        return new AnalysisError(endPos, ErrorType.UNTERMINATED_EXPRESSION, message);
    }
}
