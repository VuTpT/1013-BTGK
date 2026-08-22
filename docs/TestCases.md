# DANH SÁCH TEST CASE – MiniShop (bản rút gọn v2)

> Nguồn gốc từng test case (kỹ thuật thiết kế nào sinh ra nó) xem [TestDesign-Techniques.md](TestDesign-Techniques.md).
> **Ma trận truy vết đã được gộp vào mục 5 của chính tài liệu này** (trước đây là file riêng `TraceabilityMatrix.md`).
>
> **Tổng cộng: 80 test case** — 64 unit (JUnit 5) · 11 UI (Selenium + TestNG) · 3 đối chứng (Playwright) · 2 mobile (Appium, tùy chọn).
> Kết quả lần chạy gần nhất: **78 pass · 0 fail · 2 skip** (mobile, cần Emulator).
> Ký hiệu `[n]` là số biến thể dữ liệu của một test method tham số hóa.
>
> So với bản v1: 188 → 80 test case (**giảm 57%**) nhưng **giữ nguyên 5 kỹ thuật thiết kế** và
> **độ phủ yêu cầu vẫn 24/24 = 100%** (xem mục 5).

---

## 1. Tầng UNIT – JUnit 5 (`src/test/java/vn/edu/ktpm/minishop/unit`)

### 1.1 FR-06 Calculator — `CalculatorTest` (6 TC)

| ID | Tiêu đề | Kỹ thuật | Dữ liệu | Kỳ vọng |
| --- | --- | --- | --- | --- |
| TC-UNIT-001 [2] | `add()` trả về tổng | EP | (2,3) (−2,3) | 5, 1 |
| TC-UNIT-002 | `add()` **notEquals** giá trị sai | Kiểm chứng âm | (2,3) | ≠ 6.0 |
| TC-UNIT-003 | `div()` trả về thương | EP | (6,3) | 2 |
| TC-UNIT-004 | `div()` chia cho 0 | Lớp không hợp lệ | (6,0) | Ném `ArithmeticException` |
| TC-UNIT-005 | Bốn phép tính trên cùng bộ dữ liệu (`assertAll`) | Gộp | (10,4) | 14, 6, 40, 2.5 |

### 1.2 FR-06 Rational — `RationalTest` (13 TC, dùng `@Nested`)

| ID | Tiêu đề | Kỹ thuật | Kỳ vọng |
| --- | --- | --- | --- |
| TC-UNIT-011 [2] | Chuẩn hóa: rút gọn, mẫu dương | Hộp trắng (`normalize`, `GCD`) | 2/4→1/2 · 1/−2→−1/2 |
| TC-UNIT-012 | Mẫu số = 0 | Giá trị biên không hợp lệ | Ném `Illegal` |
| TC-UNIT-013 | `add()` | EP | 1/2 + 1/3 = 5/6 |
| TC-UNIT-014 | `subtract()` | EP | 1/2 − 1/3 = 1/6 |
| TC-UNIT-015 | `multiply()` | EP | 2/3 × 3/4 = 1/2 |
| TC-UNIT-016 [2] | `divide()` — phủ các đường đi độc lập | **Phủ nhánh** | 1/2 ÷ 1/3 = 3/2 · 1/2 ÷ (−1/3) = −3/2 |
| TC-UNIT-017 | `divide()` cho phân số 0 | **Phủ nhánh** | Ném `Illegal`, trạng thái **không đổi** |
| TC-UNIT-018 | `equals()` hai cách viết cùng giá trị | Kiểm chứng dương | 1/2 = 2/4 |
| TC-UNIT-019 | `notEquals` khác giá trị / khác kiểu | Kiểm chứng âm | 1/2 ≠ 1/3, ≠ "1/2" |
| TC-UNIT-022 [2] | `toString()` | EP | `1/2`, `2` |

### 1.3 FR-01 Đăng nhập — `LoginServiceTest` (7 TC, dùng EasyMock)

