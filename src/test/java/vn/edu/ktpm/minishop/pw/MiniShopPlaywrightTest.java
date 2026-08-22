package vn.edu.ktpm.minishop.pw;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import vn.edu.ktpm.minishop.support.LocalWebServer;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-PW-001..003 - <b>Doi chung voi Selenium</b> (ban rut gon v2: 3 test case, v1 co 5).
 *
 * <p>Ba test case duoi day tuong ung 1-1 voi TC-WEB-001, TC-WEB-005 va TC-WEB-010 cua tang
 * Selenium: cung SUT, cung du lieu, cung khang dinh. Khac biet duy nhat la cong cu, nho do
 * bang so sanh trong docs/Report.md moi co y nghia.</p>
 *
 * <p>Diem can chi ra trong video:</p>
 * <ul>
 *   <li>Playwright <b>tu dong cho</b> phan tu san sang - khong can WebDriverWait/ExpectedConditions.</li>
 *   <li>Mot dong {@code page.fill()} thay cho {@code findElement().clear() + sendKeys()}.</li>
 *   <li><b>Trace viewer</b>: moi test deu sinh tap tin trace xem lai duoc tung buoc bang lenh
 *       {@code npx playwright show-trace target/traces/&lt;ten-test&gt;.zip}.</li>
 * </ul>
 *
 * <p>Ghi chu ky thuat: lop nay dung <b>JUnit 5</b> (v1 dung TestNG). Sau khi gop ve mot project,
 * chi con tang Selenium dung TestNG - ranh gioi "JUnit 5 cho unit/Playwright, TestNG cho UI"
 * vi the van ro ma cau hinh lai don gian han.</p>
 */
@Epic("Tang 2b - Playwright (doi chung)")
@Feature("So sanh Selenium vs Playwright")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MiniShopPlaywrightTest {

    private LocalWebServer server;
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeAll
    void setUpClass() throws Exception {
        server = new LocalWebServer(LocalWebServer.defaultRoot());
        server.start();

        playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("headless", "true")));
        String channel = System.getProperty("pw.channel", "");
        if (!channel.isBlank()) {
            options.setChannel(channel);      // dung Chrome da cai san tren may
        }
        browser = playwright.chromium().launch(options);
    }

    @AfterAll
    void tearDownClass() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        if (server != null) {
            server.stop();
        }
    }

    @BeforeEach
    void setUp() {
        page = browser.newContext().newPage();
        page.context().tracing().start(new Tracing.StartOptions()
                .setScreenshots(true).setSnapshots(true).setSources(true));
        page.navigate(server.baseUrl());
        page.evaluate("localStorage.clear()");
        page.reload();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        Path trace = Paths.get("target", "traces", testInfo.getTestMethod().orElseThrow().getName() + ".zip");
        page.context().tracing().stop(new Tracing.StopOptions().setPath(trace));
        page.context().close();
    }

    // ------------------------------------------------------------------ tien ich
    private void login(String username, String password) {
        page.fill("[data-testid='username']", username);
        page.fill("[data-testid='password']", password);
        page.click("[data-testid='login-button']");
    }

    private void addToCart(String code, int quantity) {
        page.fill("[data-testid='qty-" + code + "']", String.valueOf(quantity));
        page.click("[data-testid='add-" + code + "']");
    }

    private long money(String selector) {
        return Long.parseLong(page.textContent(selector).replaceAll("[^0-9]", ""));
    }

    // ------------------------------------------------------------------ test case
    @Test
    @DisplayName("TC-PW-001 (= TC-WEB-001) Dang nhap hop le")
    @TmsLink("KTPM-101")
    void tcPw001_loginValid() {
        login("standard_user", "secret_sauce");

        assertTrue(page.isVisible("[data-testid='page-products']"));
        assertEquals("standard_user", page.textContent("[data-testid='current-user']"));
    }

    @Test
    @DisplayName("TC-PW-002 (= TC-WEB-005) Sai 3 lan lien tiep -> khoa tai khoan")
    @TmsLink("KTPM-102")
    @TmsLink("KTPM-103")
    @TmsLink("KTPM-105")
    void tcPw002_lockAfterThreeFailures() {
        login("standard_user", "sai1");
        assertTrue(page.textContent("[data-testid='login-error']").contains("2 lần thử"));

        login("standard_user", "sai2");
        assertTrue(page.textContent("[data-testid='login-error']").contains("1 lần thử"));

        login("standard_user", "sai3");
        assertTrue(page.textContent("[data-testid='login-error']").contains("khóa"));

        login("standard_user", "secret_sauce");
        assertTrue(page.textContent("[data-testid='login-error']").contains("đang bị khóa"));
    }

    @Test
    @DisplayName("TC-PW-003 (= TC-WEB-010) Them san pham vao gio")
    @TmsLink("KTPM-112")
    void tcPw003_addToCart() {
        login("standard_user", "secret_sauce");
        addToCart("P01", 2);

        assertEquals("2", page.textContent("[data-testid='cart-count']"));

        page.click("[data-testid='nav-cart']");
        assertEquals("2", page.textContent("[data-testid='cart-qty-P01']"));
        assertEquals(300_000L, money("[data-testid='cart-line-total-P01']"));
    }
}
