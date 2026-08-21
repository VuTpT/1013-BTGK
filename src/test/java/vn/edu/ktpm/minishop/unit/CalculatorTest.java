package vn.edu.ktpm.minishop.unit;

import vn.edu.ktpm.minishop.math.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-001..004 - FR-06 Calculator (yeu cau bai W8: co ca equals va notEquals).
 *
 * <p>Ban rut gon v2: 6 test case (v1 co 17). Van giu du 4 phep tinh, ca hai kieu khang dinh
 * equals/notEquals va truong hop chia cho 0.</p>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-06 Calculator")
@Tag("smoke")
class CalculatorTest {

    private static final double DELTA = 1e-9;

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @ParameterizedTest(name = "TC-UNIT-001.{index}: {0} + {1} = {2}")
    @CsvSource({
            "2, 3, 5",
            "-2, 3, 1"
    })
    @DisplayName("TC-UNIT-001 add() tra ve tong hai so")
    @Story("Phep cong")
    void add_returnsSum(double a, double b, double expected) {
        calculator.setA(a);
        calculator.setB(b);
        assertEquals(expected, calculator.add(), DELTA);
    }

    @Test
    @DisplayName("TC-UNIT-002 add() KHONG tra ve gia tri sai (notEquals)")
    void add_notEqualsWrongValue() {
        calculator = new Calculator(2, 3);
        assertNotEquals(6.0, calculator.add(), DELTA);
    }

    @Test
    @DisplayName("TC-UNIT-003 div() tra ve thuong hai so")
    void div_returnsQuotient() {
        assertEquals(2.0, new Calculator(6, 3).div(), DELTA);
    }

    @Test
    @DisplayName("TC-UNIT-004 div() nem ArithmeticException khi chia cho 0")
    @Severity(SeverityLevel.CRITICAL)
    void div_throwsWhenDivideByZero() {
        Calculator c = new Calculator(6, 0);
        ArithmeticException ex = assertThrows(ArithmeticException.class, c::div);
        assertTrue(ex.getMessage().contains("0"));
    }

    @Test
    @DisplayName("TC-UNIT-005 Kiem tra gop 4 phep tinh tren cung bo du lieu (assertAll)")
    void allOperations_onSameData() {
        Calculator c = new Calculator(10, 4);
        assertAll("bon phep tinh voi a=10, b=4",
                () -> assertEquals(14.0, c.add(), DELTA),
                () -> assertEquals(6.0, c.sub(), DELTA),
                () -> assertEquals(40.0, c.mul(), DELTA),
                () -> assertEquals(2.5, c.div(), DELTA));
    }
}
