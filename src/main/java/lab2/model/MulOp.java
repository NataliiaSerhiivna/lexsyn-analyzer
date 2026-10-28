package lab2.model;

public enum MulOp {
    MUL('*'),
    DIV('/');

    private final char symbol;

    MulOp(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }

    public MulOp invert() {
        return this == MUL ? DIV : MUL;
    }

    public MulOp apply(MulOp inner) {
        return this == MUL ? inner : inner.invert();
    }
}
