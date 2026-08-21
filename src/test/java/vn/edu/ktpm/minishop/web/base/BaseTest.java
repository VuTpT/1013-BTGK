package vn.edu.ktpm.minishop.web.base;

import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;
import vn.edu.ktpm.minishop.support.LocalWebServer;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Lop cha cho moi test UI: khoi dong SUT, tao/dong WebDriver, chup man hinh khi that bai.
 */
public abstract class BaseTest {

    private static LocalWebServer server;
    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void startSut() throws IOException {
        if (server == null) {
            server = new LocalWebServer(LocalWebServer.defaultRoot());
            server.start();
            System.out.println("[SUT] MiniShop dang chay tai " + server.baseUrl());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void stopSut() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void openBrowser() {
        driver = WebDriverFactory.create();
        driver.get(server.baseUrl());
        // Moi test case bat dau tu trang thai sach: xoa bo dem dang nhap sai va gio hang cu.
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                attachScreenshot(result.getName());
                attachPageSource();
            }
            driver.quit();
        }
    }

    protected String baseUrl() {
        return server.baseUrl();
    }

    private void attachScreenshot(String name) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot - " + name, new ByteArrayInputStream(png));
        } catch (Exception ignored) {
            // Khong de viec chup man hinh lam hong ket qua test
        }
    }

    private void attachPageSource() {
        try {
            Allure.addAttachment("Page source", "text/html", driver.getPageSource(), ".html");
        } catch (Exception ignored) {
        }
    }
}
