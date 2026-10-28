package lab2.model;

public record OpFactor(MulOp op, Factor factor) {
    @Override
    public String toString() {
        String f = factor instanceof Sum ? "(" + factor + ")" : factor.toString();
        return op.symbol() + f;
    }
}
