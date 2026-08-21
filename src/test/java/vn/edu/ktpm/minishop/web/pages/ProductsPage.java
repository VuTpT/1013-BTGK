package vn.edu.ktpm.minishop.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

/** FR-02 - Trang danh sach san pham. */
public class ProductsPage extends BasePage {

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed("page-products");
    }

    public String currentUser() {
        return visible("current-user").getText();
    }

    @Step("Them {1} san pham {0} vao gio")
    public ProductsPage addToCart(String productCode, int quantity) {
        type("qty-" + productCode, String.valueOf(quantity));
        clickable("add-" + productCode).click();
        return this;
    }

    public ProductsPage addToCart(String productCode) {
        return addToCart(productCode, 1);
    }

    public boolean hasMessage() {
        return isDisplayed("product-message");
    }

    public String message() {
        return visible("product-message").getText();
    }

    public int cartCount() {
        return Integer.parseInt(visible("cart-count").getText().trim());
    }

    public long priceOf(String productCode) {
        return parseMoney(visible("price-" + productCode).getText());
    }

    @Step("Mo gio hang")
    public CartPage openCart() {
        clickable("nav-cart").click();
        return new CartPage(driver);
    }

    @Step("Dang xuat")
    public LoginPage logout() {
        clickable("logout-button").click();
        return new LoginPage(driver);
    }
}
