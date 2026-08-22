package vn.edu.ktpm.minishop.web.tests;

import io.qameta.allure.Description;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;
import vn.edu.ktpm.minishop.web.base.BaseTest;
import vn.edu.ktpm.minishop.web.pages.CartPage;
import vn.edu.ktpm.minishop.web.pages.CheckoutPage;
import vn.edu.ktpm.minishop.web.pages.LoginPage;
import vn.edu.ktpm.minishop.web.pages.SuccessPage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * TC-WEB-030 - Kich ban dau-cuoi (end to end) di qua toan bo 4 yeu cau chuc nang.
 * Day la test case duoc chieu truc tiep trong video demo.
 */
@Epic("Tang 2 - Web UI")
@Feature("Luong dau-cuoi")
public class E2ETest extends BaseTest {

    @TmsLink("KTPM-120")
    @TmsLink("KTPM-121")
    @TmsLink("KTPM-122")
    @TmsLink("KTPM-123")
    @TmsLink("KTPM-134")
    @Test(groups = {"smoke"}, description = "TC-WEB-030 Khach VIP mua hang va thanh toan thanh cong")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Dang nhap VIP -> them 2 loai san pham -> ap ma SALE10 -> kiem tra giam 25% -> dat hang")
    public void tcWeb030_vipHappyPath() {
        CartPage cart = new LoginPage(driver)
                .loginAs("vip_user", "secret_sauce")
                .addToCart("P03", 1)      // 500.000
                .addToCart("P02", 1)      // 350.000  => tam tinh 850.000
                .openCart();

        assertEquals(cart.subtotal(), 850_000L);

        cart.applyCoupon("SALE10");
        assertEquals(cart.discountPercent(), 25, "VIP + don >= 500k + ma hop le");
        assertEquals(cart.total(), 637_500L);

        CheckoutPage checkout = cart.goToCheckout();
        checkout.fill("Tran Thi B", "0987654321", "45 Le Loi, Da Nang").placeOrder();

        SuccessPage success = checkout.successPage();
        assertTrue(success.isDisplayed());
        assertEquals(success.orderTotal(), 637_500L);
        assertEquals(success.continueShopping().cartCount(), 0, "Gio hang phai duoc xoa");
    }
}
