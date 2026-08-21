package vn.edu.ktpm.minishop.unit;

import vn.edu.ktpm.minishop.shop.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-050..055 - FR-03 Khuyen mai (ban rut gon v2: 12 test case, v1 co 27).
 *
 * <p>Bang quyet dinh giu NGUYEN du 8 rule - khong duoc cat, vi day la ky thuat cua bai W5.</p>
 *
 * <p>Moi dong cua {@code decisionTable()} tuong ung <b>chinh xac mot rule</b> trong bang quyet dinh
 * (ky thuat bai W5) - xem docs/TestDesign-Techniques.md muc 3.</p>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-03 Khuyen mai")
class DiscountPolicyTest {

    @ParameterizedTest(name = "TC-UNIT-050 Rule {index}: vip={0}, subtotal={1}, coupon={2} -> {3}%")
    @CsvSource(value = {
            "true  | 600000 | SALE10 | 25",   // R1: T T T
            "true  | 600000 | NULL   | 15",   // R2: T T F
            "true  | 100000 | SALE10 | 20",   // R3: T F T
            "true  | 100000 | NULL   | 10",   // R4: T F F
            "false | 600000 | SALE10 | 15",   // R5: F T T
            "false | 600000 | NULL   |  5",   // R6: F T F
            "false | 100000 | SALE10 | 10",   // R7: F F T
            "false | 100000 | NULL   |  0"    // R8: F F F
    }, delimiter = '|', nullValues = "NULL")
    @DisplayName("TC-UNIT-050 Phu du 8 rule cua bang quyet dinh")
    @Tag("smoke")
    @Story("Bang quyet dinh")
    void decisionTable(boolean vip, long subtotal, String coupon, int expectedPercent) {
        assertEquals(expectedPercent, DiscountPolicy.discountPercent(vip, subtotal, coupon));
    }

    @ParameterizedTest(name = "TC-UNIT-051.{index}: tam tinh {0} -> {1}%")
    @CsvSource({
            "499999, 0",    // ngay duoi nguong
            "500000, 5"     // dung nguong -> duoc uu dai
    })
    @DisplayName("TC-UNIT-051 [BIEN] Nguong 500.000 duoc tinh la >= (khach thuong, khong ma)")
    void thresholdBoundary(long subtotal, int expectedPercent) {
        assertEquals(expectedPercent, DiscountPolicy.discountPercent(false, subtotal, null));
    }



    @ParameterizedTest(name = "TC-UNIT-055.{index}: vip={0}, subtotal={1}, coupon={2} -> thanh tien {3}")
    @CsvSource(value = {
            "false | 600000 | NULL   | 570000",   // giam 5%
            "true  | 600000 | SALE10 | 450000"    // giam 25%
    }, delimiter = '|', nullValues = "NULL")
    @DisplayName("TC-UNIT-055 finalAmount() tru dung so tien va lam tron XUONG")
    void finalAmountCalculation(boolean vip, long subtotal, String coupon, long expected) {
        assertEquals(expected, DiscountPolicy.finalAmount(vip, subtotal, coupon));
    }

    @ParameterizedTest(name = "TC-UNIT-052.{index}: ma [{0}] -> hop le = {1}")
    @CsvSource(value = {
            "sale10   | true",     // khong phan biet hoa thuong
            "'  SALE10  ' | true", // tu cat khoang trang
            "SALE11   | false"     // ma khong ton tai
    }, delimiter = '|')
    @DisplayName("TC-UNIT-052 Ma giam gia: khong phan biet hoa thuong, tu cat khoang trang")
    void couponNormalization(String coupon, boolean expectedValid) {
        assertEquals(expectedValid, DiscountPolicy.isValidCoupon(coupon));
    }

}
