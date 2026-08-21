package vn.edu.ktpm.minishop.auth;

import java.time.Instant;

/**
 * Tai khoan nguoi dung cua MiniShop (FR-01).
 */
public class User {

    private final String username;
    private final String password;
    private final boolean vip;
    /** Bi khoa vinh vien boi quan tri vien. */
    private final boolean disabled;

    private int failedAttempts;
    private Instant lockedUntil;

    public User(String username, String password, boolean vip, boolean disabled) {
        this.username = username;
        this.password = password;
        this.vip = vip;
        this.disabled = disabled;
    }

    public static User standard(String username, String password) {
        return new User(username, password, false, false);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVip() {
        return vip;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}
