package lab2.model;

public record Var(String name, int pos) implements Leaf {

    @Override
    public String toString() {
        return name;
    }
}
