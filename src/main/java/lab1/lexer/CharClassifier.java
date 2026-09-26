package lab1.lexer;

import lab1.model.CharClass;

public final class CharClassifier {
    private CharClassifier() {
    }
    public static CharClass classify(char ch) {
        if (ch >= '0' && ch <= '9') {
            return CharClass.DIGIT;
        }
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
            return CharClass.LETTER;
        }
        if (ch == '.') {
            return CharClass.DOT;
        }
        if (ch == '+' || ch == '-') {
            return CharClass.PLUS_MINUS;
        }
        if (ch == '*' || ch == '/') {
            return CharClass.MUL_DIV;
        }
        if (ch == '(') {
            return CharClass.OPEN_BRACKET;
        }
        if (ch == ')') {
            return CharClass.CLOSE_BRACKET;
        }
        if (ch == ',') {
            return CharClass.COMMA;
        }
        if (ch == ' ' || ch == '\t') {
            return CharClass.SPACE;
        }
        return CharClass.UNKNOWN;
    }
}
