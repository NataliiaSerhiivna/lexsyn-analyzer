package lab2.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class Fraction implements Comparable<Fraction> {

    public static final Fraction ZERO = new Fraction(BigInteger.ZERO, BigInteger.ONE);
    public static final Fraction ONE = new Fraction(BigInteger.ONE, BigInteger.ONE);
    private final BigInteger num;
    private final BigInteger den;
    private Fraction(BigInteger num, BigInteger den) {
        this.num = num;
        this.den = den;
    }

    public static Fraction of(BigInteger num, BigInteger den) {
        if (den.signum() == 0) {
            throw new ArithmeticException("знаменник дорівнює 0");
        }
        if (den.signum() < 0) {
            num = num.negate();
            den = den.negate();
        }
        BigInteger gcd = num.gcd(den);
        if (gcd.signum() != 0 && !gcd.equals(BigInteger.ONE)) {
            num = num.divide(gcd);
            den = den.divide(gcd);
        }
        if (num.signum() == 0) {
            den = BigInteger.ONE;
        }
        return new Fraction(num, den);
    }
    public static Fraction of(long value) {
        return of(BigInteger.valueOf(value), BigInteger.ONE);
    }

    public static Fraction parse(String text) {
        BigDecimal d = new BigDecimal(text);
        if (d.scale() <= 0) {
            return of(d.toBigIntegerExact(), BigInteger.ONE);
        }
        return of(d.unscaledValue(), BigInteger.TEN.pow(d.scale()));
    }

    public Fraction add(Fraction o) {
        return of(num.multiply(o.den).add(o.num.multiply(den)), den.multiply(o.den));
    }

    public Fraction subtract(Fraction o) {
        return add(o.negate());
    }

    public Fraction multiply(Fraction o) {
        return of(num.multiply(o.num), den.multiply(o.den));
    }

    public Fraction divide(Fraction o) {
        return of(num.multiply(o.den), den.multiply(o.num));
    }

    public Fraction negate() {
        return new Fraction(num.negate(), den);
    }

    public Fraction abs() {
        return signum() < 0 ? negate() : this;
    }

    public int signum() {
        return num.signum();
    }

    public boolean isZero() {
        return num.signum() == 0;
    }

    public boolean isOne() {
        return num.equals(BigInteger.ONE) && den.equals(BigInteger.ONE);
    }

    public boolean isInteger() {
        return den.equals(BigInteger.ONE);
    }

    public BigInteger numerator() {
        return num;
    }

    public BigInteger denominator() {
        return den;
    }

    public boolean isFiniteDecimal() {
        BigInteger d = den;
        BigInteger two = BigInteger.TWO;
        BigInteger five = BigInteger.valueOf(5);
        while (d.mod(two).signum() == 0) {
            d = d.divide(two);
        }
        while (d.mod(five).signum() == 0) {
            d = d.divide(five);
        }
        return d.equals(BigInteger.ONE);
    }

    @Override
    public int compareTo(Fraction o) {
        return num.multiply(o.den).compareTo(o.num.multiply(den));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Fraction f)) {
            return false;
        }
        return num.equals(f.num) && den.equals(f.den);
    }

    @Override
    public int hashCode() {
        return num.hashCode() * 31 + den.hashCode();
    }

    @Override
    public String toString() {
        if (isInteger()) {
            return num.toString();
        }
        if (isFiniteDecimal()) {
            return new BigDecimal(num).divide(new BigDecimal(den)).stripTrailingZeros().toPlainString();
        }
        return num + "/" + den;
    }
}
