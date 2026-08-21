package vn.edu.ktpm.minishop.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

/** FR-04 - Trang thanh toan. */
public class CheckoutPage extends BasePage {

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed("page-checkout");
    }

    @Step("Nhap thong tin giao hang: ho ten=[{0}], sdt=[{1}], dia chi=[{2}]")
    public CheckoutPage fill(String fullName, String phone, String address) {
        type("fullName", fullName);
        type("phone", phone);
        type("address", address);
        return this;
    }

    @Step("Bam Dat hang")
    public CheckoutPage placeOrder() {
        clickable("place-order").click();
        return this;
    }

    public boolean hasError(String field) {
        return isDisplayed("error-" + field);
    }

    public String errorOf(String field) {
        return visible("error-" + field).getText();
    }

    public SuccessPage successPage() {
        return new SuccessPage(driver);
    }
}
