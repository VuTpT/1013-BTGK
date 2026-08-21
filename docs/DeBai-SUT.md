# ĐỀ BÀI TỰ ĐẶT – ĐẶC TẢ HỆ THỐNG ĐƯỢC KIỂM THỬ (SUT)

> **Tên hệ thống:** **MiniShop** – Website bán hàng thu nhỏ
> **Vai trò trong seminar:** là *đối tượng kiểm thử* xuyên suốt cả 4 tầng (unit → web UI → mobile → CI).
> **Lý do tự xây thay vì dùng site demo trên Internet:** chạy được **offline**, kết quả **tái lập 100%**, và quan trọng nhất — **cùng một đặc tả nghiệp vụ được kiểm thử ở 2 mức**: mức unit (Java) và mức giao diện (Web), nhờ đó ma trận truy vết (RTM) mới thực sự có ý nghĩa.

---

## 1. Tổng quan

MiniShop gồm 2 phần hiện thực **cùng một bộ quy tắc nghiệp vụ**:

| Phần | Công nghệ | Dùng để kiểm thử bằng |
| --- | --- | --- |
| `sut-core` | Java 17 (thư viện nghiệp vụ thuần, không phụ thuộc UI) | JUnit 5 + EasyMock + JaCoCo |
| `sut-web` | HTML + CSS + JavaScript thuần (SPA tĩnh, lưu trạng thái ở `localStorage`) | Selenium + TestNG, Playwright |

Việc tách đôi này là **có chủ ý**: một lỗi nghiệp vụ có thể bị bắt ở tầng unit (rẻ, nhanh) hoặc tầng UI (đắt, chậm) → dùng để minh họa **kim tự tháp kiểm thử** trong video.

---

## 2. Yêu cầu chức năng

### FR-01 – Đăng nhập

1. Người dùng nhập **tên đăng nhập** và **mật khẩu**.
2. Nếu sai tên đăng nhập hoặc mật khẩu **dưới 3 lần**, hệ thống cho phép nhập lại và hiển thị số lần còn lại.
3. Nếu sai **đủ 3 lần liên tiếp**, tài khoản bị **khóa tạm 15 phút**; hết thời gian khóa hệ thống **tự động mở lại** và bộ đếm sai được reset về 0.
4. Đăng nhập đúng khi tài khoản **đang không bị khóa** → chuyển sang trang danh sách sản phẩm.
5. Đăng nhập đúng nhưng tài khoản **đang bị khóa** → vẫn bị từ chối, báo số phút còn lại.
6. Tên đăng nhập hoặc mật khẩu **để trống** → báo lỗi, **không tính** vào số lần sai.

> *(Yêu cầu này cố tình mô phỏng lại bài W6 – sơ đồ chuyển trạng thái đăng nhập Xmail, để tái sử dụng kiến thức đã học.)*

**Tài khoản mẫu:**

| Username | Password | Loại | Ghi chú |
| --- | --- | --- | --- |
| `standard_user` | `secret_sauce` | Thường | Luồng chuẩn |
| `vip_user` | `secret_sauce` | **VIP** | Dùng cho quy tắc khuyến mãi |
| `locked_user` | `secret_sauce` | Thường | **Bị khóa sẵn** bởi quản trị viên |

### FR-02 – Danh sách sản phẩm & Giỏ hàng

1. Hiển thị 6 sản phẩm, mỗi sản phẩm có: mã, tên, giá (VNĐ), nút *Thêm vào giỏ*.
2. Số lượng mỗi sản phẩm trong giỏ: **số nguyên từ 1 đến 10**. Ngoài khoảng này → từ chối, giữ nguyên giỏ.
3. Giỏ chứa **tối đa 5 loại sản phẩm** khác nhau. Thêm loại thứ 6 → từ chối, báo lỗi.
4. Thêm một sản phẩm đã có trong giỏ → **cộng dồn** số lượng, nhưng tổng không vượt quá 10.
5. Cho phép **xóa** một sản phẩm khỏi giỏ và **xóa toàn bộ** giỏ.
6. **Tạm tính** = Σ (giá × số lượng).

### FR-03 – Khuyến mãi (áp dụng khi thanh toán)

Mức giảm giá được xác định bởi **3 điều kiện độc lập**:

- **C1** – Khách hàng là **VIP**
- **C2** – Tạm tính **≥ 500.000 VNĐ**
- **C3** – Có nhập **mã giảm giá hợp lệ** (`SALE10`)

Quy tắc (dạng bảng quyết định – sẽ phân tích chi tiết ở `TestDesign-Techniques.md`):

| C1 (VIP) | C2 (≥500k) | C3 (mã hợp lệ) | Mức giảm |
| --- | --- | --- | --- |
| T | T | T | **25%** |
| T | T | F | 15% |
| T | F | T | 20% |
| T | F | F | 10% |
| F | T | T | 15% |
| F | T | F | 5% |
| F | F | T | 10% |
| F | F | F | 0% |

- **Tổng thanh toán** = Tạm tính − (Tạm tính × mức giảm), làm tròn **xuống** đến đơn vị đồng.
- Mã giảm giá **không phân biệt hoa thường**, tự động cắt khoảng trắng hai đầu. Mã sai → xem như không có mã (không báo lỗi chặn).

### FR-04 – Thanh toán (validate thông tin giao hàng)

