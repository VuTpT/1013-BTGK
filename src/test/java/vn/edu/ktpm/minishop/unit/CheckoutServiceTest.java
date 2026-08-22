package vn.edu.ktpm.minishop.unit;

import vn.edu.ktpm.minishop.shop.*;

import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-075..078 - FR-04 Dat hang (muc tich hop nho: validator + khuyen mai + gio hang).
 *
 * <p>Doi chieu voi {@code CheckoutValidatorTest}: cung mot rang buoc nhung o muc <i>tich hop</i>,
 * dung de minh hoa su khac nhau giua unit test va integration test trong video.</p>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-04 Thanh toan")
class CheckoutServiceTest {

    private CheckoutService service;
    private Cart cart;

    @BeforeEach
    void setUp() {
        service = new CheckoutService();
        cart = new Cart();
    }

    @Test
    @DisplayName("TC-UNIT-075 Dat hang thanh cong -> tra ve ma don va xoa sach gio")
    @TmsLink("KTPM-134")
    void placeOrderSuccess() {
        cart.add(Catalog.byCode("P03"), 1);   // 500.000
        String orderId = service.placeOrder(cart, false, null, "Nguyen Van A", "0912345678", "Ha Noi");

        assertTrue(orderId.startsWith("MS"), () -> "Ma don khong dung dinh dang: " + orderId);
        assertTrue(orderId.endsWith("475000"), () -> "Phai giam 5% cho don >= 500k: " + orderId);
        assertTrue(cart.isEmpty(), "Gio hang phai duoc xoa sau khi dat hang");
    }


    @Test
    @DisplayName("TC-UNIT-077 Thong tin sai -> nem CartException, gio hang KHONG bi xoa")
    @TmsLink("KTPM-134")
    void placeOrderInvalidInfoKeepsCart() {
        cart.add(Catalog.byCode("P01"), 2);

        CartException ex = assertThrows(CartException.class,
                () -> service.placeOrder(cart, false, null, "", "0912345678", "Ha Noi"));

        assertTrue(ex.getMessage().contains("Ho ten"));
        assertEquals(2, cart.quantityOf("P01"), "Gio hang phai duoc giu nguyen");
    }
}
