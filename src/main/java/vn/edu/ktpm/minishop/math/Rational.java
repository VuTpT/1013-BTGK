package vn.edu.ktpm.minishop.math;

/**
 * FR-06 - Phan so, giu nguyen API cua bai W11 va bo sung rang buoc chuan hoa:
 * <ul>
 *   <li>Mau so luon &gt; 0 (dau am duoc day len tu so)</li>
 *   <li>Luon o dang toi gian</li>
 * </ul>
 *
 * <p>Hai ham {@code divide} va {@code GCD} duoc dung lam vi du cho ky thuat kiem thu
 * <b>hop trang</b> (luu do, do phuc tap Cyclomatic) - xem docs/TestDesign-Techniques.md.</p>
 */
public class Rational {

    private long numerator;
    private long denominator;

    /** Phan so mac dinh 0/1. */
    public Rational() {
        this.numerator = 0;
        this.denominator = 1;
    }

    public Rational(long numerator, long denominator) throws Illegal {
        if (denominator == 0) {
            throw new Illegal("Mau so khong duoc bang 0");
        }
        this.numerator = numerator;
        this.denominator = denominator;
        normalize();
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public void add(Rational x) {
        numerator = (numerator * x.denominator) + (x.numerator * denominator);
        denominator = denominator * x.denominator;
        normalize();
    }

    public void subtract(Rational x) {
        numerator = (numerator * x.denominator) - (x.numerator * denominator);
        denominator = denominator * x.denominator;
        normalize();
    }

    public void multiply(Rational x) {
        numerator = numerator * x.numerator;
        denominator = denominator * x.denominator;
        normalize();
    }

    /** @throws Illegal khi chia cho phan so 0. */
    public void divide(Rational x) throws Illegal {
        if (x.numerator == 0) {
            throw new Illegal("Khong the chia cho phan so 0");
        }
        numerator = numerator * x.denominator;
        denominator = denominator * x.numerator;
        normalize();
    }

    /** Dua ve dang toi gian, mau so duong. */
    private void normalize() {
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        long gcd = GCD(Math.abs(numerator), denominator);
        numerator /= gcd;
        denominator /= gcd;
    }

    /** Uoc chung lon nhat (thuat toan Euclid de quy). Yeu cau {@code b > 0}. */
    private long GCD(long a, long b) {
        if (a % b == 0) {
            return b;
        }
        return GCD(b, a % b);
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (!(x instanceof Rational)) {
            return false;
        }
        Rational other = (Rational) x;
        return numerator == other.numerator && denominator == other.denominator;
    }

    @Override
    public int hashCode() {
        return (int) (numerator * 31 + denominator);
    }

    /** @return so am / 0 / so duong tuong ung this &lt; x, this = x, this &gt; x. */
    public long compareTo(Object x) {
        if (!(x instanceof Rational)) {
            throw new IllegalArgumentException("Chi so sanh duoc voi Rational");
        }
        Rational other = (Rational) x;
        return numerator * other.denominator - other.numerator * denominator;
    }

    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator);
        }
        return numerator + "/" + denominator;
    }
}
