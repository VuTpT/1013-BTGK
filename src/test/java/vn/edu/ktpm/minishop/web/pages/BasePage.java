package vn.edu.ktpm.minishop.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Lop cha cua Page Object: gom cac tien ich cho (wait) va tim phan tu theo {@code data-testid}.
 *
 * <p>Chon {@code data-testid} thay vi XPath theo cau truc DOM: locator on dinh hon khi giao dien
 * thay doi - day la mot trong nhung bai hoc trinh bay trong video.</p>
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        // Selenium 4 dung Duration thay cho so nguyen giay nhu ban 3.x o bai W12
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected static By testId(String value) {
        return By.cssSelector("[data-testid='" + value + "']");
    }

    protected WebElement visible(String testId) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(testId(testId)));
    }

    protected WebElement clickable(String testId) {
        return wait.until(ExpectedConditions.elementToBeClickable(testId(testId)));
    }

    protected void type(String testId, String text) {
        WebElement element = visible(testId);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected boolean isDisplayed(String testId) {
        return !driver.findElements(testId(testId)).isEmpty()
                && driver.findElement(testId(testId)).isDisplayed();
    }

    /** "420.000 d" -> 420000 */
    protected static long parseMoney(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0L : Long.parseLong(digits);
    }
}
