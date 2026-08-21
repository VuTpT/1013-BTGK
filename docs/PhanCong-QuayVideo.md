# PHÂN CÔNG QUAY VIDEO – 2 THÀNH VIÊN

> Kịch bản quay chi tiết cho seminar Automation Test. Mục tiêu **20 phút**, có tiếng của **cả hai người**.
> Mỗi cảnh ghi rõ: ai quay · chuẩn bị gì · bấm gì · nói gì · **cảnh đạt khi nào**.
>
> Tổng quan kịch bản rút gọn nằm ở [Plan.md](Plan.md) mục 8; tài liệu này là bản triển khai chi tiết của mục đó.
> Phân công **làm bài** (không phải quay) nằm ở [Plan.md](Plan.md) mục 11.
> **Lời thoại nguyên văn từng cảnh** và **các bước phải làm trước khi quay**: [KichBan-ChiTiet.md](KichBan-ChiTiet.md).

---

## 1. Nguyên tắc chia việc

| | **Thành viên A** — "người kỹ thuật" | **Thành viên B** — "người quy trình" |
| --- | --- | --- |
| Trục phụ trách | Mã nguồn, IDE, chạy test, CI/CD | Đặc tả, thiết kế test case, Jira, báo cáo |
| Máy cần chuẩn bị | Eclipse + Chrome + Jenkins chạy được | Chrome + tài khoản Jira + Allure CLI |
| Tool đứng tên | JUnit 5 · EasyMock · JaCoCo · TestNG · Selenium · Jenkins | Jira · GitHub Actions · Playwright · Appium · Allure |
| Thời lượng nói | ~10 phút | ~10 phút |

**Quy tắc khi quay:** mỗi người quay **màn hình máy mình**, nói phần mình; hai cảnh cuối (9 và 10)
quay chung một buổi gọi (share màn hình luân phiên) để nghe rõ có hai giọng đối đáp.

**Thứ tự quay đề xuất** (không quay theo thứ tự trình chiếu): quay **cảnh 6 → 7 → 3** trước
(A, phần dễ hỏng nhất, cần chạy thật), rồi **cảnh 4 → 5 → 8** (B), cuối cùng **1, 2, 9, 10**.
Lý do: nếu demo hỏng thì còn thời gian sửa, phần dẫn chuyện quay sau cùng vẫn kịp.

---

## 2. Bảng tổng quan 10 cảnh

| # | Cảnh | Người quay | Thời lượng | Tool xuất hiện |
| --- | --- | --- | --- | --- |
| 1 | Mở đầu + sơ đồ dây chuyền | A | 1'15 | (tổng quan cả 9 tool) |
| 2 | Giới thiệu SUT MiniShop + phạm vi | B | 1'15 | — |
| 3 | Cài đặt + import Eclipse | A | 2'30 | JDK · Maven · **Eclipse** |
| 4 | Thiết kế test case — 5 kỹ thuật đã học | B | 3'00 | (kiến thức môn học) |
| 5 | Quản lý test case bằng Jira | B | 2'00 | **Jira** · GitHub |
| 6 | JUnit 5 + EasyMock + JaCoCo + TDD đỏ→xanh | A | 3'00 | **JUnit 5 · EasyMock · JaCoCo** |
| 7 | TestNG + Selenium (IDE → Page Object) | A | 3'00 | **TestNG · Selenium IDE · WebDriver** |
| 8 | Playwright đối chứng + bảng so sánh | B | 2'00 | **Playwright** |
| 8b | *(tùy chọn)* Appium trên Emulator | B | 1'00 | **Appium** |
| 9 | Vòng khép kín: lỗi → CI đỏ → Jira → sửa → xanh | A + B | 2'30 | **GitHub · Actions · Jenkins · Allure · Jira** |
| 10 | Kết quả, bài học, hạn chế | A + B | 1'00 | — |

**Tổng: 20'45** (có Appium) hoặc **19'45** (không Appium) — dư ~1 phút cho chuyển cảnh.

---

## 3. Chi tiết từng cảnh

### Cảnh 1 — Mở đầu (A · 1'15)

**Chuẩn bị**: mở sẵn [Plan.md](Plan.md) mục 2.1 (sơ đồ dây chuyền) ở chế độ preview Markdown, phóng to.

