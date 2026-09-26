package lab1.finitestate;

import lab1.model.CharClass;
import lab1.model.State;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TransitionTable {

    private final Map<TransitionKey, State> table = new HashMap<>();
    private static final Set<State> ACCEPTING = EnumSet.of(State.C, State.CDC, State.CB, State.V_FN);
    public TransitionTable() {
        buildTable();
    }
    private void rule(State from, CharClass cls, State to) {
        table.put(new TransitionKey(from, cls), to);
    }

    private void buildTable() {
        //INVALID_START
        rule(State.S, CharClass.DIGIT, State.C);
        rule(State.S, CharClass.LETTER, State.V_FN);
        rule(State.S, CharClass.OPEN_BRACKET, State.OB);
        rule(State.S, CharClass.PLUS_MINUS, State.OP);

        //DOUBLE_OPERATOR and MISSING_OPERAND.
        rule(State.OP, CharClass.DIGIT, State.C);
        rule(State.OP, CharClass.LETTER, State.V_FN);
        rule(State.OP, CharClass.OPEN_BRACKET, State.OB);

        //rule bracket
        for (State st : new State[]{State.OB, State.FNOB}) {
            rule(st, CharClass.DIGIT, State.C);
            rule(st, CharClass.LETTER, State.V_FN);
            rule(st, CharClass.PLUS_MINUS, State.OP);
            rule(st, CharClass.OPEN_BRACKET, State.OB);
        }
        rule(State.FNOB, CharClass.CLOSE_BRACKET, State.CB);

        //variable/function name
        rule(State.V_FN, CharClass.LETTER, State.V_FN);
        rule(State.V_FN, CharClass.DIGIT, State.V_FN);
        rule(State.V_FN, CharClass.OPEN_BRACKET, State.FNOB);
        rule(State.V_FN, CharClass.PLUS_MINUS, State.OP);
        rule(State.V_FN, CharClass.MUL_DIV, State.OP);
        rule(State.V_FN, CharClass.CLOSE_BRACKET, State.CB);

        //C
        rule(State.C, CharClass.DIGIT, State.C);
        rule(State.C, CharClass.DOT, State.CD);
        rule(State.C, CharClass.PLUS_MINUS, State.OP);
        rule(State.C, CharClass.MUL_DIV, State.OP);
        rule(State.C, CharClass.CLOSE_BRACKET, State.CB);

        //MISSING_DIGIT_AFTER_DOT fractional part
        rule(State.CD, CharClass.DIGIT, State.CDC);
        rule(State.CDC, CharClass.DIGIT, State.CDC);
        rule(State.CDC, CharClass.PLUS_MINUS, State.OP);
        rule(State.CDC, CharClass.MUL_DIV, State.OP);
        rule(State.CDC, CharClass.CLOSE_BRACKET, State.CB);


        rule(State.CB, CharClass.CLOSE_BRACKET, State.CB);
        rule(State.CB, CharClass.PLUS_MINUS, State.OP);
        rule(State.CB, CharClass.MUL_DIV, State.OP);

        //functional comma
        for (State st : new State[]{State.V_FN, State.C, State.CDC, State.CB}) {
            rule(st, CharClass.COMMA, State.AS);
        }
        rule(State.AS, CharClass.DIGIT, State.C);
        rule(State.AS, CharClass.LETTER, State.V_FN);
        rule(State.AS, CharClass.PLUS_MINUS, State.OP);
        rule(State.AS, CharClass.OPEN_BRACKET, State.OB);
    }

    public Optional<State> getNextState(State current, CharClass cls) {
        return Optional.ofNullable(table.get(new TransitionKey(current, cls)));
    }

    public boolean isAccepting(State state) {
        return ACCEPTING.contains(state);
    }
}
