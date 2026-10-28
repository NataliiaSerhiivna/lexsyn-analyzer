package lab2.model;

import java.util.List;

public record Func(String name, List<Sum> args, String src, int pos) implements Factor {

    public Func {
        args = List.copyOf(args);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append('(');
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(args.get(i));
        }
        return sb.append(')').toString();
    }
}
