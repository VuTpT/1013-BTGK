package vn.edu.ktpm.minishop.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

/** FR-02 / FR-03 - Trang gio hang va khuyen mai. */
public class CartPage extends BasePage {

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed("page-cart");
    }

    public boolean isEmptyMessageDisplayed() {
        return isDisplayed("cart-empty");
    }

    public boolean hasProduct(String productCode) {
        return isDisplayed("cart-row-" + productCode);
    }

    public int quantityOf(String productCode) {
        return Integer.parseInt(visible("cart-qty-" + productCode).getText().trim());
    }

    public long lineTotalOf(String productCode) {
        return parseMoney(visible("cart-line-total-" + productCode).getText());
    }

    public long subtotal() {
        return parseMoney(visible("subtotal").getText());
    }

    public int discountPercent() {
        return Integer.parseInt(visible("discount-percent").getText().replace("%", "").trim());
    }

    public long total() {
        return parseMoney(visible("total").getText());
    }

    @Step("Ap dung ma giam gia [{0}]")
    public CartPage applyCoupon(String coupon) {
        type("coupon", coupon);
        clickable("apply-coupon").click();
        return this;
    }

    public boolean hasMessage() {
        return isDisplayed("cart-message");
    }

    public String message() {
        return visible("cart-message").getText();
    }

    @Step("Xoa san pham {0} khoi gio")
    public CartPage remove(String productCode) {
        clickable("remove-" + productCode).click();
        return this;
    }

    @Step("Xoa toan bo gio hang")
    public CartPage clearCart() {
        clickable("clear-cart").click();
        return this;
    }

    @Step("Sang trang thanh toan")
    public CheckoutPage goToCheckout() {
        clickable("go-checkout").click();
        return new CheckoutPage(driver);
    }

    public ProductsPage backToProducts() {
        clickable("nav-products").click();
        return new ProductsPage(driver);
    }
}