**Thao tác**: chiếu sơ đồ, dùng con trỏ đi theo đúng chiều mũi tên Jira → GitHub → CI → 4 tầng test → Allure → về lại Jira.

**Phải nói**:
- Tên nhóm, tên môn, đề tài: *"Kết hợp 9 công cụ thành một dây chuyền kiểm thử tự động khép kín"*.
- Một câu chốt: *"Điểm chính không phải dùng được bao nhiêu tool, mà là mỗi tool giữ đúng một mắt xích, bỏ đi một cái là đứt dây chuyền."*

**Cảnh đạt khi**: người xem nhìn sơ đồ hiểu được đầu vào (Jira) và đầu ra (Allure quay lại Jira).

---

### Cảnh 2 — SUT MiniShop (B · 1'15)

**Chuẩn bị**: mở `sut-web/index.html` bằng Chrome; mở sẵn [DeBai-SUT.md](DeBai-SUT.md).

**Thao tác**:
1. Đăng nhập `standard_user` / `secret_sauce` → vào trang sản phẩm.
2. Thêm 1 sản phẩm → mở giỏ → nhập mã `SALE10` → sang trang thanh toán (chưa cần đặt hàng).
3. Đăng xuất, nhập sai mật khẩu **3 lần** → cho xem thông báo khóa tài khoản.

**Phải nói**:
- MiniShop được nhóm **tự viết** để chạy **offline**, kết quả tái lập 100%, không phụ thuộc site trên Internet.
- Cùng một đặc tả được kiểm thử ở **hai mức**: thư viện Java (unit) và giao diện web (UI) → nhờ đó ma trận truy vết mới có ý nghĩa.
- Phạm vi: 6 nhóm yêu cầu FR-01…FR-06; **ngoài phạm vi**: hiệu năng, bảo mật, API.

**Cảnh đạt khi**: quay được đúng cảnh khóa tài khoản sau 3 lần sai (đây là ví dụ dùng lại ở cảnh 4 và 6).

---

### Cảnh 3 — Cài đặt và mở dự án bằng Eclipse (A · 2'30)

**Chuẩn bị**: theo [Setup-Guide.md](Setup-Guide.md); **xóa sẵn project khỏi workspace Eclipse** để import lại từ đầu cho thật.

**Thao tác** (phần 1–2 tua nhanh 4×, phần 3–5 quay tốc độ thật):
1. `java -version` → 17.0.x · `mvn -v` → 3.9.16.
2. Eclipse: *Window → Preferences → Java → Installed JREs* → JDK 17.
3. *File → Import… → Maven → **Existing Maven Projects*** → chọn thư mục `BTCK` → Finish.
4. Chỉ vào cây dự án: **một `pom.xml` duy nhất**, `src/main/java` (nghiệp vụ) và `src/test/java` với **4 gói = 4 tầng test**.
5. *Help → Eclipse Marketplace* → cho thấy **TestNG for Eclipse** đã cài.

**Phải nói**:
- Vì sao chỉ còn 1 module: bản đầy đủ có 7 module Maven, đã gộp lại cho dễ học và dễ mở bằng IDE (xem [Plan.md](Plan.md) mục 1).
- Nếu `pom.xml` gạch đỏ *"Plugin execution not covered by lifecycle"* → *Quick Fix → Mark goal as ignored* — lỗi của m2e, không phải lỗi dự án.

**Cảnh đạt khi**: Project Explorer hiện `minishop-automation` **không có dấu ✕ đỏ**.

---

### Cảnh 4 — Thiết kế test case: 5 kỹ thuật đã học (B · 3'00)

**Chuẩn bị**: mở [TestDesign-Techniques.md](TestDesign-Techniques.md) ở chế độ preview; mở song song [TestCases.md](TestCases.md).

**Thao tác** — mỗi kỹ thuật ~35 giây, **chiếu bảng rồi chỉ sang test case tương ứng**:

