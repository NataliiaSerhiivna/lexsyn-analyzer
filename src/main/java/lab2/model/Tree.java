package lab2.model;

public sealed interface Tree permits Leaf, Call, Bin {

    default int height() {
        return 0;
    }
}
