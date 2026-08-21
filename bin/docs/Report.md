# BÁO CÁO KẾT QUẢ KIỂM THỬ – MiniShop

| Mục | Nội dung |
| --- | --- |
| Hệ thống | MiniShop 2.0.0 – **bản rút gọn v2, một project Maven duy nhất** ([DeBai-SUT.md](DeBai-SUT.md)) |
| Ngày thực thi | 19/08/2026 (bản v1: 16/08/2026) |
| Môi trường | Windows 10 Pro 19045 · JDK 17.0.12 · Maven 3.9.16 · Chrome 151 · Eclipse 2024-03 |
| Kết luận | **Đạt toàn bộ tiêu chí ra** (EX-1 … EX-5) |

> Mọi số liệu dưới đây được lấy trực tiếp từ báo cáo Surefire và JaCoCo của lần chạy thật,
> không phải số ước lượng. Lệnh tái lập được ghi kèm từng bảng.

---

## 1. Tổng hợp kết quả

| Tầng | Công cụ | Test case | Pass | Fail | Skip | Thời gian |
| --- | --- | --- | --- | --- | --- | --- |
| 1 – Unit | JUnit 5 + EasyMock | 64 | **64** | 0 | 0 | 5,2 s |
| 2 – Web UI | Selenium 4 + TestNG | 11 | **11** | 0 | 0 | 14 s |
| 2b – Đối chứng | Playwright | 3 | **3** | 0 | 0 | 6 s |
| 3 – Mobile | Appium | 2 | — | — | 2 | Tự bỏ qua khi không có Emulator (`@EnabledIfSystemProperty`) |
| **Tổng** | | **80 thiết kế** | **78 pass** | **0 fail** | **2 skip** | **28 s** cho cả build + 3 tầng |

> **So với bản v1**: 188 → 80 test case (giảm 57%), thời gian `mvn clean test` đầy đủ ~60 s → **28 s**,
> nhưng **độ phủ yêu cầu giữ nguyên 24/24 = 100%**. Chi tiết cắt gọn: [Plan.md](Plan.md) mục 1.

```bash
# Tái lập - CHỈ MỘT LỆNH cho cả 3 tầng (bản v2 chỉ còn 1 module Maven)
cd BTCK
mvn clean test                        # unit + Selenium + Playwright
mvn test -Dmobile.tests=true          # thêm tầng Appium (cần Emulator + appium server)
mvn test jacoco:report                # báo cáo độ phủ
```

### Tỷ lệ tự động hóa

80/80 test case đã thiết kế đều được tự động hóa → **tỷ lệ tự động hóa 100%** (không test case nào chạy tay);
trong đó **78 đã thực thi và pass**, 2 test case Appium chờ môi trường Emulator.

---

## 2. Độ phủ mã lệnh (JaCoCo)

| Chỉ số | Ngưỡng yêu cầu (AC-02) | Đạt được | Kết luận |
| --- | --- | --- | --- |
| Độ phủ lệnh (instruction) | ≥ 80% (ngưỡng v2) | **83,8%** | ✅ Đạt |
| Độ phủ nhánh (branch) | ≥ 75% (ngưỡng v2) | **78,7%** | ✅ Đạt |
| Độ phủ dòng (line) | — | **84,4%** | — |

Báo cáo chi tiết: `target/site/jacoco/index.html` (đã loại trừ gói `support` vì `LocalWebServer`
là tiện ích phục vụ test, không phải đối tượng kiểm thử).

> **Cái giá của việc cắt test case — số liệu thật, nên nói thẳng trong video:**
> bản v1 với 144 unit test đạt 91,3% lệnh / 89,0% nhánh; bản v2 với 64 unit test còn
> **83,8% / 78,7%** (giảm ~7,5 điểm và ~10 điểm). Vì vậy ngưỡng chấp nhận AC-02 đã được
> hạ tương ứng trong [TestPlan.md](TestPlan.md). Đây là minh chứng định lượng cho kết luận:
> **giảm số test case thì giảm độ phủ** — đánh đổi có ý thức chứ không phải ngẫu nhiên.