| Kỹ thuật | Chiếu ở đâu | Test case dẫn chứng | Câu chốt |
| --- | --- | --- | --- |
| Phân hoạch tương đương (W7) | Bảng lớp tương đương họ tên / SĐT | TC-UNIT-063, 065 | Mỗi lớp chỉ cần 1 đại diện |
| Giá trị biên (W7) | Bảng biên số lượng 0·1·10·11 | TC-UNIT-040, 041 | Lỗi hay nằm ở biên, không nằm ở giữa |
| Bảng quyết định (W5) | Bảng 8 rule khuyến mãi | TC-UNIT-050 (8 rule) | 3 điều kiện → 2³ = 8 rule, **không cắt rule nào** |
| Sơ đồ chuyển trạng thái (W6) | Sơ đồ đăng nhập / khóa 15 phút | TC-UNIT-030…037 | Mỗi mũi tên = 1 test case |
| Phủ lệnh & phủ nhánh (W7) | Lưu đồ `divide()` + V(G) | TC-UNIT-016, 017 | Số test tối thiểu suy ra từ Cyclomatic |

**Phải nói**:
- Test case được **thiết kế trước, viết mã sau** — không phải viết mã rồi đặt tên cho có.
- Chốt bằng ma trận truy vết: [TestCases.md](TestCases.md) mục 5 — **24/24 yêu cầu = 100%**, và bảng 5.3 chỉ rõ kỹ thuật nào áp dụng ở đâu.

**Cảnh đạt khi**: đủ 5 kỹ thuật, mỗi kỹ thuật gắn được với mã test case cụ thể trên màn hình.

---

### Cảnh 5 — Quản lý test case bằng Jira (B · 2'00)

**Chuẩn bị**: đã nhập [jira-import.csv](jira-import.csv) theo [Setup-Guide.md](Setup-Guide.md) bước 10, sprint `KTPM Sprint 1` đã **Start** với khoảng 16/08 → 22/08/2026.

**Thao tác**:
1. Board Scrum: 36 issue — 5 Epic · 24 Test · 3 Task · 2 Bug; kéo thả 1 issue qua cột khác cho thấy quy trình.
2. Mở KTPM-120 (bảng quyết định): chỉ **Description có Steps/Expected/Kỹ thuật**, **Priority**, **Assignee**, **Due date**, **Original Estimate / Time Spent**.
3. Chạy JQL: `project = KTPM AND type = Test AND status != Done ORDER BY priority DESC`.
4. Mở **Reports → Burndown** và **Sprint report**: tổng **ước lượng 28 giờ / thực tế 27,9 giờ**.
5. Mở KTPM-191 (bug thật) → chỉ mục **Development** thấy nhánh/commit gắn mã Jira.

**Phải nói**:
- Quy ước nhánh `feature/KTPM-xx-...` và commit `KTPM-xx <mô tả>` là thứ làm Jira và GitHub tự nối với nhau.
- Độ ưu tiên phản ánh rủi ro: luồng đăng nhập và thanh toán để `Highest`, giao diện phụ để `Low`.

**Cảnh đạt khi**: thấy rõ **giao việc (Assignee A/B)**, **tiến độ (burndown)** và **liên kết sang GitHub** — đúng ba thứ đề bài yêu cầu.

---

### Cảnh 6 — JUnit 5 + EasyMock + JaCoCo + TDD (A · 3'00)

**Chuẩn bị**: mở `LoginServiceTest.java`, `DiscountPolicyTest.java`, `DiscountPolicy.java` sẵn thành tab trong Eclipse.

**Thao tác**:
1. **Annotation** (40s): mở `CalculatorTest` → chỉ `@Test`, `@DisplayName`, `@BeforeEach`, `@ParameterizedTest` + `@CsvSource`, `assertThrows`, `assertAll`; mở `RationalTest` chỉ thêm `@Nested`.
2. **EasyMock** (40s): mở `LoginServiceTest` → chỉ `mock(UserRepository.class)` → `expect(...).andReturn(...)` → `replay` → `verify`; nhấn mạnh `MutableClock` giúp test "tự mở khóa sau 15 phút" chạy trong **vài mili-giây**.
3. **Chạy** (20s): chuột phải gói `unit` → *Run As → JUnit Test* → thanh **xanh, 64 test**.
4. **TDD đỏ → xanh** (60s) — dùng đúng lỗi đã kiểm chứng:
   - Mở `DiscountPolicy.java` dòng `boolean bigOrder = subtotal >= THRESHOLD;` → sửa `>=` thành `>` → Ctrl+S.
   - Chạy lại → **2 test đỏ**: `TC-UNIT-051` (biên 500.000) và `TC-UNIT-075` (đặt hàng 500.000 phải giảm 5%).
   - Nói: *"Một dấu bằng bị mất, hai tầng test bắt được ngay — đây chính là giá trị của kỹ thuật giá trị biên."*
   - Trả lại `>=` → chạy lại → **xanh**.