| Trường | Ràng buộc |
| --- | --- |
| **Họ tên** | Bắt buộc; **1–50 ký tự**; chỉ gồm chữ cái A–Z, a–z, chữ tiếng Việt có dấu và khoảng trắng |
| **Số điện thoại** | Bắt buộc; **đúng 10 chữ số**; bắt đầu bằng số `0` |
| **Địa chỉ** | Bắt buộc; **1–100 ký tự** |
| **Giỏ hàng** | Phải có **ít nhất 1 sản phẩm** mới cho thanh toán |

- Hợp lệ toàn bộ → hiện màn hình **"Đặt hàng thành công"** kèm mã đơn hàng và tổng tiền, đồng thời **xóa sạch giỏ**.
- Bất kỳ trường nào sai → hiện **thông báo lỗi tại đúng trường đó**, không tạo đơn.

### FR-05 – Đăng xuất

Đăng xuất đưa người dùng về màn hình đăng nhập, **giữ nguyên** giỏ hàng của phiên (theo tài khoản).

### FR-06 – Tiện ích tính toán (phục vụ kiểm thử hộp trắng)

Kèm theo hai lớp tiện ích **cố ý giữ lại từ bài W8 và W11** để minh họa kỹ thuật hộp trắng:

- `Calculator`: `add`, `sub`, `mul`, `div` trên hai số `double`; chia cho 0 → ném `ArithmeticException`.
- `Rational`: phân số với `numerator`/`denominator` kiểu `long`; hỗ trợ `add`, `subtract`, `multiply`, `divide`, `equals`, `compareTo`, `toString`; mẫu số bằng 0 → ném `Illegal`; luôn tự rút gọn về **dạng tối giản** và giữ dấu âm ở tử số.

---

## 3. Yêu cầu phi chức năng (chỉ nêu, không kiểm thử sâu)

- **NFR-01**: Trang phản hồi thao tác người dùng dưới 1 giây trên máy cá nhân.
- **NFR-02**: Chạy được trên Chrome, Firefox, Edge (bản v2 kiểm chứng trên Chrome bằng Selenium và Playwright).
- **NFR-03**: Không cần server ứng dụng — chạy được từ một web server tĩnh bất kỳ.
- **NFR-04**: Giao diện có `data-testid` ổn định cho mọi phần tử cần thao tác → phục vụ automation, tránh XPath giòn.

---

## 4. Ràng buộc kiểm thử (được ghi thẳng vào đề để phần thiết kế test có căn cứ)

1. **Mọi hằng số biên phải kiểm thử được**: thời gian khóa (15 phút), số lần sai (3), số lượng (1–10), số loại sản phẩm (5), ngưỡng khuyến mãi (500.000), độ dài họ tên (1–50), địa chỉ (1–100), số điện thoại (10 số).
2. `LoginService` **không được gọi trực tiếp** `System.currentTimeMillis()` mà nhận `java.time.Clock` qua constructor → có thể **tua thời gian** trong test để kiểm thử việc tự mở khóa sau 15 phút.
3. `LoginService` lấy dữ liệu tài khoản qua interface `UserRepository` → có thể **giả lập (mock) bằng EasyMock**, không phụ thuộc cơ sở dữ liệu.
4. Trên bản web, thời gian khóa được rút xuống **60 giây** (`LOCK_SECONDS`) để test UI chạy trong thời gian chấp nhận được; giá trị này được ghi rõ trong `app.js` và nêu trong video như một **quyết định thiết kế phục vụ testability**.

---

## 5. Tiêu chí chấp nhận (Acceptance Criteria)

| ID | Tiêu chí |
| --- | --- |
| AC-01 | 100% yêu cầu FR-01…FR-06 có ít nhất 1 test case truy vết được trong RTM |
| AC-02 | Độ phủ lệnh (statement coverage) của `sut-core` ≥ 85%, độ phủ nhánh ≥ 80% |
| AC-03 | Toàn bộ test case tầng unit chạy **pass**, thời gian < 30 giây |
| AC-04 | Suite Selenium chạy pass ở cả chế độ tuần tự và song song |
| AC-05 | Mọi lỗi phát hiện được ghi vào defect log kèm mã Jira |

---

## 6. Bảng ánh xạ: Yêu cầu ↔ Kỹ thuật thiết kế test đã học

| Yêu cầu | Kỹ thuật áp dụng | Bài đã học |
| --- | --- | --- |
| FR-01 Đăng nhập (3 lần sai → khóa 15 phút) | **Sơ đồ chuyển trạng thái** | Bài 6 (W6) |
| FR-02 Số lượng 1–10, tối đa 5 loại | **Giá trị biên (BVA)** | Bài 7 (W7) |
| FR-03 Khuyến mãi theo 3 điều kiện | **Bảng quyết định** | Bài 5 (W5) |
| FR-04 Validate họ tên / SĐT / địa chỉ | **Phân hoạch tương đương + BVA** | Bài 7 (W7) |
| FR-06 `Rational.divide`, `GCD` | **Phủ lệnh / phủ nhánh + Cyclomatic** | Bài 7 (W7) |
| FR-06 `Calculator` | **Unit test equals / notEquals** | Bài 8 (W8) |
| `LoginService` + `UserRepository` | **Mock đối tượng phụ thuộc** | Bài 10 (W10) |
| Toàn bộ dự án | **Maven, TDD đỏ→xanh** | Bài 11 (W11) |
| `sut-web` | **Selenium WebDriver / IDE** | Bài 12 (W12) |
