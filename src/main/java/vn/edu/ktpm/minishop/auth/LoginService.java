package vn.edu.ktpm.minishop.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * FR-01 - Dang nhap voi co che khoa tam sau 3 lan sai.
 *
 * <p>May trang thai (xem so do trong docs/TestDesign-Techniques.md):</p>
 * <pre>
 *   CHUA_DANG_NHAP --sai(&lt;3)--&gt; CHUA_DANG_NHAP
 *   CHUA_DANG_NHAP --sai(lan 3)--&gt; KHOA_TAM --het 15 phut--&gt; CHUA_DANG_NHAP (reset bo dem)
 *   CHUA_DANG_NHAP --dung--&gt; DA_DANG_NHAP
 * </pre>
 *
 * <p>Nhan {@link Clock} qua constructor de test co the "tua thoi gian" (testability - muc 4.2
 * cua de bai), va nhan {@link UserRepository} de co the mock bang EasyMock.</p>
 */
public class LoginService {

    /** So lan nhap sai toi da truoc khi bi khoa. */
    public static final int MAX_FAILED_ATTEMPTS = 3;

    /** Thoi gian khoa tam tinh bang phut. */
    public static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final Clock clock;

    public LoginService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public LoginResult login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return LoginResult.emptyInput();
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            // Khong lo thong tin tai khoan co ton tai hay khong.
            return LoginResult.invalid(MAX_FAILED_ATTEMPTS);
        }
        if (user.isDisabled()) {
            return LoginResult.disabled();
        }

        Instant now = clock.instant();
        if (isLocked(user, now)) {
            return LoginResult.locked(remainingLockMinutes(user, now));
        }

        // Het han khoa -> tu dong mo lai va reset bo dem.
        if (user.getLockedUntil() != null) {
            user.setLockedUntil(null);
            user.setFailedAttempts(0);
            userRepository.save(user);
        }

        if (!user.getPassword().equals(password)) {
            return handleWrongPassword(user, now);
        }

        if (user.getFailedAttempts() != 0 || user.getLockedUntil() != null) {
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
        return LoginResult.success();
    }

    private LoginResult handleWrongPassword(User user, Instant now) {
        int failed = user.getFailedAttempts() + 1;
        user.setFailedAttempts(failed);
        if (failed >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(now.plus(Duration.ofMinutes(LOCK_MINUTES)));
            userRepository.save(user);
            return LoginResult.locked(LOCK_MINUTES);
        }
        userRepository.save(user);
        return LoginResult.invalid(MAX_FAILED_ATTEMPTS - failed);
    }

    private boolean isLocked(User user, Instant now) {
        return user.getLockedUntil() != null && now.isBefore(user.getLockedUntil());
    }

    private long remainingLockMinutes(User user, Instant now) {
        long seconds = Duration.between(now, user.getLockedUntil()).getSeconds();
        return (seconds + 59) / 60; // lam tron len
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
