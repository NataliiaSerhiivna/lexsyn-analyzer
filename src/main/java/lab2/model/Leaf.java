package lab2.model;

public sealed interface Leaf extends Factor, Tree permits Var, Const {
}
