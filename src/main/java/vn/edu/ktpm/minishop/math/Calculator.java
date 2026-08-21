package vn.edu.ktpm.minishop.math;

/**
 * FR-06 - Lop Calculator theo yeu cau bai W8.
 */
public class Calculator {

    private double a;
    private double b;

    public Calculator() {
    }

    public Calculator(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double add() {
        return a + b;
    }

    public double sub() {
        return a - b;
    }

    public double mul() {
        return a * b;
    }

    /** @throws ArithmeticException khi chia cho 0. */
    public double div() {
        if (b == 0) {
            throw new ArithmeticException("Khong the chia cho 0");
        }
        return a / b;
    }
}
