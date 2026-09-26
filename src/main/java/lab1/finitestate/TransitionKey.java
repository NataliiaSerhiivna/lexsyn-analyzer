package lab1.finitestate;

import lab1.model.CharClass;
import lab1.model.State;

public record TransitionKey(State from, CharClass cls) {
}
