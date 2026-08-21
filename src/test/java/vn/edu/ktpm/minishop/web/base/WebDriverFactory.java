package vn.edu.ktpm.minishop.web.base;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Tao WebDriver cho tang UI - ban rut gon v2: chi dung <b>Chrome</b>.
 *
 * <p>Tham so duy nhat: {@code -Dheadless=true|false}
 * ({@code true} de chay nhanh, {@code false} de hien trinh duyet khi quay video).</p>
 *
 * <p>Tu Selenium 4.6, <b>Selenium Manager</b> tu tai driver phu hop nen khong con phai goi
 * {@code System.setProperty("webdriver.chrome.driver", ...)} nhu bai W12.</p>
 *
 * <p><i>Ghi chu ban v2:</i> ban v1 con ho tro Firefox/Edge va chay qua <b>Selenium Grid</b>
 * bang {@code RemoteWebDriver}. Phan do da duoc luoc bo de bai gon lai (xem docs/Plan.md muc 1.2);
 * Grid van duoc trinh bay bang loi noi trong video.</p>
 */
public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver create() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        driver.manage().window().setSize(new Dimension(1440, 900));
        return driver;
    }
}
