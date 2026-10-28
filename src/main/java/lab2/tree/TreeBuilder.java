package lab2.tree;

import lab2.model.Call;
import lab2.model.Const;
import lab2.model.Factor;
import lab2.model.Fraction;
import lab2.model.Func;
import lab2.model.Leaf;
import lab2.model.MulOp;
import lab2.model.OpFactor;
import lab2.model.Sign;
import lab2.model.Sum;
import lab2.model.Term;
import lab2.model.Tree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class TreeBuilder {

    private TreeBuilder() {
    }
    public static Tree buildSum(Sum sum) {
        List<Sign> signs = new ArrayList<>();
        List<Tree> trees = new ArrayList<>();
        for (Term t : sum.terms()) {
            signs.add(t.sign());
            trees.add(buildTerm(t));
        }
        return Balancer.balance(signs, trees, '+', '-', Fraction.ZERO);
    }
    static Tree buildTerm(Term t) {
        Fraction coef = t.coef();
        if (t.factors().isEmpty() && coef.isFiniteDecimal()) {
            return Const.of(coef);
        }

        List<Sign> signs = new ArrayList<>();
        List<Tree> trees = new ArrayList<>();
        BigInteger num = coef.numerator();
        BigInteger den = coef.denominator();
        if (!num.equals(BigInteger.ONE) || t.factors().isEmpty()) {
            signs.add(Sign.PLUS);
            trees.add(Const.of(Fraction.of(num, BigInteger.ONE)));
        }
        for (OpFactor f : t.factors()) {
            signs.add(f.op() == MulOp.MUL ? Sign.PLUS : Sign.MINUS);
            trees.add(buildFactor(f.factor()));
        }
        if (!den.equals(BigInteger.ONE)) {
            signs.add(Sign.MINUS);
            trees.add(Const.of(Fraction.of(den, BigInteger.ONE)));
        }
        return Balancer.balance(signs, trees, '*', '/', Fraction.ONE);
    }
    private static Tree buildFactor(Factor f) {
        if (f instanceof Leaf leaf) {
            return leaf;
        }
        if (f instanceof Sum s) {
            return buildSum(s);
        }
        Func fn = (Func) f;
        List<Tree> args = new ArrayList<>();
        for (Sum arg : fn.args()) {
            args.add(buildSum(arg));
        }
        return new Call(fn.name(), args);
    }
}
