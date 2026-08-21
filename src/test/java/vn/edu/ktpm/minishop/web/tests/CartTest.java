package vn.edu.ktpm.minishop.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import vn.edu.ktpm.minishop.web.base.BaseTest;
import vn.edu.ktpm.minishop.web.pages.CartPage;
import vn.edu.ktpm.minishop.web.pages.LoginPage;
import vn.edu.ktpm.minishop.web.pages.ProductsPage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * TC-WEB-010..019 - FR-02 Gio hang va FR-03 Khuyen mai tren giao dien.
 *
 * <p>Cac gia tri 0/1/10/11 va so loai san pham 5/6 lay truc tiep tu bang <b>gia tri bien</b>
 * o docs/TestDesign-Techniques.md - dung bo bien da dung cho {@code CartTest} tang unit.</p>
 */
@Epic("Tang 2 - Web UI")
@Feature("FR-02 Gio hang")
public class CartTest extends BaseTest {

    private ProductsPage loginAsStandard() {
        return new LoginPage(driver).loginAs("standard_user", "secret_sauce");
    }

    private ProductsPage loginAsVip() {
        return new LoginPage(driver).loginAs("vip_user", "secret_sauce");
    }

    @DataProvider(name = "invalidQuantities")
    public Object[][] invalidQuantities() {
        return new Object[][]{{0}, {11}, {99}};
    }

    @Test(groups = {"smoke"}, priority = 1, description = "TC-WEB-010 Them san pham vao gio")
    @Severity(SeverityLevel.BLOCKER)
    public void tcWeb010_addProductToCart() {
        ProductsPage products = loginAsStandard().addToCart("P01", 2);
        assertEquals(products.cartCount(), 2, "Badge gio hang phai bang 2");

        CartPage cart = products.openCart();
        assertTrue(cart.hasProduct("P01"));
        assertEquals(cart.quantityOf("P01"), 2);
        assertEquals(cart.lineTotalOf("P01"), 300_000L);
    }



    @Test(groups = {"regression"}, priority = 5, description = "TC-WEB-014 Xoa mot san pham khoi gio")
    public void tcWeb014_removeProduct() {
        CartPage cart = loginAsStandard().addToCart("P01", 1).addToCart("P02", 1).openCart();
        cart.remove("P01");

        assertFalse(cart.hasProduct("P01"), "P01 phai bi xoa");
        assertTrue(cart.hasProduct("P02"), "P02 phai con lai");
        assertEquals(cart.subtotal(), 350_000L);
    }

    @Test(groups = {"smoke"}, priority = 7,
            description = "TC-WEB-016 Tam tinh = tong (gia x so luong)")
    public void tcWeb016_subtotalCalculation() {
        CartPage cart = loginAsStandard().addToCart("P01", 2).addToCart("P05", 1).openCart();
        assertEquals(cart.subtotal(), 420_000L);
    }


}
