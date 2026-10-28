package lab2.model;

import java.util.List;

public record Sum(List<Term> terms, String src, int pos) implements Factor {

    public Sum {
        terms = List.copyOf(terms);
    }

    @Override
    public String toString() {
        if (terms.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terms.size(); i++) {
            String t = terms.get(i).toString();
            sb.append(i == 0 && t.startsWith("+") ? t.substring(1) : t);
        }
        return sb.toString();
    }
}