5. **JaCoCo** (20s): *Run As → Maven build…* goal `test jacoco:report` → mở `target/site/jacoco/index.html` → **83,8% lệnh · 78,7% nhánh**; mở lớp `Rational` cho thấy dòng xanh/đỏ.

**Phải nói**: độ phủ là **chỉ số hỗ trợ, không phải mục tiêu** — bản v1 có 144 unit test đạt 91,3%, bản v2 còn 64 test nên còn 83,8%; đó là đánh đổi có ý thức.

**Cảnh đạt khi**: quay liền mạch được nhịp **xanh → đỏ → xanh** mà không cắt.

---

### Cảnh 7 — TestNG + Selenium (A · 3'00)

**Chuẩn bị**: cài extension Selenium IDE; đóng bớt cửa sổ để Chrome bật lên không che Eclipse.

**Thao tác**:
1. **Selenium IDE** (45s): Record kịch bản đăng nhập MiniShop → Play lại → *Export → Java JUnit*; mở file vừa export cho thấy locator tự sinh.
2. **Page Object** (60s): mở `LoginPage`, `ProductsPage` → chỉ `data-testid` do nhóm chủ động đặt, `BasePage` với `WebDriverWait` + `ExpectedConditions` (nhắc: Selenium 4 dùng `Duration.ofSeconds`, khác bài W12 dùng số nguyên).
3. **TestNG** (45s): mở `testng.xml` → `<suite>` / `<test>` / listener Allure; mở `LoginTest` → `@DataProvider` đọc `login-data.csv`, `priority`, `groups`.
4. **Chạy** (30s): chuột phải `testng.xml` → *Run As → TestNG Suite* → **11 test pass**; hoặc chạy `ChayTest.bat hien` để **hiện trình duyệt** thao tác thật.

**Phải nói**:
- IDE hợp **tạo bản nháp nhanh**, Page Object hợp **bảo trì lâu dài** (bảng so sánh trong `selenium-ide/README.md`).
- Vì sao Selenium 4 **không cần** `System.setProperty("webdriver.chrome.driver", ...)` như bài W12: Selenium Manager tự tải driver.
- Một câu về **Selenium Grid**: đã dùng ở bản v1, bản v2 bỏ vì chi phí lớn hơn lợi ích ở quy mô này (DEF-01 trong [Report.md](Report.md)).

**Cảnh đạt khi**: người xem thấy trình duyệt tự thao tác và suite báo 11 pass.

---

### Cảnh 8 — Playwright đối chứng (B · 2'00)

**Chuẩn bị**: chạy trước `mvn clean test` một lần để có sẵn `target/traces/*.zip`.

**Thao tác**:
1. Mở `MiniShopPlaywrightTest` → chỉ ra 3 test case **trùng khớp 1-1** với TC-WEB-001, khóa tài khoản, TC-WEB-010.
2. So sánh trực diện hai đoạn mã: `page.fill(...)` (Playwright) vs `findElement(...).clear() + sendKeys(...)` + `WebDriverWait` (Selenium).
3. Chạy `mvn test -Dtest=MiniShopPlaywrightTest` → 3 pass.
4. Mở trace: `npx playwright show-trace target/traces/tcPw001_loginValid.zip` → tua lại từng bước có ảnh + DOM.
5. Chiếu bảng so sánh ở [Report.md](Report.md) mục 5.1.

**Phải nói**: Playwright thắng về **tốc độ, auto-wait, trace viewer**; Selenium thắng về **chuẩn W3C, hệ sinh thái, mức phổ biến** → dự án mới chọn Playwright, doanh nghiệp có sẵn hạ tầng thì giữ Selenium.

**Cảnh đạt khi**: bảng so sánh có **số liệu**, không chỉ nhận xét cảm tính.

---

### Cảnh 8b *(tùy chọn)* — Appium (B · 1'00)

**Chuẩn bị**: Emulator + `appium` đã chạy. **Nếu máy yếu thì bỏ cảnh này**, thay bằng 30 giây chiếu ảnh Appium Inspector và giải thích kiến trúc.