| ID | Trạng thái → sự kiện → trạng thái | Kỹ thuật | Kỳ vọng |
| --- | --- | --- | --- |
| TC-UNIT-030 | ChưaĐN → nhập đúng → ĐãĐN | Sơ đồ trạng thái S1 | `SUCCESS` |
| TC-UNIT-031 [2] | ChưaĐN → sai lần 1, 2 → ChưaĐN | S2, S3 | `INVALID_CREDENTIAL`, còn 2 / còn 1 lượt |
| TC-UNIT-032 | ChưaĐN → sai lần 3 → KhóaTạm | S4 | `LOCKED`, `lockedUntil` = now + 15′ |
| TC-UNIT-033 | KhóaTạm → nhập **đúng** → KhóaTạm | S5 | `LOCKED`, còn 15 phút |
| TC-UNIT-035 | KhóaTạm → hết 15′ → ĐãĐN | S6, S7 | `SUCCESS`, bộ đếm reset 0 |
| TC-UNIT-037 | Tài khoản không tồn tại | S9 | `INVALID_CREDENTIAL` (không lộ thông tin) |

> Cả 7 test case đều **mock `UserRepository` bằng EasyMock** và **tua thời gian bằng `MutableClock`** —
> minh họa trực tiếp yêu cầu của bài W10.

### 1.4 FR-02 Giỏ hàng — `CartTest` (8 TC)

| ID | Tiêu đề | Kỹ thuật | Kỳ vọng |
| --- | --- | --- | --- |
| TC-UNIT-040 [2] | Số lượng 1, 10 | **BVA hợp lệ** | Chấp nhận |
| TC-UNIT-041 [2] | Số lượng 0, 11 | **BVA không hợp lệ** | `CartException`, giỏ **không đổi** |
| TC-UNIT-046 | Loại sản phẩm thứ 6 | **BVA** | Từ chối, giữ 5 loại |
| TC-UNIT-047 | `subtotal()` | Tính toán | 150k×2 + 120k = 420.000 |
| TC-UNIT-048 | `remove()` sản phẩm có / không có | EP | Xóa được / `CartException` |
| TC-UNIT-049 | `clear()` | EP | Giỏ rỗng, tạm tính 0 |

### 1.5 FR-03 Khuyến mãi — `DiscountPolicyTest` (15 TC)

| ID | Tiêu đề | Kỹ thuật | Kỳ vọng |
| --- | --- | --- | --- |
| TC-UNIT-050 [8] | **Phủ đủ 8 rule của bảng quyết định** | Bảng quyết định | 25, 15, 20, 10, 15, 5, 10, 0 (%) |
| TC-UNIT-051 [2] | Ngưỡng 499.999 / 500.000 | **BVA** | 0% / 5% |
| TC-UNIT-052 [3] | Mã `sale10`, `  SALE10  `, `SALE11` | EP hợp lệ / không hợp lệ | Hợp lệ · hợp lệ · không hợp lệ |
| TC-UNIT-055 [2] | `finalAmount()` trừ đúng số tiền | Tính toán | 600k → 570k (5%) · 600k VIP+mã → 450k (25%) |

> **Không cắt 8 rule của bảng quyết định** — đây là toàn bộ nội dung kỹ thuật bài W5, cắt đi là mất kỹ thuật.

### 1.6 FR-04 Thông tin giao hàng — `CheckoutValidatorTest` (13 TC)

| ID | Tiêu đề | Kỹ thuật | Kỳ vọng |
| --- | --- | --- | --- |
| TC-UNIT-060 | Toàn bộ hợp lệ | EP hợp lệ | Không lỗi |
| TC-UNIT-061 [2] | Họ tên 1, 50 ký tự | **BVA hợp lệ** | Không lỗi |
| TC-UNIT-062 | Họ tên 51 ký tự | **BVA không hợp lệ** | Lỗi `fullName` |
| TC-UNIT-063 [2] | Họ tên rỗng / có chữ số | EP không hợp lệ | Lỗi `fullName` |
| TC-UNIT-065 | SĐT `0912345678` | EP hợp lệ | Không lỗi |
| TC-UNIT-066 [2] | SĐT 9 số / 11 số | **BVA không hợp lệ** | Lỗi `phone` |
| TC-UNIT-067 [2] | Địa chỉ 100 / 101 ký tự | **BVA** | Không lỗi / lỗi `address` |
| TC-UNIT-069 | Giỏ hàng rỗng | EP | Lỗi `cart` |
| TC-UNIT-070 | Nhiều trường sai cùng lúc | Gộp | Báo đủ các lỗi |

### 1.7 FR-04 Đặt hàng — `CheckoutServiceTest` (2 TC, mức tích hợp)

