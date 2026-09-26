package lab1.model;
public record Token(TokenType type, String text, int startPos) {

    @Override
    public String toString() {
        return type + "(" + text + ")";
    }
}
