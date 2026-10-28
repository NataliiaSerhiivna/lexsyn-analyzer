package lab2.model;

public record Const(Fraction value, int pos) implements Leaf {

    public static Const of(Fraction value) {
        return new Const(value, -1);
    }
    @Override
    public String toString() {
        return value.toString();
    }
}