| ID | Tiêu đề | Kỳ vọng |
| --- | --- | --- |
| TC-UNIT-075 | Đặt hàng thành công | Mã đơn `MS…`, giảm 5%, giỏ được xóa |
| TC-UNIT-077 | Thông tin sai | `CartException`, **giỏ hàng không bị xóa** |

---

## 2. Tầng WEB UI – Selenium 4 + TestNG (`src/test/java/vn/edu/ktpm/minishop/web`)

Tiền điều kiện chung: SUT chạy tại địa chỉ do `LocalWebServer` cấp; mỗi test case bắt đầu bằng
trình duyệt sạch (`localStorage.clear()`). Chạy tuần tự, không retry.

### 2.1 FR-01 Đăng nhập — `LoginTest` (5 TC)

| ID | Nhóm | Các bước | Dữ liệu | Kỳ vọng |
| --- | --- | --- | --- | --- |
| TC-WEB-001 | smoke | Nhập tài khoản → Đăng nhập | `standard_user` / `secret_sauce` | Vào trang sản phẩm, hiện tên user, giỏ = 0 |
| TC-WEB-002 [3] | regression | Nhập sai → Đăng nhập (**data-driven từ `login-data.csv`**) | mật khẩu sai · bỏ trống username · `locked_user` | Hiện đúng thông báo lỗi, ở lại trang đăng nhập |
| TC-WEB-006 | smoke | Đăng nhập → Đăng xuất | — | Quay về trang đăng nhập |

### 2.2 FR-02 + FR-03 Giỏ hàng — `CartTest` (3 TC)

| ID | Nhóm | Các bước | Dữ liệu | Kỳ vọng |
| --- | --- | --- | --- | --- |
| TC-WEB-010 | smoke | Thêm P01 số lượng 2 → mở giỏ | P01 × 2 | Badge = 2, thành tiền dòng = 300.000 |
| TC-WEB-014 | regression | Xóa 1 sản phẩm khỏi giỏ | P01, P02 | P01 biến mất, tạm tính = 350.000 |
| TC-WEB-016 | smoke | Kiểm tra tạm tính | P01×2 + P05×1 | 420.000 |

### 2.3 FR-04 Thanh toán — `CheckoutTest` (2 TC)

| ID | Nhóm | Các bước | Dữ liệu | Kỳ vọng |
| --- | --- | --- | --- | --- |
| TC-WEB-020 | smoke | Điền thông tin hợp lệ → Đặt hàng | `Nguyen Van A` / `0912345678` / địa chỉ hợp lệ | Màn hình thành công, mã `MS…`, tổng 300.000, giỏ được xóa |
| TC-WEB-021 | regression | Thiếu họ tên → Đặt hàng (**`@DataProvider`**) | họ tên rỗng | Báo lỗi đúng trường `fullName`, ở lại trang thanh toán |

### 2.4 Luồng đầu-cuối — `E2ETest` (1 TC)

| ID | Nhóm | Các bước | Kỳ vọng |
| --- | --- | --- | --- |
| TC-WEB-030 | smoke | VIP đăng nhập → thêm P03 + P02 (850.000) → áp `SALE10` → thanh toán | Giảm 25%, tổng 637.500, đặt hàng thành công, giỏ được xóa |

---

## 3. Tầng đối chứng – Playwright (`src/test/java/vn/edu/ktpm/minishop/pw`)

Ba test case dưới đây **lặp lại chính xác** kịch bản Selenium tương ứng, cùng SUT và cùng dữ liệu;
mục đích là so sánh công cụ chứ không phải tăng độ phủ.

| ID | Tương ứng | Nội dung |
| --- | --- | --- |
| TC-PW-001 | TC-WEB-001 | Đăng nhập hợp lệ |
| TC-PW-002 | (luồng khóa tài khoản) | Sai 3 lần → khóa tài khoản |
| TC-PW-003 | TC-WEB-010 | Thêm sản phẩm vào giỏ |

---

## 4. Tầng mobile – Appium (`src/test/java/vn/edu/ktpm/minishop/mobile`, tùy chọn)

| ID | Nội dung | Kỳ vọng |
| --- | --- | --- |
| TC-MOB-001 | Đăng nhập trên Chrome của Android Emulator | Hiện trang sản phẩm |
| TC-MOB-002 | Thêm sản phẩm vào giỏ | Badge = 1 |

