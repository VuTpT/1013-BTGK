# KẾ HOẠCH KIỂM THỬ (TEST PLAN) – MiniShop

| Mục | Nội dung |
| --- | --- |
| Dự án | MiniShop – Website bán hàng thu nhỏ (xem [DeBai-SUT.md](DeBai-SUT.md)) |
| Phiên bản | 1.0.0 |
| Người lập | Nhóm 2 thành viên – môn Kiểm thử phần mềm (1013) |
| Ngày lập | 16/08/2026 |
| Trạng thái | Đã thực thi – kết quả tại [Report.md](Report.md) |

---

## 1. Mục tiêu

1. Chứng minh mọi yêu cầu FR-01…FR-06 hoạt động đúng đặc tả.
2. Áp dụng và minh họa **5 kỹ thuật thiết kế test case** đã học (W5–W8) trên một hệ thống thật.
3. Xây dựng một **dây chuyền kiểm thử tự động khép kín** Jira → GitHub → CI → Allure.
4. Đạt các tiêu chí chấp nhận AC-01…AC-05 trong đề bài.

## 2. Phạm vi

### Trong phạm vi

| Mức | Đối tượng | Công cụ |
| --- | --- | --- |
| Unit | `sut-core` (Java): đăng nhập, giỏ hàng, khuyến mãi, validate, Rational/Calculator | JUnit 5, EasyMock, JaCoCo |
| Tích hợp nhỏ | `CheckoutService` (ghép validator + khuyến mãi + giỏ) | JUnit 5 |
| Hệ thống (UI) | `sut-web` trên trình duyệt desktop | Selenium 4 + TestNG (Chrome) |
| Hệ thống (UI đối chứng) | 5 kịch bản tiêu biểu | Playwright |
| Hệ thống (mobile) | `sut-web` trên Chrome của Android Emulator | Appium |

### Ngoài phạm vi

Kiểm thử hiệu năng, bảo mật, khả năng truy cập (accessibility), tương thích trình duyệt cũ (IE),
kiểm thử API (SUT không có tầng API).

## 3. Chiến lược kiểm thử

### 3.1 Kim tự tháp kiểm thử

```
            /\        Mobile (Appium)        2 TC   – kiểm chứng chạy được trên thiết bị
           /  \       UI đối chứng (PW)      3 TC   – so sánh công cụ
          /    \      UI (Selenium+TestNG)  11 TC   – luồng người dùng chính
         /______\     Unit (JUnit 5)        64 TC   – phủ các nhánh nghiệp vụ chính
```

Nguyên tắc: **tổ hợp điều kiện đẩy xuống tầng unit**, tầng UI chỉ giữ kịch bản đại diện.
Lý do và số liệu cụ thể: [TestDesign-Techniques.md §6](TestDesign-Techniques.md).

### 3.2 Kỹ thuật thiết kế test case

Phân hoạch tương đương · Giá trị biên · Bảng quyết định · Sơ đồ chuyển trạng thái · Phủ lệnh và
phủ nhánh (Cyclomatic) — chi tiết trong [TestDesign-Techniques.md](TestDesign-Techniques.md).

### 3.3 Loại kiểm thử

| Loại | Cách thực hiện |
| --- | --- |
| Functional | Toàn bộ test case theo FR |
| Regression | Nhóm `regression` trong `testng.xml`, chạy đầy đủ trên Jenkins |
| Smoke | Nhóm `smoke` (6 TC, ~7 giây), chạy trên mỗi Pull Request qua GitHub Actions |
| Retest sau sửa lỗi | Chạy lại test case gắn với Bug trên Jira trước khi đóng issue |

## 4. Môi trường kiểm thử

| Thành phần | Phiên bản |
| --- | --- |
| Hệ điều hành | Windows 10 Pro 19045 |
| JDK | Temurin/Oracle 17.0.12 |
| Maven | 3.9.16 |
| Trình duyệt | Chrome 151 (Selenium Manager tự tải driver), Firefox/Edge tùy chọn |
| Selenium | 4.21.0 (WebDriver + IDE extension; Grid chỉ trình bày, không dùng trong mã) |
| Playwright | 1.44.0 (dùng kênh `chrome` sẵn có) |
| Appium | Server 2.x + java-client 9.2.2 + Android Emulator API 30+ |
| CI | Jenkins LTS (local, cổng 8080) + GitHub Actions |
| Báo cáo | Allure 2.27.0 |

