package vn.edu.ktpm.minishop.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import vn.edu.ktpm.minishop.web.base.BaseTest;
import vn.edu.ktpm.minishop.web.pages.CheckoutPage;
import vn.edu.ktpm.minishop.web.pages.LoginPage;
import vn.edu.ktpm.minishop.web.pages.SuccessPage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * TC-WEB-020..029 - FR-04 Thanh toan tren giao dien.
 *
 * <p>Bo du lieu cua {@code invalidCheckoutData()} chinh la cac <b>lop tuong duong khong hop le</b>
 * va <b>gia tri bien</b> da liet ke o docs/TestDesign-Techniques.md muc 5.</p>
 */
@Epic("Tang 2 - Web UI")
@Feature("FR-04 Thanh toan")
public class CheckoutTest extends BaseTest {

    private static final String VALID_NAME = "Nguyen Van A";
    private static final String VALID_PHONE = "0912345678";
    private static final String VALID_ADDRESS = "12 Nguyen Trai, Q1, TP.HCM";

    private CheckoutPage checkoutWithOneProduct() {
        return new LoginPage(driver)
                .loginAs("standard_user", "secret_sauce")
                .addToCart("P01", 2)          // 300.000, khong duoc giam gia
                .openCart()
                .goToCheckout();
    }

    private static String repeat(String s, int times) {
        return s.repeat(times);
    }

    @DataProvider(name = "invalidCheckoutData")
    public Object[][] invalidCheckoutData() {
        return new Object[][]{
                {"", VALID_PHONE, VALID_ADDRESS, "fullName", "TC-WEB-021 Thieu ho ten"}
        };
    }

    @Test(groups = {"smoke"}, priority = 1,
            description = "TC-WEB-020 Dat hang thanh cong voi thong tin hop le")
    @Severity(SeverityLevel.BLOCKER)
    public void tcWeb020_placeOrderSuccessfully() {
        CheckoutPage checkout = checkoutWithOneProduct();
        checkout.fill(VALID_NAME, VALID_PHONE, VALID_ADDRESS).placeOrder();

        SuccessPage success = checkout.successPage();
        assertTrue(success.isDisplayed(), "Phai hien man hinh dat hang thanh cong");
        assertTrue(success.orderId().startsWith("MS"), "Ma don: " + success.orderId());
        assertEquals(success.orderTotal(), 300_000L);

        // Gio hang phai duoc xoa sach sau khi dat hang
        assertEquals(success.continueShopping().cartCount(), 0);
    }

    @Test(dataProvider = "invalidCheckoutData", groups = {"regression"}, priority = 2,
            description = "TC-WEB-021 Thong tin giao hang khong hop le -> bao loi dung truong")
    @Severity(SeverityLevel.CRITICAL)
    public void tcWeb021_invalidCheckoutData(String name, String phone, String address,
                                             String expectedErrorField, String caseName) {
        CheckoutPage checkout = checkoutWithOneProduct();
        checkout.fill(name, phone, address).placeOrder();

        assertTrue(checkout.hasError(expectedErrorField),
                caseName + ": phai bao loi o truong " + expectedErrorField);
        assertTrue(checkout.isDisplayed(), caseName + ": phai o lai trang thanh toan");
    }

}