> Chạy: bật Emulator + `appium`, rồi `mvn test -Dmobile.tests=true`.
> Emulator truy cập máy thật qua địa chỉ `10.0.2.2` thay cho `127.0.0.1`.
> Nếu không bật Emulator, cả lớp tự động bị bỏ qua (`@EnabledIfSystemProperty`) — build vẫn xanh.

---

## 5. MA TRẬN TRUY VẾT (gộp từ `TraceabilityMatrix.md`)

Mục đích: chứng minh **mọi yêu cầu đều được kiểm thử** và **mọi test case đều phục vụ một yêu cầu**.

### 5.1 Yêu cầu → Test case

| Yêu cầu | Nội dung | Kỹ thuật thiết kế | Tầng UNIT | Tầng UI | Jira (key thật · mã nội bộ) | Kết quả |
| --- | --- | --- | --- | --- | --- | --- |
| **FR-01.1** | Đăng nhập đúng → vào hệ thống | Sơ đồ trạng thái S1 | TC-UNIT-030 | TC-WEB-001, TC-PW-001, TC-MOB-001 | MS-6 (KTPM-101) | ✅ Pass |
| **FR-01.2** | Sai < 3 lần → cho nhập lại | S2, S3 | TC-UNIT-031 | TC-PW-002 | MS-7 (KTPM-102) | ✅ Pass |
| **FR-01.3** | Sai đủ 3 lần → khóa tạm | S4 | TC-UNIT-032 | TC-PW-002 | MS-8 (KTPM-103) | ✅ Pass |
| **FR-01.4** | Hết thời gian khóa → tự mở, reset bộ đếm | S6, S7 | TC-UNIT-035 | — (chỉ ở tầng unit) | MS-9 (KTPM-104) | ✅ Pass |
| **FR-01.5** | Đang khóa, nhập đúng vẫn bị từ chối | S5 | TC-UNIT-033 | TC-PW-002 | MS-10 (KTPM-105) | ✅ Pass |
| **FR-01.6** | Bỏ trống ô nhập → báo lỗi | S8 | — | TC-WEB-002 (dòng CSV 2) | MS-11 (KTPM-106) | ✅ Pass |
| **FR-01.7** | Tài khoản bị quản trị viên khóa | S10 | TC-UNIT-037 | TC-WEB-002 (dòng CSV 3) | MS-12 (KTPM-107) | ✅ Pass |
| **FR-02.1** | Số lượng mỗi sản phẩm 1..10 | **BVA** | TC-UNIT-040, 041 | TC-WEB-010 | MS-13 (KTPM-110) | ✅ Pass |
| **FR-02.2** | Tối đa 5 loại sản phẩm | **BVA** | TC-UNIT-046 | — | MS-14 (KTPM-111) | ✅ Pass |
| **FR-02.3** | Thêm sản phẩm vào giỏ | EP | TC-UNIT-040 | TC-WEB-010, TC-PW-003, TC-MOB-002 | MS-15 (KTPM-112) | ✅ Pass |
| **FR-02.4** | Xóa 1 sản phẩm / xóa toàn bộ giỏ | EP | TC-UNIT-048, 049 | TC-WEB-014 | MS-16 (KTPM-113) | ✅ Pass |
| **FR-02.5** | Tạm tính = Σ(giá × số lượng) | Tính toán | TC-UNIT-047 | TC-WEB-016 | MS-17 (KTPM-114) | ✅ Pass |
| **FR-03.1** | 8 rule khuyến mãi | **Bảng quyết định** | TC-UNIT-050 (8 rule) | TC-WEB-030 (R1) | MS-18 (KTPM-120) | ✅ Pass |
| **FR-03.2** | Ngưỡng 500.000 tính là ≥ | **BVA** | TC-UNIT-051 | TC-WEB-030 | MS-19 (KTPM-121) | ✅ Pass |
| **FR-03.3** | Mã giảm giá: không phân biệt hoa thường, cắt khoảng trắng | EP | TC-UNIT-052 | TC-WEB-030 | MS-20 (KTPM-122) | ✅ Pass |
| **FR-03.4** | Tổng thanh toán trừ đúng số tiền | Tính toán | TC-UNIT-055 | TC-WEB-030 | MS-21 (KTPM-123) | ✅ Pass |
| **FR-04.1** | Họ tên 1–50 ký tự, chỉ chữ và khoảng trắng | **EP + BVA** | TC-UNIT-061, 062, 063 | TC-WEB-021 | MS-22 (KTPM-130) | ✅ Pass |
| **FR-04.2** | SĐT đúng 10 số, bắt đầu bằng 0 | **EP + BVA** | TC-UNIT-065, 066 | TC-WEB-020 | MS-23 (KTPM-131) | ✅ Pass |
| **FR-04.3** | Địa chỉ 1–100 ký tự | **BVA** | TC-UNIT-067 | TC-WEB-020 | MS-24 (KTPM-132) | ✅ Pass |
| **FR-04.4** | Giỏ rỗng → không cho thanh toán | EP | TC-UNIT-069 | — | MS-25 (KTPM-133) | ✅ Pass |
| **FR-04.5** | Đặt hàng thành công → mã đơn + xóa giỏ | Luồng chính | TC-UNIT-075, 077 | TC-WEB-020, 030 | MS-26 (KTPM-134) | ✅ Pass |
| **FR-05** | Đăng xuất về màn hình đăng nhập | Sơ đồ trạng thái S11 | — | TC-WEB-006 | MS-27 (KTPM-140) | ✅ Pass |
| **FR-06.1** | `Calculator` 4 phép tính + chia 0 | EP + lớp không hợp lệ | TC-UNIT-001..005 | — | MS-28 (KTPM-150) | ✅ Pass |
| **FR-06.2** | `Rational` chuẩn hóa, 4 phép toán, so sánh | **Phủ lệnh/nhánh + Cyclomatic** | TC-UNIT-011..022 | — | MS-29 (KTPM-151) | ✅ Pass |

