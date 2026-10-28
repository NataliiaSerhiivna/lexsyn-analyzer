package lab2.transform;

import lab2.model.Const;
import lab2.model.Factor;
import lab2.model.Fraction;
import lab2.model.Func;
import lab2.model.MulOp;
import lab2.model.OpFactor;
import lab2.model.Sign;
import lab2.model.Sum;
import lab2.model.Term;
import lab2.model.Var;
import lab2.parser.Node;

import java.util.ArrayList;
import java.util.List;

public class Flattener {
    private final String expr;

    public Flattener(String expr) {
        this.expr = expr;
    }

    public Sum flatten(Node root) {
        return toSum(root);
    }

    private Sum toSum(Node node) {
        List<Term> terms = new ArrayList<>();
        flattenSum(node, Sign.PLUS, terms);
        return new Sum(terms, text(node), node.start());
    }

    private void flattenSum(Node node, Sign sign, List<Term> out) {
        if (node instanceof Node.BinOp b && b.op() == '+') {
            flattenSum(b.left(), sign, out);
            flattenSum(b.right(), sign, out);
        } else if (node instanceof Node.BinOp b && b.op() == '-') {
            flattenSum(b.left(), sign, out);
            flattenSum(b.right(), sign.negate(), out);
        } else {
            List<OpFactor> factors = new ArrayList<>();
            flattenProduct(node, MulOp.MUL, factors);
            out.add(new Term(sign, Fraction.ONE, factors, text(node), node.start()));
        }
    }

    private void flattenProduct(Node node, MulOp op, List<OpFactor> out) {
        if (node instanceof Node.BinOp b && b.op() == '*') {
            flattenProduct(b.left(), op, out);
            flattenProduct(b.right(), op, out);
        } else if (node instanceof Node.BinOp b && b.op() == '/') {
            flattenProduct(b.left(), op, out);
            flattenProduct(b.right(), op.invert(), out);
        } else {
            out.add(new OpFactor(op, toFactor(node)));
        }
    }

    private Factor toFactor(Node node) {
        if (node instanceof Node.Num n) {
            return new Const(Fraction.parse(n.text()), n.start());
        }
        if (node instanceof Node.Ident id) {
            return new Var(id.name(), id.start());
        }
        if (node instanceof Node.Call c) {
            List<Sum> args = new ArrayList<>();
            for (Node arg : c.args()) {
                args.add(toSum(arg));
            }
            return new Func(c.name(), args, text(node), c.start());
        }
        return toSum(node);
    }

    private String text(Node node) {
        return expr.substring(node.start(), node.end());
    }
}
