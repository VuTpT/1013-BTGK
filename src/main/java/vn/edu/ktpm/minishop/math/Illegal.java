package vn.edu.ktpm.minishop.math;

/**
 * Ngoai le cua lop {@link Rational} - giu nguyen thiet ke cua bai W11.
 */
public class Illegal extends Exception {

    private static final long serialVersionUID = 1L;

    private final String reason;

    public Illegal(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
