package vn.edu.ktpm.minishop.web.pages;

import org.openqa.selenium.WebDriver;

/** Man hinh dat hang thanh cong. */
public class SuccessPage extends BasePage {

    public SuccessPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed("page-success");
    }

    public String orderId() {
        return visible("order-id").getText();
    }

    public long orderTotal() {
        return parseMoney(visible("order-total").getText());
    }

    public ProductsPage continueShopping() {
        clickable("back-to-products").click();
        return new ProductsPage(driver);
    }
}
