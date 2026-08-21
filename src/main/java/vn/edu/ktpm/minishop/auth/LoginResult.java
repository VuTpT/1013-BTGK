package vn.edu.ktpm.minishop.auth;

/**
 * Ket qua mot lan dang nhap (FR-01).
 */
public class LoginResult {

    public enum Status {
        /** Dang nhap thanh cong. */
        SUCCESS,
        /** Bo trong ten dang nhap hoac mat khau - KHONG tinh vao so lan sai. */
        EMPTY_INPUT,
        /** Sai ten dang nhap hoac mat khau, van con luot thu. */
        INVALID_CREDENTIAL,
        /** Tai khoan dang bi khoa tam (15 phut). */
        LOCKED,
        /** Tai khoan bi quan tri vien khoa vinh vien. */
        DISABLED
    }

    private final Status status;
    private final int remainingAttempts;
    private final long lockRemainingMinutes;

    private LoginResult(Status status, int remainingAttempts, long lockRemainingMinutes) {
        this.status = status;
        this.remainingAttempts = remainingAttempts;
        this.lockRemainingMinutes = lockRemainingMinutes;
    }

    static LoginResult success() {
        return new LoginResult(Status.SUCCESS, LoginService.MAX_FAILED_ATTEMPTS, 0);
    }

    static LoginResult emptyInput() {
        return new LoginResult(Status.EMPTY_INPUT, LoginService.MAX_FAILED_ATTEMPTS, 0);
    }

    static LoginResult invalid(int remainingAttempts) {
        return new LoginResult(Status.INVALID_CREDENTIAL, remainingAttempts, 0);
    }

    static LoginResult locked(long lockRemainingMinutes) {
        return new LoginResult(Status.LOCKED, 0, lockRemainingMinutes);
    }

    static LoginResult disabled() {
        return new LoginResult(Status.DISABLED, 0, 0);
    }

    public Status getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    /** So lan nhap sai con lai truoc khi bi khoa. */
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    /** So phut con lai cua lan khoa hien tai (lam tron len). */
    public long getLockRemainingMinutes() {
        return lockRemainingMinutes;
    }

    @Override
    public String toString() {
        return "LoginResult{" + status + ", remainingAttempts=" + remainingAttempts
                + ", lockRemainingMinutes=" + lockRemainingMinutes + '}';
    }
}
