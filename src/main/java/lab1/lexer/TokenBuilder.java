package lab1.lexer;

import lab1.model.State;
import lab1.model.Token;
import lab1.model.TokenType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TokenBuilder {

    private static final Set<State> ACCUMULATING = EnumSet.of(State.C, State.CD, State.CDC, State.V_FN);
    private final StringBuilder buffer = new StringBuilder();
    private int bufferStart = -1;
    private final List<Token> tokens = new ArrayList<>();

    public void onSuccessfulTransition(State from, State to, char ch, int pos) {
        boolean isBuilding = ACCUMULATING.contains(to);
        boolean wasBuilding = ACCUMULATING.contains(from);

        if (isBuilding && !wasBuilding) {
            bufferStart = pos;
            buffer.setLength(0);
        }
        if (isBuilding) {
            buffer.append(ch);
        }
        if (wasBuilding && !isBuilding) {
            TokenType type = classifyIdentifier(from, to);
            flush(type, bufferStart);
        }

        if (to == State.OP || to == State.OB || to == State.FNOB || to == State.CB || to == State.AS) {
            TokenType type = classifySymbolToken(from, to);
            tokens.add(new Token(type, String.valueOf(ch), pos));
        }
    }
    private TokenType classifyIdentifier(State from, State to) {
        if (to == State.FNOB) {
            return TokenType.FUNCTION;
        }
        if (from == State.V_FN) {
            return TokenType.VAR;
        }
        return TokenType.CONST;
    }
    private TokenType classifySymbolToken(State from, State to) {
        if (to == State.OB || to == State.FNOB) {
            return TokenType.OPEN_BRACKET;
        }
        if (to == State.CB) {
            return TokenType.CLOSE_BRACKET;
        }
        if (to == State.AS) {
            return TokenType.ARG_SEPARATOR;
        }
        if (from == State.S || from == State.OB || from == State.FNOB || from == State.AS) {
            return TokenType.UNARY_OPERATOR;
        }
        return TokenType.OPERATOR;
    }

    private void flush(TokenType type, int startPos) {
        if (buffer.length() == 0) {
            return;
        }
        tokens.add(new Token(type, buffer.toString(), startPos));
        buffer.setLength(0);
    }

    public void forceFlush(State currentState, int posForFlush) {
        if (currentState == State.C || currentState == State.CDC) {
            flush(TokenType.CONST, bufferStart);
        } else if (currentState == State.V_FN) {
            flush(TokenType.VAR, bufferStart);
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public Optional<Token> lastToken() {
        if (tokens.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tokens.get(tokens.size() - 1));
    }

    public Optional<Token> lastOperandToken() {
        for (int idx = tokens.size() - 1; idx >= 0; idx--) {
            TokenType type = tokens.get(idx).type();
            if (type == TokenType.CONST || type == TokenType.VAR || type == TokenType.FUNCTION) {
                return Optional.of(tokens.get(idx));
            }
        }
        return Optional.empty();
    }
}