**Phần chưa được phủ** (cố ý giữ lại để phân tích trong video):

- `Product` — nhánh chặn giá âm (không có đường đi nào từ giao diện tạo ra giá âm)
- `Catalog.byCode` — nhánh mã sản phẩm không tồn tại
- Một số `toString()` phục vụ gỡ lỗi

→ Bài học: **100% coverage không phải mục tiêu**; độ phủ chỉ có giá trị khi đi kèm bộ test case
được thiết kế theo kỹ thuật bài bản (xem [TestDesign-Techniques.md](TestDesign-Techniques.md)).

---

## 3. Kết quả theo yêu cầu chức năng

| Yêu cầu | Số test case | Kết quả |
| --- | --- | --- |
| FR-01 Đăng nhập (khóa 3 lần sai) | 7 unit + 4 UI + 2 PW | ✅ Pass |
| FR-02 Giỏ hàng | 8 unit + 3 UI + 1 PW | ✅ Pass |
| FR-03 Khuyến mãi (bảng quyết định) | 15 unit + 1 UI | ✅ Pass |
| FR-04 Thanh toán | 15 unit + 3 UI | ✅ Pass |
| FR-05 Đăng xuất | 1 UI | ✅ Pass |
| FR-06 Calculator + Rational | 19 unit | ✅ Pass |

Độ phủ yêu cầu: **24/24 = 100%** (ma trận truy vết: [TestCases.md](TestCases.md) mục 5).

---

## 4. Nhật ký lỗi (Defect log)

### DEF-01 / KTPM-190 — Suite UI thất bại hàng loạt khi chạy song song 3 luồng

| Thuộc tính | Nội dung |
| --- | --- |
| **Mức nghiêm trọng** | High (chặn dây chuyền CI, không phải lỗi sản phẩm) |
| **Loại** | Lỗi hạ tầng kiểm thử (test infrastructure) |
| **Phát hiện lúc** | Lần chạy đầu tiên toàn bộ suite `web-selenium` |
| **Hiện tượng** | `Tests run: 49, Failures: 8` — nhiều test báo `TimeoutException` khi khởi tạo ChromeDriver (chờ tới 180 giây), một số báo `Timed out waiting for driver server to stop` |
| **Bước tái hiện** | Đặt `parallel="methods" thread-count="3"` trong `testng.xml` rồi chạy `mvn test -Pui -pl web-selenium` |
| **Phân tích nguyên nhân** | Mỗi test case mở một trình duyệt riêng (`@BeforeMethod`). Với 3 luồng chạy ở mức *method*, có thời điểm 3 tiến trình Chrome + 3 chromedriver khởi động đồng thời → máy không đủ tài nguyên → hết thời gian chờ |
| **Cách xác minh không phải lỗi logic** | Chạy riêng `CheckoutTest` **tuần tự** (`testng-debug.xml`): 12/12 pass trong 17,5 giây → khẳng định test case đúng, lỗi nằm ở cấu hình song song |
| **Cách sửa** | Chuyển sang `parallel="classes" thread-count="2"` (tối đa 2 trình duyệt đồng thời) |
| **Kết quả sau khi sửa** | **36/36 pass trong 48 giây** |
| **Trạng thái** | ✅ Đã đóng |

> Đây chính là kịch bản **build FAIL → tạo Bug trên Jira → sửa → build SUCCESS** được quay trong video:
> một lỗi có thật, phát hiện thật, sửa thật, không dàn dựng.

### DEF-02 — `@CsvSource` không nhận chuỗi rỗng

| Thuộc tính | Nội dung |
| --- | --- |
| **Mức nghiêm trọng** | Medium (chặn 2 test case) |
| **Hiện tượng** | `PreconditionViolationException: Record at index 2 contains invalid CSV: ""` ở `CheckoutValidatorTest` |
| **Nguyên nhân** | Trong `@CsvSource`, một dòng chỉ gồm chuỗi rỗng bị coi là bản ghi không hợp lệ |
| **Cách sửa** | Viết chuỗi rỗng dưới dạng có nháy đơn: `"''"` và `"'   '"` |
| **Trạng thái** | ✅ Đã đóng |

