package lab2.model;

public sealed interface Factor permits Leaf, Func, Sum {
    int pos();
}
