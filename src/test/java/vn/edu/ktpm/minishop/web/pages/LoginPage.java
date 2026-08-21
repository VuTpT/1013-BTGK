package vn.edu.ktpm.minishop.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

/** FR-01 - Trang dang nhap. */
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Dang nhap voi username={0}, password={1}")
    public ProductsPage loginAs(String username, String password) {
        submit(username, password);
        return new ProductsPage(driver);
    }

    @Step("Nhap username={0}, password={1} va bam Dang nhap")
    public LoginPage submit(String username, String password) {
        type("username", username);
        type("password", password);
        clickable("login-button").click();
        return this;
    }

    public boolean isErrorDisplayed() {
        return isDisplayed("login-error");
    }

    public String errorMessage() {
        return visible("login-error").getText();
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed("page-login");
    }
}
