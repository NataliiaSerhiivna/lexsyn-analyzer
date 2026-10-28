package lab2.model;

public enum Sign {
    PLUS('+'),
    MINUS('-');

    private final char symbol;

    Sign(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }

    public Sign negate() {
        return this == PLUS ? MINUS : PLUS;
    }

    public Sign times(Sign other) {
        return this == other ? PLUS : MINUS;
    }
}