**SUT chạy offline**: `LocalWebServer` (dùng `com.sun.net.httpserver` có sẵn trong JDK) tự phục vụ
thư mục `sut-web` trên một cổng ngẫu nhiên → không phụ thuộc Internet, kết quả tái lập 100%.

## 5. Tiêu chí vào / ra

### Tiêu chí vào (Entry criteria)

- Đặc tả SUT đã chốt ([DeBai-SUT.md](DeBai-SUT.md))
- Môi trường đã cài đặt xong theo [Setup-Guide.md](Setup-Guide.md)
- Test case đã thiết kế và nhập lên Jira

### Tiêu chí ra (Exit criteria)

| ID | Tiêu chí | Ngưỡng |
| --- | --- | --- |
| EX-1 | Tỷ lệ test case pass | 100% ở tầng unit và UI |
| EX-2 | Độ phủ lệnh của mã nghiệp vụ | ≥ 80% *(bản v1 đặt ≥ 85%; hạ ngưỡng vì v2 chủ động cắt 57% số test case — xem [Report.md](Report.md) mục 2)* |
| EX-3 | Độ phủ nhánh của mã nghiệp vụ | ≥ 75% *(bản v1: ≥ 80%)* |
| EX-4 | Bug mức Critical/Blocker còn mở | 0 |
| EX-5 | Mỗi yêu cầu FR có ít nhất 1 test case truy vết được | 100% |

## 6. Rủi ro và biện pháp

| Rủi ro | Mức | Biện pháp |
| --- | --- | --- |
| Test UI không ổn định (flaky) do thời gian chờ | Cao | Explicit wait + **chạy tuần tự** (bản v2 bỏ chạy song song và bỏ `IRetryAnalyzer` cho gọn) |
| Máy chạy CI quá tải khi mở nhiều trình duyệt | Cao | Chạy tuần tự, mỗi lúc chỉ một Chrome (bản v1 chạy 2 luồng; 3 luồng gây `TimeoutException` — DEF-01) |
| Emulator Android nặng, tốn thời gian | Trung bình | Tầng Appium mặc định bị bỏ qua, bật bằng `-Dmobile.tests=true` |
| Chênh lệch phiên bản Chrome/driver | Thấp | Selenium 4.6+ dùng Selenium Manager tự quản driver |
| Quy tắc nghiệp vụ lệch nhau giữa `sut-core` và `sut-web` | Trung bình | Cùng một bộ test case chạy ở hai tầng; hằng số được ghi chú tham chiếu chéo trong mã nguồn |

## 7. Lịch trình

| Giai đoạn | Thời gian | Sản phẩm |
| --- | --- | --- |
| Chuẩn bị môi trường | D1–D2 | [Setup-Guide.md](Setup-Guide.md) |
| Thiết kế test case | D3 | [TestDesign-Techniques.md](TestDesign-Techniques.md), [TestCases.md](TestCases.md) |
| Hiện thực tầng unit | D4 | gói `unit` – 64 TC |
| Hiện thực tầng UI | D5 | gói `web` – 11 TC |
| Đối chứng + mobile + CI | D6 | gói `pw`, gói `mobile`, `ci/` |
| Báo cáo + video | D7 | [Report.md](Report.md), video demo |

## 8. Phân công

| Vai trò | Thành viên A | Thành viên B |
| --- | --- | --- |
| Test Designer | Kỹ thuật hộp trắng, test case tầng unit | Kỹ thuật hộp đen, test case tầng UI |
| Automation Engineer | `sut-core`, `unit-tests`, `web-selenium` | `web-playwright`, `mobile-appium`, `sut-web` |
| CI/CD | Jenkins, `Jenkinsfile` | GitHub Actions, cấu hình Allure |
| Test Manager | Báo cáo, ma trận truy vết | Quản lý Jira, sprint, defect |

## 9. Sản phẩm bàn giao

1. Mã nguồn: **một project Maven** (`pom.xml` + `src/`) + `sut-web/`
2. Tài liệu: 7 tập tin trong `docs/`
3. Cấu hình CI: `ci/Jenkinsfile`, `ci/workflows/ci.yml`
4. Báo cáo Allure và JaCoCo (sinh khi chạy `mvn test`)
5. Video demo step-by-step
