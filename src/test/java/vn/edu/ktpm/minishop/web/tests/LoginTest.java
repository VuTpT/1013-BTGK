package vn.edu.ktpm.minishop.web.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.TmsLink;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import vn.edu.ktpm.minishop.web.base.BaseTest;
import vn.edu.ktpm.minishop.web.pages.LoginPage;
import vn.edu.ktpm.minishop.web.pages.ProductsPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * TC-WEB-001..006 - FR-01 Dang nhap tren giao dien.
 *
 * <p>Cac test case duoc suy ra tu <b>so do chuyen trang thai</b> (bai W6) - cung so do da dung
 * cho {@code LoginServiceTest} o tang unit, nho vay chung minh duoc cung mot yeu cau
 * duoc kiem thu o hai muc khac nhau.</p>
 */
@Epic("Tang 2 - Web UI")
@Feature("FR-01 Dang nhap")
public class LoginTest extends BaseTest {

    /** Doc bo du lieu dang nhap sai tu tap tin CSV - ky thuat data-driven cua TestNG. */
    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() throws IOException {
        List<Object[]> rows = new ArrayList<>();
        try (InputStream in = getClass().getResourceAsStream("/login-data.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // bo dong tieu de
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(";", -1);
                rows.add(new Object[]{parts[0], parts[1], parts[2]});
            }
        }
        return rows.toArray(new Object[0][]);
    }

    @TmsLink("KTPM-101")
    @Test(groups = {"smoke"}, priority = 1, description = "TC-WEB-001 Dang nhap voi tai khoan hop le")
    @Severity(SeverityLevel.BLOCKER)
    public void tcWeb001_loginWithValidAccount() {
        ProductsPage products = new LoginPage(driver).loginAs("standard_user", "secret_sauce");

        assertTrue(products.isDisplayed(), "Phai chuyen sang trang danh sach san pham");
        assertEquals(products.currentUser(), "standard_user");
        assertEquals(products.cartCount(), 0, "Gio hang moi phai rong");
    }

    @TmsLink("KTPM-106")
    @TmsLink("KTPM-107")
    @Test(dataProvider = "invalidLogins", groups = {"regression"}, priority = 2,
            description = "TC-WEB-002 Dang nhap khong hop le (data-driven tu CSV)")
    @Severity(SeverityLevel.CRITICAL)
    public void tcWeb002_invalidLogin(String username, String password, String expectedMessagePart) {
        LoginPage login = new LoginPage(driver).submit(username, password);

        assertTrue(login.isErrorDisplayed(), "Phai hien thong bao loi");
        assertTrue(login.errorMessage().contains(expectedMessagePart),
                "Thong bao thuc te: [" + login.errorMessage() + "], mong doi chua: [" + expectedMessagePart + "]");
        assertTrue(login.isLoginPageDisplayed(), "Van phai o lai trang dang nhap");
    }




    @TmsLink("KTPM-140")
    @Test(groups = {"smoke"}, priority = 3, description = "TC-WEB-006 Dang xuat quay ve trang dang nhap")
    public void tcWeb006_logout() {
        LoginPage login = new LoginPage(driver)
                .loginAs("standard_user", "secret_sauce")
                .logout();
        assertTrue(login.isLoginPageDisplayed(), "Phai quay ve trang dang nhap");
    }

}
