package lab1.bracket;

import lab1.model.AnalysisError;
import lab1.model.BracketEntry;
import lab1.model.BracketPair;
import lab1.model.ErrorType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BracketChecker {

    private final Deque<BracketEntry> stack = new ArrayDeque<>();
    private final List<BracketPair> matchedPairs = new ArrayList<>();
    private final List<AnalysisError> errors = new ArrayList<>();

    public boolean process(char ch, int pos, boolean functionCall) {
        if (ch == '(') {
            stack.push(new BracketEntry(pos, functionCall));
            return false;
        }
        if (ch == ')') {
            if (stack.isEmpty()) {
                errors.add(new AnalysisError(pos, ErrorType.EXTRA_CLOSING_BRACKET,
                        "зайва закриваюча дужка"));
                return true;
            }
            BracketEntry opening = stack.pop();
            matchedPairs.add(new BracketPair(opening.position(), pos));
            return false;
        }
        return false;
    }

    public void finalizeCheck(int endOfStringPos) {
        while (!stack.isEmpty()) {
            BracketEntry unmatched = stack.pop();
            errors.add(new AnalysisError(unmatched.position(), ErrorType.MISSING_CLOSING_BRACKET,
                    "відкриваюча дужка на позиції " + unmatched.position() + " не має пари"));
        }
    }
    public boolean insideFunctionCall() {
        return !stack.isEmpty() && stack.peek().functionCall();
    }
    public List<AnalysisError> getErrors() {
        return errors;
    }

    public List<BracketPair> getMatchedPairs() {
        return matchedPairs;
    }
}