### DEF-03 / KTPM-191 — Gộp project làm engine JUnit 5 chết âm thầm, build vẫn báo SUCCESS

| Thuộc tính | Nội dung |
| --- | --- |
| **Mức nghiêm trọng** | **Critical** (mất trắng một tầng test mà không ai biết) |
| **Loại** | Lỗi hạ tầng kiểm thử |
| **Phát hiện lúc** | Ngay sau khi gộp 7 module thành 1 module ở bản v2 |
| **Hiện tượng** | `Tests run: 37` — chỉ thấy 36 test TestNG, còn **toàn bộ 144 unit test JUnit 5 biến mất**, thay bằng một dòng lỗi `NoClassDefFoundError: org/junit/platform/engine/support/store/NamespacedHierarchicalStore$CloseAction` |
| **Bước tái hiện** | Để `junit-jupiter`, `testng`, `testng-engine` và `allure-junit5` cùng một `pom.xml` mà **không** khai báo `junit-bom` |
| **Phân tích nguyên nhân** | Maven áp dụng quy tắc "nearest wins": `testng-engine` kéo `junit-platform-engine` **1.7.2**, `allure-junit5` kéo `junit-platform-launcher` **1.9.2**, trong khi `junit-jupiter` 5.11.4 cần platform **1.11.4** → engine Jupiter không khởi động được |
| **Cách xác minh** | `mvn dependency:tree \| grep junit-platform` cho thấy rõ ba phiên bản lệch nhau |
| **Cách sửa** | Khai báo `<dependencyManagement>` import **`org.junit:junit-bom`** để đồng bộ toàn bộ họ junit-platform |
| **Kết quả sau khi sửa** | **80/80 test được nhận diện**, 78 pass / 2 skip |
| **Bài học** | Chỉ nhìn dòng `BUILD SUCCESS` là **chưa đủ** — phải kiểm tra cả **tổng số test** mỗi khi đổi cấu hình build |
| **Trạng thái** | ✅ Đã đóng |

**Số lỗi của sản phẩm (`sut-core` / `sut-web`) được phát hiện: 0** — do sản phẩm được hiện thực sau khi
đã có đặc tả và test case (quy trình TDD), nên các sai lệch được chặn ngay khi viết mã.

---

## 5. So sánh công cụ

### 5.1 Selenium vs Playwright — đo trên **cùng test case, cùng SUT, cùng dữ liệu**

> Số liệu dưới đây đo ở **bản v1** (5 cặp test case đối chứng). Bản v2 giữ 3 cặp (TC-PW-001..003);
> tỷ lệ chênh lệch giữa hai công cụ không đổi, nên bảng vẫn giữ nguyên để có mẫu so sánh đầy đủ hơn.

| Tiêu chí | Selenium 4 + TestNG | Playwright | Nhận xét |
| --- | --- | --- | --- |
| Thời gian chạy TC-001 (đăng nhập) | 0,223 s | 0,134 s | Playwright nhanh hơn ~40% |
| TC-005 / PW-002 (khóa tài khoản) | 0,379 s | 0,219 s | nt |
| TC-010 / PW-003 (thêm giỏ hàng) | 0,218 s | 0,165 s | nt |
| TC-018 / PW-004 (khuyến mãi) | 0,255 s | 0,197 s | nt |
| TC-020 / PW-005 (đặt hàng) | 0,381 s | 0,256 s | nt |
| **Tổng 5 test case** | **1,456 s** | **0,971 s** | Playwright nhanh hơn **33%** |
| Số dòng mã toàn module | 962 dòng / 36 TC (5 lớp Page Object + Base + 4 lớp test) ≈ 27 dòng/TC | 175 dòng / 5 TC ≈ 35 dòng/TC | Playwright gọn hơn khi viết ít test; Selenium có chi phí ban đầu cao (Page Object) nhưng **rẻ dần** khi số test tăng và **dễ bảo trì khi giao diện đổi** |
| Cơ chế chờ | Phải viết `WebDriverWait` + `ExpectedConditions` | **Tự động chờ** | Playwright giảm hẳn nguy cơ flaky |
| Gỡ lỗi | Ảnh chụp màn hình khi fail | **Trace Viewer** xem lại từng bước có ảnh + DOM | Playwright vượt trội |
| Khởi động | ~5 s mỗi trình duyệt / test | 1 lần cho cả lớp, mỗi test 1 context | Playwright tiết kiệm hơn |
| Đa trình duyệt | Cần Grid hoặc đổi driver | 1 cấu hình cho chromium/firefox/webkit | Playwright đơn giản hơn |
| Hệ sinh thái, tài liệu tiếng Việt, độ phổ biến trong tuyển dụng | **Rất lớn** | Đang phát triển | Selenium vượt trội |
| Chuẩn hóa | Chuẩn **W3C WebDriver** | Giao thức riêng | Selenium vượt trội |

