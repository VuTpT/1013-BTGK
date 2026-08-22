package vn.edu.ktpm.minishop.unit;

import vn.edu.ktpm.minishop.shop.*;

import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-040..049 - FR-02 Gio hang.
 *
 * <p>Thiet ke theo ky thuat <b>phan tich gia tri bien</b> (bai W7):
 * so luong hop le [1..10] -> kiem tra 0, 1, 10, 11;
 * (ban rut gon v2 giu 2 gia tri bien moi ben: 1, 10 va 0, 11).</p>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-02 Gio hang")
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
    }

    @ParameterizedTest(name = "TC-UNIT-040.{index}: so luong hop le = {0}")
    @ValueSource(ints = {1, 10})
    @DisplayName("TC-UNIT-040 [BIEN HOP LE] So luong trong [1..10] duoc chap nhan")
    @TmsLink("KTPM-110")
    @TmsLink("KTPM-112")
    @Tag("smoke")
    void validQuantities(int quantity) {
        cart.add(Catalog.byCode("P01"), quantity);
        assertEquals(quantity, cart.quantityOf("P01"));
    }

    @ParameterizedTest(name = "TC-UNIT-041.{index}: so luong khong hop le = {0}")
    @ValueSource(ints = {0, 11})
    @DisplayName("TC-UNIT-041 [BIEN KHONG HOP LE] So luong ngoai [1..10] bi tu choi, gio khong doi")
    @TmsLink("KTPM-110")
    void invalidQuantities(int quantity) {
        CartException ex = assertThrows(CartException.class,
                () -> cart.add(Catalog.byCode("P01"), quantity));
        assertTrue(ex.getMessage().contains("So luong"));
        assertTrue(cart.isEmpty(), "Gio hang phai giu nguyen khi thao tac that bai");
    }

    @Test
    @DisplayName("TC-UNIT-047 subtotal() = tong (gia x so luong)")
    @TmsLink("KTPM-114")
    void subtotalCalculation() {
        cart.add(Catalog.byCode("P01"), 2);   // 150.000 x 2 = 300.000
        cart.add(Catalog.byCode("P05"), 1);   // 120.000 x 1 = 120.000
        assertEquals(420_000L, cart.subtotal());
        assertEquals(3, cart.totalQuantity());
    }

    @Test
    @DisplayName("TC-UNIT-048 remove() xoa san pham; xoa san pham khong co -> loi")
    @TmsLink("KTPM-113")
    void removeProduct() {
        cart.add(Catalog.byCode("P01"), 1);
        cart.remove("P01");
        assertTrue(cart.isEmpty());
        assertThrows(CartException.class, () -> cart.remove("P01"));
    }

    @Test
    @DisplayName("TC-UNIT-046 [BIEN] Loai san pham thu 6 -> tu choi")
    @TmsLink("KTPM-111")
    void sixthDistinctProductRejected() {
        for (String code : new String[]{"P01", "P02", "P03", "P04", "P05"}) {
            cart.add(Catalog.byCode(code), 1);
        }
        CartException ex = assertThrows(CartException.class, () -> cart.add(Catalog.byCode("P06"), 1));
        assertTrue(ex.getMessage().contains("5"));
        assertEquals(5, cart.distinctProductCount());
    }

    @Test
    @DisplayName("TC-UNIT-049 clear() xoa toan bo gio, subtotal ve 0")
    @TmsLink("KTPM-113")
    void clearCart() {
        cart.add(Catalog.byCode("P02"), 2);
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0L, cart.subtotal());
    }

}