**Thao tác**: `mvn test -Dmobile.tests=true` → quay màn hình Emulator tự thao tác → `Tests run: 82`.

**Phải nói**: Emulator gọi về máy thật qua `10.0.2.2`; nếu không bật Emulator thì hai test **tự bỏ qua** nhờ `@EnabledIfSystemProperty` nên build vẫn xanh.

---

### Cảnh 9 — Vòng khép kín (A + B · 2'30) ⭐ cảnh quan trọng nhất

Quay **một mạch, không cắt**. A điều khiển máy, B thuyết minh.

| Bước | Ai làm | Thao tác | Thời lượng |
| --- | --- | --- | --- |
| 1 | A | Sửa `DiscountPolicy` `>=` → `>`, commit `KTPM-192 thu nghiem loi bien`, push lên nhánh | 20s |
| 2 | B | Mở GitHub → tab **Actions** → workflow **đỏ**, mở log chỉ đúng 2 test fail | 30s |
| 3 | B | Tạo issue **Bug KTPM-192** trên Jira, dán link build, đặt Priority `Highest`, gán cho A | 30s |
| 4 | A | Mở **Jenkins** → build tương ứng cũng đỏ → mở **Allure Report** → xem test fail kèm ảnh chụp màn hình | 30s |
| 5 | A | Sửa lại `>=`, commit `KTPM-192 sua loi bien nguong khuyen mai`, push | 20s |
| 6 | B | Actions **xanh** → Jenkins build **SUCCESS** → Allure **history trend** hồi phục → kéo KTPM-192 sang **Done** | 20s |

**Phải nói (B chốt)**: *"Đây là toàn bộ lý do phải kết hợp tool: một dấu `=` bị mất đi qua đủ 5 công cụ — GitHub, Actions, Jenkins, Allure, Jira — và quay về đúng chỗ nó bắt đầu."*

**Cảnh đạt khi**: nhìn thấy **cả đỏ lẫn xanh** của cùng một issue trong cùng một mạch quay.

---

### Cảnh 10 — Kết luận (A + B · 1'00)

**Chiếu**: bảng tổng hợp [Report.md](Report.md) mục 1.

**A nói (30s)** — số liệu: 80 test case · 78 pass · 0 fail · 2 skip · 28 giây · độ phủ 83,8%; 2 defect thật đã đóng (DEF-01 chạy song song, **DEF-03 engine JUnit chết âm thầm**).

**B nói (30s)** — bài học và hạn chế:
- `BUILD SUCCESS` **không** đồng nghĩa "đã chạy hết test" — phải nhìn **tổng số test** (DEF-03).
- Đơn giản hóa cũng có giá: cắt 57% test case thì độ phủ giảm ~7,5 điểm — biết mình đang đánh đổi cái gì.
- Hạn chế: chưa kiểm thử hiệu năng/bảo mật, Appium phụ thuộc máy; hướng mở rộng ở [Report.md](Report.md) mục 7.

---

## 4. Checklist chuẩn bị trước ngày quay

### Thành viên A

- [ ] `mvn clean test` xanh, in đúng **`Tests run: 80`**
- [ ] Eclipse import sạch, tạo sẵn 3 Run Configuration: `1-Unit test`, `2-UI test`, `3-Coverage`
- [ ] Phóng to font Eclipse (*Preferences → General → Appearance → Colors and Fonts*) và font terminal
- [ ] Selenium IDE đã cài, thử record–play **trước** 1 lần
- [ ] Jenkins chạy `localhost:8080`, job đã có **ít nhất 3 build** để Allure có history trend
- [ ] Đã thử trước nhịp `>=` → `>` → chạy đỏ → khôi phục → chạy xanh
- [ ] Tắt thông báo Windows, đóng ứng dụng cá nhân, dọn thanh bookmark trình duyệt

### Thành viên B

- [ ] Jira: đã nhập CSV, sprint đã Start, burndown có dữ liệu, app GitHub for Jira đã cài
- [ ] GitHub: repo public, Actions đã chạy xanh ít nhất 1 lần, badge trong README hoạt động
- [ ] `allure serve target/allure-results` mở được, `allure.properties` đã thay `<ten-site>` bằng site Jira thật
- [ ] Đã chạy `mvn clean test` để có sẵn `target/traces/*.zip` cho trace viewer
- [ ] Emulator + `appium` sẵn sàng **hoặc** đã quyết định bỏ cảnh 8b và chuẩn bị ảnh thay thế
- [ ] Mở sẵn các file Markdown ở chế độ preview, cuộn tới đúng mục cần chiếu