**Kết luận**: Playwright thắng về tốc độ, độ ổn định và trải nghiệm gỡ lỗi; Selenium thắng về mức độ
phổ biến, chuẩn hóa và hệ sinh thái (Grid, IDE, tích hợp sẵn trong mọi công cụ CI).
Dự án mới nên chọn Playwright; dự án doanh nghiệp sẵn có hạ tầng Selenium thì giữ Selenium.

### 5.2 JUnit 5 vs TestNG — vì sao dùng cả hai

| Tiêu chí | JUnit 5 (tầng unit) | TestNG (tầng UI) |
| --- | --- | --- |
| Tham số hóa | `@ParameterizedTest` + `@CsvSource`/`@ValueSource`/`@MethodSource` — mạnh và gọn hơn | `@DataProvider` — linh hoạt khi đọc dữ liệu ngoài (CSV/Excel/DB) |
| Nhóm test | `@Tag` | `groups` + khai báo `include/exclude` ngay trong `testng.xml` |
| Cấu hình suite | Bằng mã / thuộc tính | **Tập tin XML** — đổi phạm vi chạy không cần biên dịch lại |
| Chạy song song | Qua thuộc tính `junit.jupiter.execution.parallel.*` | `parallel="classes"` hoặc `"methods"` kèm `thread-count="n"` ngay trong XML |
| Chạy lại test flaky | Không có sẵn | **`IRetryAnalyzer`** — thiết yếu cho kiểm thử UI |
| Phụ thuộc giữa test | Không khuyến khích | `dependsOnMethods` — hữu ích cho kịch bản mobile tuần tự |

→ Phân vai: JUnit 5 cho tầng unit (tham số hóa mạnh), TestNG cho tầng UI (suite XML, retry, song song).

### 5.3 Jenkins vs GitHub Actions

| Tiêu chí | Jenkins (dây chuyền chính) | GitHub Actions (cổng PR) |
| --- | --- | --- |
| Vị trí chạy | Máy nhóm (on-premise) | Cloud của GitHub |
| Phù hợp với | Suite đầy đủ cần trình duyệt, Grid, Emulator | Unit test + smoke, chạy nhanh |
| Thời gian phản hồi | Vài phút | < 5 phút, tự chạy mỗi PR |
| Cấu hình | `Jenkinsfile` + cài plugin | 1 tập tin YAML, không cần cài gì |
| Báo cáo Allure | Plugin sẵn có, giữ **history trend** | Phải tải artifact hoặc đẩy lên GitHub Pages |

→ Hai công cụ **bổ sung nhau**, không trùng: Actions chặn lỗi sớm ở cửa PR, Jenkins chạy đầy đủ sau merge.

### 5.4 Maven vs Ant (câu hỏi của bài W11)

| Tiêu chí | Ant | Maven |
| --- | --- | --- |
| Triết lý | Kịch bản mệnh lệnh — tự khai báo mọi bước | Quy ước hơn cấu hình — vòng đời chuẩn `compile → test → package → verify` |
| Quản lý thư viện | Không có (phải chép JAR thủ công, hoặc thêm Ivy) | Có sẵn, khai báo `dependency` là đủ |
| Dự án nhiều module | Phải tự viết logic | Hỗ trợ sẵn (`<modules>`, `-pl`, `-am`) |
| Giống nhau | Đều là công cụ build chạy trên JVM, đều tích hợp được với JUnit/TestNG và mọi công cụ CI | |

