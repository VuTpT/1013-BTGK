package vn.edu.ktpm.minishop.unit;

import vn.edu.ktpm.minishop.auth.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-030..039 - FR-01 Dang nhap.
 *
 * <p>Bo test nay minh hoa hai ky thuat cua mon hoc cung luc:</p>
 * <ul>
 *   <li><b>Kiem thu theo so do chuyen trang thai</b> (bai W6): moi test case phu mot phep
 *       chuyen trang thai trong so do o docs/TestDesign-Techniques.md muc 2.</li>
 *   <li><b>Gia lap doi tuong phu thuoc bang EasyMock</b> (bai W10): {@link UserRepository}
 *       duoc mock, khong can co so du lieu.</li>
 * </ul>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-01 Dang nhap")
class LoginServiceTest {

    private static final String USERNAME = "standard_user";
    private static final String PASSWORD = "secret_sauce";

    private UserRepository repository;
    private MutableClock clock;
    private LoginService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        clock = MutableClock.at("2026-08-16T08:00:00Z");
        service = new LoginService(repository, clock);
    }

    private User existingUser(int failedAttempts) {
        User user = User.standard(USERNAME, PASSWORD);
        user.setFailedAttempts(failedAttempts);
        return user;
    }

    @Test
    @DisplayName("TC-UNIT-030 [CHUA_DANG_NHAP -> DA_DANG_NHAP] Dang nhap dung")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    void loginSuccess() {
        expect(repository.findByUsername(USERNAME)).andReturn(existingUser(0));
        replay(repository);

        LoginResult result = service.login(USERNAME, PASSWORD);

        assertTrue(result.isSuccess());
        assertEquals(LoginResult.Status.SUCCESS, result.getStatus());
        verify(repository);
    }

    @ParameterizedTest(name = "TC-UNIT-031.{index}: sai lan {0} -> con {1} luot")
    @CsvSource({"1, 2", "2, 1"})
    @DisplayName("TC-UNIT-031 [CHUA_DANG_NHAP -> CHUA_DANG_NHAP] Sai duoi 3 lan, van cho nhap lai")
    void wrongPasswordUnderThreshold(int attemptNo, int expectedRemaining) {
        User user = existingUser(attemptNo - 1);
        expect(repository.findByUsername(USERNAME)).andReturn(user);
        repository.save(user);
        expectLastCall();
        replay(repository);

        LoginResult result = service.login(USERNAME, "sai_mat_khau");

        assertEquals(LoginResult.Status.INVALID_CREDENTIAL, result.getStatus());
        assertEquals(expectedRemaining, result.getRemainingAttempts());
        assertEquals(attemptNo, user.getFailedAttempts());
        verify(repository);
    }

    @Test
    @DisplayName("TC-UNIT-032 [CHUA_DANG_NHAP -> KHOA_TAM] Sai du 3 lan -> khoa 15 phut")
    @Severity(SeverityLevel.CRITICAL)
    void lockAfterThirdFailure() {
        User user = existingUser(2);
        expect(repository.findByUsername(USERNAME)).andReturn(user);
        repository.save(user);
        expectLastCall();
        replay(repository);

        LoginResult result = service.login(USERNAME, "sai_mat_khau");

        assertEquals(LoginResult.Status.LOCKED, result.getStatus());
        assertEquals(LoginService.LOCK_MINUTES, result.getLockRemainingMinutes());
        assertEquals(clock.instant().plusSeconds(15 * 60), user.getLockedUntil());
        verify(repository);
    }

    @Test
    @DisplayName("TC-UNIT-033 [KHOA_TAM] Dang bi khoa, nhap DUNG mat khau van bi tu choi")
    void correctPasswordWhileLocked() {
        User user = existingUser(3);
        user.setLockedUntil(clock.instant().plusSeconds(15 * 60));
        expect(repository.findByUsername(USERNAME)).andReturn(user);
        replay(repository);

        LoginResult result = service.login(USERNAME, PASSWORD);

        assertEquals(LoginResult.Status.LOCKED, result.getStatus());
        assertEquals(15, result.getLockRemainingMinutes());
        verify(repository);
    }

    @Test
    @DisplayName("TC-UNIT-035 [KHOA_TAM -> CHUA_DANG_NHAP] Du 15 phut -> tu dong mo khoa, reset bo dem")
    @Severity(SeverityLevel.CRITICAL)
    void autoUnlockAfterLockPeriod() {
        User user = existingUser(3);
        user.setLockedUntil(clock.instant().plusSeconds(15 * 60));
        expect(repository.findByUsername(USERNAME)).andReturn(user);
        repository.save(user);
        expectLastCall();
        replay(repository);

        clock.advanceMinutes(15);
        LoginResult result = service.login(USERNAME, PASSWORD);

        assertTrue(result.isSuccess());
        assertEquals(0, user.getFailedAttempts());
        assertEquals(null, user.getLockedUntil());
        verify(repository);
    }

    @Test
    @DisplayName("TC-UNIT-037 Tai khoan khong ton tai -> INVALID_CREDENTIAL, khong lo thong tin")
    void unknownUser() {
        expect(repository.findByUsername("khong_ton_tai")).andReturn(null);
        replay(repository);

        LoginResult result = service.login("khong_ton_tai", PASSWORD);

        assertEquals(LoginResult.Status.INVALID_CREDENTIAL, result.getStatus());
        assertEquals(LoginService.MAX_FAILED_ATTEMPTS, result.getRemainingAttempts());
        verify(repository);
    }

}
