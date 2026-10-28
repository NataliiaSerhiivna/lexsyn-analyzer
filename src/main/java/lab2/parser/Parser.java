package lab2.parser;

import lab1.model.Token;
import lab1.model.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Node parse() {
        Node node = parseExpr();
        if (index != tokens.size()) {
            throw new IllegalStateException("зайвий токен " + tokens.get(index) + " на позиції " + tokens.get(index).startPos());
        }
        return node;
    }

    private Node parseExpr() {
        Node node;
        Token first = peek();
        if (first != null && first.type() == TokenType.UNARY_OPERATOR) {
            index++;
            Node term = parseTerm();
            if (first.text().equals("-")) {
                Node zero = new Node.Num("0", true, first.startPos(), first.startPos());
                node = new Node.BinOp('-', zero, term, first.startPos(), term.end());
            } else {
                node = term.withSpan(first.startPos(), term.end());
            }
        } else {
            node = parseTerm();
        }

        while (isOperator("+") || isOperator("-")) {
            char op = next().text().charAt(0);
            Node right = parseTerm();
            node = new Node.BinOp(op, node, right, node.start(), right.end());
        }
        return node;
    }

    private Node parseTerm() {
        Node node = parseFactor();
        while (isOperator("*") || isOperator("/")) {
            char op = next().text().charAt(0);
            Node right = parseFactor();
            node = new Node.BinOp(op, node, right, node.start(), right.end());
        }
        return node;
    }

    private Node parseFactor() {
        Token t = next();
        switch (t.type()) {
            case CONST:
                return new Node.Num(t.text(), false, t.startPos(), end(t));
            case VAR:
                return new Node.Ident(t.text(), t.startPos(), end(t));
            case FUNCTION:
                return parseCall(t);
            case OPEN_BRACKET: {
                Node inner = parseExpr();
                Token close = expect(TokenType.CLOSE_BRACKET);
                return inner.withSpan(t.startPos(), end(close));
            }
            default:
                throw new IllegalStateException("неочікуваний токен " + t + " на позиції " + t.startPos());
        }
    }

    private Node parseCall(Token name) {
        expect(TokenType.OPEN_BRACKET);
        List<Node> args = new ArrayList<>();
        if (peek() != null && peek().type() != TokenType.CLOSE_BRACKET) {
            args.add(parseExpr());
            while (peek() != null && peek().type() == TokenType.ARG_SEPARATOR) {
                index++;
                args.add(parseExpr());
            }
        }
        Token close = expect(TokenType.CLOSE_BRACKET);
        return new Node.Call(name.text(), args, name.startPos(), end(close));
    }

    private static int end(Token t) {
        return t.startPos() + t.text().length();
    }

    private Token peek() {
        return index < tokens.size() ? tokens.get(index) : null;
    }

    private Token next() {
        Token t = peek();
        if (t == null) {
            throw new IllegalStateException("неочікуваний кінець виразу");
        }
        index++;
        return t;
    }

    private Token expect(TokenType type) {
        Token t = next();
        if (t.type() != type) {
            throw new IllegalStateException("очікувався " + type + ", а отримано " + t + " на позиції " + t.startPos());
        }
        return t;
    }

    private boolean isOperator(String text) {
        Token t = peek();
        return t != null && t.type() == TokenType.OPERATOR && t.text().equals(text);
    }
}
