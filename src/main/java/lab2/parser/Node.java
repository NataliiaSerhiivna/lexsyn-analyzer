package lab2.parser;

import java.util.List;

public sealed interface Node permits Node.Num, Node.Ident, Node.Call, Node.BinOp {

    int start();

    int end();


    Node withSpan(int start, int end);

    //Число
    record Num(String text, boolean synthetic, int start, int end) implements Node {
        @Override
        public Node withSpan(int s, int e) {
            return new Num(text, synthetic, s, e);
        }

        @Override
        public String toString() {
            return text;
        }
    }

    // Змінна
    record Ident(String name, int start, int end) implements Node {
        @Override
        public Node withSpan(int s, int e) {
            return new Ident(name, s, e);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    //Виклик функції з аргументами
    record Call(String name, List<Node> args, int start, int end) implements Node {
        public Call {
            args = List.copyOf(args);
        }

        @Override
        public Node withSpan(int s, int e) {
            return new Call(name, args, s, e);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(name).append('(');
            for (int i = 0; i < args.size(); i++) {
                sb.append(i > 0 ? ", " : "").append(args.get(i));
            }
            return sb.append(')').toString();
        }
    }

    // Бінарна операція
    record BinOp(char op, Node left, Node right, int start, int end) implements Node {
        @Override
        public Node withSpan(int s, int e) {
            return new BinOp(op, left, right, s, e);
        }
        @Override
        public String toString() {
            return "(" + left + " " + op + " " + right + ")";
        }
    }
}
