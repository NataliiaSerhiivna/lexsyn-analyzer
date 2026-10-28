package lab2.model;

import java.math.BigInteger;
import java.util.List;

public record Term(Sign sign, Fraction coef, List<OpFactor> factors, String src, int pos) {

    public Term {
        factors = List.copyOf(factors);
    }

    public Fraction signedCoef() {
        return sign == Sign.PLUS ? coef : coef.negate();
    }

    public static Term fromSigned(Fraction value, List<OpFactor> factors, String src, int pos) {
        Sign s = value.signum() < 0 ? Sign.MINUS : Sign.PLUS;
        return new Term(s, value.abs(), factors, src, pos);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(sign.symbol());
        if (factors.isEmpty()) {
            return sb.append(coef).toString();
        }
        boolean first = true;
        if (!coef.numerator().equals(BigInteger.ONE)) {
            sb.append(coef.numerator());
            first = false;
        }
        for (OpFactor f : factors) {
            String s = f.toString();
            if (first) {
                s = f.op() == MulOp.MUL ? s.substring(1) : "1" + s;
                first = false;
            }
            sb.append(s);
        }
        if (!coef.denominator().equals(BigInteger.ONE)) {
            sb.append('/').append(coef.denominator());
        }
        return sb.toString();
    }
}