### Cả hai

- [ ] **Diễn tập trọn kịch bản 1 lần có bấm giờ** (ngày D3 – 21/08), ghi lại chỗ vấp
- [ ] Thống nhất câu mở đầu và câu kết
- [ ] OBS: 1920×1080, 30fps, thu cả mic; test âm lượng 30 giây trước khi quay thật
- [ ] Quay clip dự phòng cho cảnh dễ hỏng: Jenkins, Emulator, Actions

---

## 5. Hậu kỳ và nộp bài

| Việc | Người làm | Hạn |
| --- | --- | --- |
| Ghép clip, chèn tiêu đề từng cảnh, chuẩn hóa âm lượng | B | 22/08 trưa |
| Xem lại toàn bộ, đối chiếu bảng mục 6 xem có sót nội dung nào không | A | 22/08 chiều |
| Xuất H.264 CRF 23, kiểm tra dung lượng, đặt tên `Nhom<XX>_AutomationTest.mp4` | B | 22/08 chiều |
| Đẩy repo, tạo tag `v2.0`, kiểm tra README hiển thị đúng trên GitHub | A | 22/08 chiều |
| Nộp mLearning + kiểm tra file mở được sau khi nộp | Cả hai | **trước 22:00 ngày 22/08** |

---

## 6. Bảng đối chiếu: có sót nội dung nào không?

### 6.1 Yêu cầu của đề bài

| Đề bài yêu cầu | Cảnh phủ |
| --- | --- |
| Cài đặt step-by-step | 3 |
| Ví dụ | 6 (unit) · 7 (web) · 8 (đối chứng) · 8b (mobile) |
| Lập test case | 4 |
| Demo | 6 · 7 · 8 · 9 |
| Quản lý test case | 5 |
| **Kết hợp các tool** | 1 (sơ đồ) · **9 (vòng khép kín)** |

### 6.2 Chín công cụ

| Tool | Cảnh | Tool | Cảnh |
| --- | --- | --- | --- |
| Jira | 5 · 9 | Selenium (IDE + WebDriver) | 7 |
| GitHub | 5 · 9 | Playwright | 8 |
| GitHub Actions | 9 | Appium | 8b *(hoặc nói trong 8)* |
| Jenkins | 9 | Allure | 9 |
| JUnit 5 | 6 | *(kèm theo)* EasyMock · JaCoCo · Maven · Eclipse | 6 · 3 |
| TestNG | 7 | | |

### 6.3 Năm kỹ thuật đã học

| Kỹ thuật | Cảnh chính | Nhắc lại ở |
| --- | --- | --- |
| Phân hoạch tương đương | 4 | 6 |
| Giá trị biên | 4 | **6 (nhịp đỏ→xanh)** · 9 |
| Bảng quyết định | 4 | 6 |
| Sơ đồ chuyển trạng thái | 4 | 2 (khóa tài khoản) · 6 |
| Phủ lệnh / phủ nhánh | 4 | 6 (JaCoCo) |

---

## 7. Phương án dự phòng

| Sự cố khi quay | Xử lý ngay |
| --- | --- |
| Jenkins không lên được | Cắt bước 4 của cảnh 9, thay bằng GitHub Actions + Allure trên GitHub Pages, nói rõ lý do |
| Emulator treo | Bỏ cảnh 8b, chiếu ảnh Appium Inspector, giữ nguyên phần giải thích |
| Chrome tự cập nhật làm Selenium lỗi | Chạy `mvn clean test` một lần trước khi quay để Selenium Manager tải driver mới |
| Test đỏ ngoài dự kiến | **Không sửa vội trước ống kính** — dừng quay, xem `target/surefire-reports`, quay lại cảnh đó |
| Video vượt 22 phút | Cắt theo thứ tự: 8b → phần Selenium IDE ở cảnh 7 → bớt 1 kỹ thuật ở cảnh 4 (giữ lại bảng quyết định và biên) |
| Thiếu thời gian trước hạn | Ưu tiên giữ **cảnh 4, 5, 9** — ba cảnh chiếm điểm nặng nhất theo [Plan.md](Plan.md) mục 13 |
