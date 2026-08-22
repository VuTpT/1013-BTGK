package vn.edu.ktpm.minishop.mobile;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import vn.edu.ktpm.minishop.support.LocalWebServer;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-MOB-001..002 - Kiem thu MiniShop tren Chrome cua Android Emulator
 * (ban rut gon v2: 2 test case, v1 co 3).
 *
 * <p><b>Dieu kien chay</b> - neu thieu, ca lop tu dong bi bo qua nho
 * {@link EnabledIfSystemProperty}, build van xanh:</p>
 * <ol>
 *   <li>Android Emulator dang chay (API 30+), da co Chrome</li>
 *   <li>Appium server 2.x dang chay ({@code appium}, da cai driver uiautomator2)</li>
 *   <li>Chay bang lenh: {@code mvn test -Dmobile.tests=true}</li>
 * </ol>
 *
 * <p>Emulator truy cap may that qua dia chi <b>10.0.2.2</b> thay cho 127.0.0.1 - day la chi tiet
 * ky thuat can neu ro trong video.</p>
 */
@Epic("Tang 3 - Mobile")
@Feature("MiniShop tren Android")
@EnabledIfSystemProperty(named = "mobile.tests", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MiniShopMobileTest {

    private LocalWebServer server;
    private AndroidDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setUp() throws Exception {
        server = new LocalWebServer(LocalWebServer.defaultRoot());
        server.start();
        String mobileUrl = server.baseUrl().replace("127.0.0.1", "10.0.2.2");

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(System.getProperty("android.device", "Android Emulator"))
                .withBrowserName("Chrome")
                .setNewCommandTimeout(Duration.ofSeconds(120));

        driver = new AndroidDriver(new URL(System.getProperty("appium.server.url", "http://127.0.0.1:4723")), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(mobileUrl);
        driver.executeScript("localStorage.clear()");
        driver.navigate().refresh();
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (server != null) {
            server.stop();
        }
    }

    private static By testId(String value) {
        return By.cssSelector("[data-testid='" + value + "']");
    }

    @Test
    @Order(1)
    @DisplayName("TC-MOB-001 Dang nhap tren thiet bi di dong")
    @TmsLink("MS-6")
    void tcMob001_login() {
        driver.findElement(testId("username")).sendKeys("standard_user");
        driver.findElement(testId("password")).sendKeys("secret_sauce");
        driver.findElement(testId("login-button")).click();

        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(testId("page-products"))).isDisplayed());
    }

    @Test
    @Order(2)
    @DisplayName("TC-MOB-002 Them san pham vao gio tren thiet bi di dong")
    @TmsLink("MS-15")
    void tcMob002_addToCart() {
        driver.findElement(testId("add-P01")).click();
        assertEquals("1", driver.findElement(testId("cart-count")).getText());
    }
}