**Độ phủ yêu cầu: 24/24 = 100%** (giữ nguyên như bản v1, dù số test case đã giảm 57%).

### 5.2 Test case → Yêu cầu (không có test "mồ côi")

| Nhóm test case | Số lượt chạy | Yêu cầu phục vụ |
| --- | --- | --- |
| TC-UNIT-001..005 | 6 | FR-06.1 |
| TC-UNIT-011..022 | 13 | FR-06.2 |
| TC-UNIT-030..037 | 7 | FR-01.1 … FR-01.7 |
| TC-UNIT-040..049 | 8 | FR-02.1 … FR-02.5 |
| TC-UNIT-050..055 | 15 | FR-03.1 … FR-03.4 |
| TC-UNIT-060..070 | 13 | FR-04.1 … FR-04.4 |
| TC-UNIT-075, 077 | 2 | FR-04.5 |
| TC-WEB-001..006 | 5 | FR-01.*, FR-05 |
| TC-WEB-010..016 | 3 | FR-02.*, FR-03.* |
| TC-WEB-020..021 | 2 | FR-04.* |
| TC-WEB-030 | 1 | Luồng đầu-cuối (FR-01→FR-04) |
| TC-PW-001..003 | 3 | Đối chứng công cụ (không thêm độ phủ yêu cầu) |
| TC-MOB-001..002 | 2 (skip nếu không có Emulator) | Kiểm chứng chạy được trên thiết bị di động |
| **Tổng** | **80** | — |

### 5.3 Kỹ thuật đã học → Nơi áp dụng

| Kỹ thuật (bài học) | Áp dụng ở đâu | Test case tiêu biểu |
| --- | --- | --- |
| Phân hoạch tương đương (W7) | Họ tên, SĐT, mã giảm giá | TC-UNIT-063, 065, 052 |
| Phân tích giá trị biên (W7) | Số lượng, độ dài trường, ngưỡng khuyến mãi | TC-UNIT-040, 041, 061, 062, 067, 051 |
| Bảng quyết định (W5) | Chính sách khuyến mãi (8 rule) | TC-UNIT-050 |
| Sơ đồ chuyển trạng thái (W6) | Phiên đăng nhập / khóa tài khoản | TC-UNIT-030..037, TC-PW-002 |
| Phủ lệnh & phủ nhánh (W7) | `Rational.divide()`, `GCD()` | TC-UNIT-016, 017 (đo bằng JaCoCo) |
| Mock đối tượng phụ thuộc (W10) | `UserRepository` | Toàn bộ `LoginServiceTest` |
