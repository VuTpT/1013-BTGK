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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-060..069 - FR-04 Kiem tra thong tin giao hang.
 *
 * <p>Thiet ke bang <b>phan hoach tuong duong + gia tri bien</b> (ky thuat bai W7):
 * ho ten [1..50], dia chi [1..100], so dien thoai dung 10 so bat dau bang 0.</p>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-04 Thanh toan")
class CheckoutValidatorTest {

    private CheckoutValidator validator;
    private Cart cart;

    @BeforeEach
    void setUp() {
        validator = new CheckoutValidator();
        cart = new Cart();
        cart.add(Catalog.byCode("P01"), 1);
    }

    private static String repeat(char c, int times) {
        return String.valueOf(c).repeat(times);
    }

    @Test
    @DisplayName("TC-UNIT-060 Toan bo thong tin hop le -> khong co loi")
    @TmsLink("KTPM-130")
    @TmsLink("KTPM-131")
    @TmsLink("KTPM-132")
    @Tag("smoke")
    void allValid() {
        ValidationResult result = validator.validate("Nguyen Van A", "0912345678", "12 Nguyen Trai, Q1", cart);
        assertTrue(result.isValid(), () -> "Khong duoc co loi, nhung nhan: " + result);
    }

    @ParameterizedTest(name = "TC-UNIT-061.{index}: ho ten dai {0} ky tu -> hop le")
    @ValueSource(ints = {1, 50})
    @DisplayName("TC-UNIT-061 [BIEN HOP LE] Ho ten dai 1..50 ky tu")
    @TmsLink("KTPM-130")
    void nameLengthValid(int length) {
        ValidationResult result = validator.validate(repeat('A', length), "0912345678", "Ha Noi", cart);
        assertFalse(result.hasError(CheckoutValidator.FIELD_NAME));
    }

    @ParameterizedTest(name = "TC-UNIT-062.{index}: ho ten dai {0} ky tu -> khong hop le")
    @ValueSource(ints = {51})
    @DisplayName("TC-UNIT-062 [BIEN KHONG HOP LE] Ho ten dai hon 50 ky tu")
    @TmsLink("KTPM-130")
    void nameTooLong(int length) {
        ValidationResult result = validator.validate(repeat('A', length), "0912345678", "Ha Noi", cart);
        assertTrue(result.hasError(CheckoutValidator.FIELD_NAME));
        assertTrue(result.errorOf(CheckoutValidator.FIELD_NAME).contains("50"));
    }

    @ParameterizedTest(name = "TC-UNIT-063.{index}: ho ten [{0}] -> khong hop le")
    @CsvSource(value = {
            "''",
            "Nguyen Van 1"
    }, nullValues = "NULL")
    @DisplayName("TC-UNIT-063 [LOP TUONG DUONG KHONG HOP LE] Ho ten rong hoac chua ky tu la")
    @TmsLink("KTPM-130")
    void nameInvalidCharacters(String name) {
        ValidationResult result = validator.validate(name, "0912345678", "Ha Noi", cart);
        assertTrue(result.hasError(CheckoutValidator.FIELD_NAME));
    }


    @ParameterizedTest(name = "TC-UNIT-065.{index}: SDT [{0}] hop le")
    @ValueSource(strings = {"0912345678"})
    @DisplayName("TC-UNIT-065 [LOP HOP LE] So dien thoai dung 10 so, bat dau bang 0")
    @TmsLink("KTPM-131")
    void phoneValid(String phone) {
        ValidationResult result = validator.validate("Nguyen Van A", phone, "Ha Noi", cart);
        assertFalse(result.hasError(CheckoutValidator.FIELD_PHONE));
    }

    @ParameterizedTest(name = "TC-UNIT-066.{index}: SDT [{0}] khong hop le")
    @CsvSource(value = {
            "091234567",      // 9 so - bien duoi
            "09123456789"     // 11 so - bien tren
    }, nullValues = "NULL")
    @DisplayName("TC-UNIT-066 [BIEN + LOP KHONG HOP LE] So dien thoai sai dinh dang")
    @TmsLink("KTPM-131")
    void phoneInvalid(String phone) {
        ValidationResult result = validator.validate("Nguyen Van A", phone, "Ha Noi", cart);
        assertTrue(result.hasError(CheckoutValidator.FIELD_PHONE));
    }



    @Test
    @DisplayName("TC-UNIT-070 Nhieu truong sai cung luc -> bao du tat ca loi")
    @TmsLink("KTPM-130")
    @TmsLink("KTPM-131")
    @TmsLink("KTPM-132")
    void multipleErrorsReported() {
        ValidationResult result = validator.validate("", "abc", "", new Cart());
        assertEquals(4, result.getErrors().size(), () -> "Thuc te: " + result);
    }

    @ParameterizedTest(name = "TC-UNIT-067.{index}: dia chi dai {0} ky tu")
    @CsvSource({"100, false", "101, true"})
    @DisplayName("TC-UNIT-067 [BIEN] Dia chi toi da 100 ky tu")
    @TmsLink("KTPM-132")
    void addressLengthBoundary(int length, boolean expectError) {
        ValidationResult result = validator.validate("Nguyen Van A", "0912345678", repeat('x', length), cart);
        assertEquals(expectError, result.hasError(CheckoutValidator.FIELD_ADDRESS));
    }

    @Test
    @DisplayName("TC-UNIT-069 Gio hang rong -> khong cho thanh toan")
    @TmsLink("KTPM-133")
    void emptyCartRejected() {
        ValidationResult result = validator.validate("Nguyen Van A", "0912345678", "Ha Noi", new Cart());
        assertTrue(result.hasError(CheckoutValidator.FIELD_CART));
    }

}
