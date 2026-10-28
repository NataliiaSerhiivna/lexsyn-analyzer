package lab2.model;

import java.util.List;
public record Call(String name, List<Tree> args) implements Tree {
    public Call {
        args = List.copyOf(args);
    }
}