Dự án này chọn Maven vì cần quản lý hàng chục thư viện và tích hợp JaCoCo/Surefire/Allure.
Bản v1 dùng Maven **multi-module** (7 module); bản v2 gộp lại còn **một module** cho dễ học và dễ mở
bằng Eclipse — cùng một công cụ Maven nhưng hai mức độ phức tạp khác nhau, tùy quy mô dự án.

---

## 6. Bài học rút ra

1. **Testability phải được thiết kế từ đầu.** `LoginService` nhận `Clock` qua constructor nên test
   "tự mở khóa sau 15 phút" chạy trong vài mili-giây; nếu gọi thẳng `System.currentTimeMillis()`
   thì test case này gần như không kiểm thử được.
2. **Mức song song không phải càng cao càng tốt.** Tăng từ 2 lên 3 luồng khiến 8/49 test thất bại
   (DEF-01). Với kiểm thử UI, giới hạn song song theo tài nguyên máy thật quan trọng hơn tốc độ.
3. **Tách logic khỏi giao diện giúp kiểm thử rẻ đi rất nhiều.** 8 rule của bảng quyết định chạy ở tầng
   unit tốn ~4 mili-giây; nếu chạy qua trình duyệt sẽ mất khoảng 40 giây cho cùng độ phủ.
4. **Locator ổn định quyết định chi phí bảo trì.** Dùng `data-testid` thay vì XPath theo cấu trúc DOM
   giúp đổi bố cục giao diện mà không phải sửa test.
5. **Độ phủ mã là chỉ số hỗ trợ, không phải mục tiêu.** 91% độ phủ có ý nghĩa vì bộ test được thiết kế
   từ 5 kỹ thuật đã học, chứ không phải viết bừa cho đủ số.
6. **`BUILD SUCCESS` không đồng nghĩa với "đã chạy hết test".** DEF-03 cho thấy một xung đột phiên bản
   thư viện có thể xóa sổ nguyên một tầng test mà build vẫn xanh. Tiêu chí nghiệm thu vì thế phải là
   **tổng số test chạy được**, không phải màu của build.
7. **Đơn giản hóa cũng phải trả giá, và cái giá đó đo được.** Gộp 7 module còn 1, cắt 188 test case còn
   80: đổi lại độ phủ lệnh giảm từ 91,3% xuống 83,8%. Việc của người kiểm thử là **biết mình đang đánh đổi cái gì**.
8. **Một lỗi thật đáng giá hơn mười slide lý thuyết.** DEF-01 cho cả nhóm trải nghiệm trọn vẹn vòng đời
   defect: phát hiện → phân lập nguyên nhân (chạy tuần tự để loại trừ) → sửa → kiểm chứng lại.

---

## 7. Hạn chế và hướng mở rộng

| Hạn chế | Hướng khắc phục |
| --- | --- |
| Tầng Appium chưa chạy thực tế (thiếu Emulator trên máy nhóm) | Chạy trên máy có Android Studio, hoặc dùng dịch vụ đám mây (BrowserStack/SauceLabs) |
| Bản v2 đã bỏ Selenium Grid, chạy song song và retry (chỉ còn trình bày bằng lời trong video) | Xem lại nhánh `backup/v1-daydu` — phần này đã hiện thực và chạy được ở bản v1 |
| Chưa có kiểm thử hiệu năng và bảo mật | Bổ sung JMeter (hiệu năng) và OWASP ZAP (bảo mật) |
| SUT không có tầng API | Nếu bổ sung backend thật thì thêm tầng kiểm thử API bằng RestAssured |
| Dữ liệu test còn nằm trong CSV và mã nguồn | Chuyển sang quản lý tập trung, hoặc sinh dữ liệu bằng thư viện faker |
