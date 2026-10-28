package lab2.transform;

public record DivisionByZero(int position, String term) {
    @Override
    public String toString() {
        return "помилка на позиції " + position + ": ділення на 0";
    }
}
