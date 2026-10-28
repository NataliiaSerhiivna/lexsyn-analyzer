package lab2.model;

public record Bin(char op, Tree left, Tree right, int height) implements Tree {
    public Bin(char op, Tree left, Tree right) {
        this(op, left, right, Math.max(left.height(), right.height()) + 1);
    }
}
