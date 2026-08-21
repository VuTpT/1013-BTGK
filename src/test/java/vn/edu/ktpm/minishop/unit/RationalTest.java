package vn.edu.ktpm.minishop.unit;

import vn.edu.ktpm.minishop.math.*;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TC-UNIT-011..022 - FR-06 Rational (ban rut gon v2: 12 test case, v1 co 36).
 *
 * <p>Bo test nay duoc thiet ke theo ky thuat <b>hop trang</b>: cac test case cua
 * {@code divide()} va {@code GCD()} duoc suy ra tu luu do va do phuc tap Cyclomatic
 * (chi tiet trong docs/TestDesign-Techniques.md muc 4).</p>
 */
@Epic("Tang 1 - Unit test")
@Feature("FR-06 Rational")
class RationalTest {

    private static Rational of(long n, long d) throws Illegal {
        return new Rational(n, d);
    }

    @Nested
    @DisplayName("Khoi tao va chuan hoa")
    class Construction {

        @ParameterizedTest(name = "TC-UNIT-011.{index}: {0}/{1} -> {2}/{3}")
        @CsvSource({
                "2, 4, 1, 2",       // rut gon
                "1, -2, -1, 2"      // day dau am len tu so
        })
        @DisplayName("TC-UNIT-011 Phan so luon o dang toi gian, mau so duong")
        void normalize(long n, long d, long expectedN, long expectedD) throws Illegal {
            Rational r = of(n, d);
            assertEquals(expectedN, r.getNumerator());
            assertEquals(expectedD, r.getDenominator());
        }

        @Test
        @DisplayName("TC-UNIT-012 Mau so bang 0 -> nem Illegal")
        @Tag("smoke")
        void zeroDenominatorThrows() {
            Illegal ex = assertThrows(Illegal.class, () -> of(1, 0));
            assertTrue(ex.getReason().toLowerCase().contains("mau so"));
        }
    }

    @Nested
    @DisplayName("Bon phep toan")
    class Arithmetic {

        @ParameterizedTest(name = "TC-UNIT-013.{index}: {0}/{1} + {2}/{3} = {4}/{5}")
        @CsvSource({
                "1, 2, 1, 3, 5, 6"
        })
        @DisplayName("TC-UNIT-013 add()")
        void add(long n1, long d1, long n2, long d2, long en, long ed) throws Illegal {
            Rational r = of(n1, d1);
            r.add(of(n2, d2));
            assertEquals(of(en, ed), r);
        }

        @ParameterizedTest(name = "TC-UNIT-014.{index}: {0}/{1} - {2}/{3} = {4}/{5}")
        @CsvSource({
                "1, 2, 1, 3, 1, 6"
        })
        @DisplayName("TC-UNIT-014 subtract()")
        void subtract(long n1, long d1, long n2, long d2, long en, long ed) throws Illegal {
            Rational r = of(n1, d1);
            r.subtract(of(n2, d2));
            assertEquals(of(en, ed), r);
        }

        @ParameterizedTest(name = "TC-UNIT-015.{index}: {0}/{1} * {2}/{3} = {4}/{5}")
        @CsvSource({
                "2, 3, 3, 4, 1, 2"
        })
        @DisplayName("TC-UNIT-015 multiply()")
        void multiply(long n1, long d1, long n2, long d2, long en, long ed) throws Illegal {
            Rational r = of(n1, d1);
            r.multiply(of(n2, d2));
            assertEquals(of(en, ed), r);
        }

        @ParameterizedTest(name = "TC-UNIT-016.{index}: {0}/{1} : {2}/{3} = {4}/{5}")
        @CsvSource({
                "1, 2, 1, 3, 3, 2",     // duong / duong
                "1, 2, -1, 3, -3, 2"    // chia cho phan so am -> dau am len tu so
        })
        @DisplayName("TC-UNIT-016 divide() - phu cac duong di doc lap cua luu do")
        void divide(long n1, long d1, long n2, long d2, long en, long ed) throws Illegal {
            Rational r = of(n1, d1);
            r.divide(of(n2, d2));
            assertEquals(of(en, ed), r);
        }

        @Test
        @DisplayName("TC-UNIT-017 divide() cho phan so 0 -> nem Illegal")
        void divideByZeroRationalThrows() throws Illegal {
            Rational r = of(1, 2);
            Rational zero = of(0, 5);
            Illegal ex = assertThrows(Illegal.class, () -> r.divide(zero));
            assertTrue(ex.getReason().contains("0"));
            // Trang thai khong duoc thay doi khi thao tac that bai
            assertEquals(of(1, 2), r);
        }
    }

    @Nested
    @DisplayName("So sanh va hien thi")
    class Comparison {

        @Test
        @DisplayName("TC-UNIT-018 equals() dung cho hai phan so cung gia tri khac cach viet")
        void equalsSameValue() throws Illegal {
            assertEquals(of(1, 2), of(2, 4));
        }

        @Test
        @DisplayName("TC-UNIT-019 notEquals cho hai phan so khac gia tri / khac kieu")
        void notEqualsDifferentValue() throws Illegal {
            assertNotEquals(of(1, 2), of(1, 3));
            assertNotEquals(of(1, 2), "1/2");
        }

        @ParameterizedTest(name = "TC-UNIT-022.{index}: toString cua {0}/{1} = {2}")
        @CsvSource({
                "1, 2, 1/2",
                "4, 2, 2"
        })
        @DisplayName("TC-UNIT-022 toString() rut gon, bo mau so 1")
        void toStringFormat(long n, long d, String expected) throws Illegal {
            assertEquals(expected, of(n, d).toString());
        }
    }
}
