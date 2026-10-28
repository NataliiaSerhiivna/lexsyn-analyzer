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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Simplifier {

    private static final int MAX_PASSES = 10;

    private final Map<Integer, DivisionByZero> errors = new TreeMap<>();

    public Sum simplify(Sum sum) {
        for (int pass = 0; pass < MAX_PASSES; pass++) {
            String before = sum.toString();
            sum = simplifySum(sum);
            if (sum.toString().equals(before)) {
                break;
            }
        }
        return sum;
    }

    public List<DivisionByZero> errors() {
        return new ArrayList<>(errors.values());
    }

    private Sum simplifySum(Sum sum) {
        List<Term> terms = new ArrayList<>();
        for (Term t : sum.terms()) {
            Term r = simplifyTerm(t);
            if (r == null) {
                continue;
            }
            Sum inner = singleBracket(r);
            if (inner != null) {
                for (Term u : inner.terms()) {
                    terms.add(new Term(r.sign().times(u.sign()), u.coef(), u.factors(), u.src(), u.pos()));
                }
            } else {
                terms.add(r);
            }
        }

        Fraction constant = Fraction.ZERO;
        Map<String, Group> groups = new LinkedHashMap<>();
        for (Term t : terms) {
            if (t.factors().isEmpty()) {
                constant = constant.add(t.signedCoef());
                continue;
            }
            groups.computeIfAbsent(key(t.factors()), k -> new Group(t)).add(t.signedCoef());
        }

        List<Term> result = new ArrayList<>();
        for (Group g : groups.values()) {
            if (!g.total.isZero()) {
                result.add(Term.fromSigned(g.total, g.first.factors(), g.first.src(), g.first.pos()));
            }
        }
        if (!constant.isZero()) {
            result.add(Term.fromSigned(constant, List.of(), constant.toString(), -1));
        }
        return new Sum(result, sum.src(), sum.pos());
    }

    private static final class Group {
        final Term first;
        Fraction total = Fraction.ZERO;

        Group(Term first) {
            this.first = first;
        }

        void add(Fraction value) {
            total = total.add(value);
        }
    }

    private static String key(List<OpFactor> factors) {
        List<String> parts = new ArrayList<>();
        for (OpFactor f : factors) {
            parts.add(f.toString());
        }
        parts.sort(null);
        return String.join(",", parts);
    }

    private static Sum singleBracket(Term t) {
        if (t.coef().isOne() && t.factors().size() == 1) {
            OpFactor f = t.factors().get(0);
            if (f.op() == MulOp.MUL && f.factor() instanceof Sum s) {
                return s;
            }
        }
        return null;
    }

    private Term simplifyTerm(Term t) {
        Sign sign = t.sign();
        Fraction coef = t.coef();
        List<OpFactor> factors = new ArrayList<>();
        boolean hasError = false;

        for (OpFactor of : t.factors()) {
            MulOp op = of.op();
            Factor f = of.factor();

            if (f instanceof Sum bracket) {
                Sum s = simplifySum(bracket);
                if (s.terms().isEmpty()) {
                    f = new Const(Fraction.ZERO, bracket.pos());
                } else if (s.terms().size() == 1 && s.terms().get(0).factors().isEmpty()) {
                    f = new Const(s.terms().get(0).signedCoef(), bracket.pos());
                } else if (s.terms().size() == 1) {
                    Term u = s.terms().get(0);
                    sign = sign.times(u.sign());
                    coef = op == MulOp.MUL ? coef.multiply(u.coef()) : coef.divide(u.coef());
                    for (OpFactor uf : u.factors()) {
                        factors.add(new OpFactor(op.apply(uf.op()), uf.factor()));
                    }
                    continue;
                } else {
                    factors.add(new OpFactor(op, s));
                    continue;
                }
            }

            if (f instanceof Const c) {
                Fraction v = c.value();
                if (op == MulOp.DIV && v.isZero()) {
                    errors.put(c.pos(), new DivisionByZero(c.pos(), t.src()));
                    hasError = true;
                    continue;
                }
                if (v.signum() < 0) {
                    sign = sign.negate();
                    v = v.negate();
                }
                coef = op == MulOp.MUL ? coef.multiply(v) : coef.divide(v);
                continue;
            }

            if (f instanceof Func fn) {
                List<Sum> args = new ArrayList<>();
                for (Sum arg : fn.args()) {
                    args.add(simplifySum(arg));
                }
                factors.add(new OpFactor(op, new Func(fn.name(), args, fn.src(), fn.pos())));
                continue;
            }

            factors.add(new OpFactor(op, (Var) f));
        }

        if (hasError || coef.isZero()) {
            return null;
        }
        return new Term(sign, coef, factors, t.src(), t.pos());
    }
}
