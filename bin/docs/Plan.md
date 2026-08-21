# KẾ HOẠCH RÚT GỌN – SEMINAR AUTOMATION TEST (bản v2, dùng Eclipse)

> **Môn:** Kiểm thử phần mềm (1013) – Seminar nhóm 2 bạn
> **Deadline nộp video:** 00:00:00 ngày 23/08/2026 → mốc chốt thực tế **22:00 ngày 22/08/2026**
> **Ngày lập plan v2:** 19/08/2026 → còn **3,5 ngày**
> **Bản v1 (đầy đủ, đã hiện thực) được giữ lại tại:** [Plan-v1-daydu.md.bak](Plan-v1-daydu.md.bak)

---

## ⚙️ TRẠNG THÁI TRIỂN KHAI (cập nhật 19/08/2026)

Các bước **B0 → B9** ở mục 3 **đã thực hiện xong**. Kết quả nghiệm thu thực tế:

| Hạng mục | Kế hoạch | Thực tế | Trạng thái |
| --- | --- | --- | --- |
| Số module Maven | 1 | **1** (`BTCK/pom.xml`) | ✅ |
| Import Eclipse (*Existing Maven Projects*) | Chạy được ngay | Cấu trúc chuẩn `src/main/java` + `src/test/java` | ✅ |
| Tổng số test nhận diện được | ≥ 50 | **80** (78 pass · 0 fail · 2 skip mobile) | ✅ |
| Số test case | ~54 | **80** — cao hơn dự kiến, xem ghi chú ⚠️ bên dưới | ⚠️ |
| Bỏ profile / Grid / parallel / retry / suite thừa | Bỏ hết | Đã bỏ hết | ✅ |
| Độ phủ mã (JaCoCo) | — | 83,8% lệnh · 78,7% nhánh (v1: 91,3% · 89,0%) | ✅ |
| Thời gian chạy đầy đủ | — | **28 giây** (v1: ~60 giây) | ✅ |
| Tài liệu cập nhật theo bảng 1.4 | Xong | Xong; `TraceabilityMatrix.md` đã gộp vào `TestCases.md` mục 5 | ✅ |

**Ba khác biệt so với kế hoạch — đều có lý do:**

1. ⚠️ **80 test case thay vì ~54.** Cắt sâu hơn sẽ phạm vào chính nguyên tắc của plan
   ("cắt theo dòng dữ liệu, không cắt theo kỹ thuật"): riêng bảng quyết định đã chiếm 8 test case
   (8 rule, không cắt được), và sau nhát cắt đầu tiên ma trận truy vết bị thủng 5 yêu cầu
   (FR-02.2, FR-03.3, FR-04.3, FR-04.4, FR-05) nên phải khôi phục 9 test case để giữ
   **độ phủ yêu cầu 24/24 = 100%**. Vẫn giảm **57%** so với 188 test case của bản v1.
2. ⚠️ **Vẫn dùng `<dependencyManagement>`** (import `junit-bom`), trái với ý định "bỏ cho gọn" ở B1 —
   xem DEF-03 trong [Report.md](Report.md): thiếu nó thì toàn bộ tầng unit test bị bỏ qua âm thầm.
3. ⚠️ **Jira giữ 36 issue thay vì ~20** (5 Epic · 24 Test · 3 Task · 2 Bug): 24 issue `Test` ánh xạ
   1-1 với 24 yêu cầu trong ma trận truy vết, cắt bớt sẽ làm hỏng cột "Jira" của ma trận.
   File [jira-import.csv](jira-import.csv) đã bổ sung **ngày tạo · hạn · ngày hoàn thành · sprint ·
   ước lượng · thời gian đã dùng** (tổng 28 giờ ước lượng / 27,9 giờ thực tế) để board có burndown
   và Sprint report thật — cách nhập xem [Setup-Guide.md](Setup-Guide.md) bước 10.

**Việc còn lại của nhóm:** dọn Jira thật, đẩy GitHub + bật Actions, dựng Jenkins job, và **quay video**
theo kịch bản ở mục 8.

---

## 0. Lý do làm bản v2 & nguyên tắc rút gọn

Bản v1 đã chạy được nhưng **quá nặng so với mục tiêu học tập**: 7 module Maven, ~144 unit test, 36 Selenium test, profile `ui`/`mobile`, Selenium Grid, chạy song song, retry analyzer, một module riêng chỉ để gộp báo cáo JaCoCo. Hệ quả: khó mở trong IDE, khó giải thích gọn trong video, và người học khó tự dựng lại từ đầu.

**Quyết định đã chốt cho v2:**

| Vấn đề | Quyết định |
| --- | --- |
| Số lượng tool | **Giữ nguyên đủ 9 tool** (đề bài chấm điểm phần "kết hợp tool") |
| Độ sâu mỗi tool | Mỗi tool chỉ giữ **2–3 ví dụ tối thiểu**, bỏ hết phần nâng cao |
| Cấu trúc dự án | **Gộp 7 module thành 1 Maven project duy nhất** |
| IDE | **Eclipse** (thay NetBeans) – import bằng *Existing Maven Projects* |
| Số test case | Cắt còn khoảng **1/3** (chi tiết ở mục 4) |

**Ba nguyên tắc xuyên suốt v2:**

1. **Một dự án – một `pom.xml` – một lệnh chạy.** Người xem video phải hiểu được cấu trúc trong 30 giây.
2. **Mỗi tool giữ đúng một mắt xích**, minh họa bằng ví dụ nhỏ nhất mà vẫn thấy rõ vai trò.
3. **Không hy sinh phần kiến thức môn học**: 5 kỹ thuật thiết kế test case (phân hoạch tương đương, giá trị biên, bảng quyết định, sơ đồ chuyển trạng thái, phủ mã lệnh) **giữ nguyên 100%** — đây vừa là phần chấm điểm nặng nhất, vừa là phần "sử dụng các phần đã học của môn".

---

## 1. BẢNG GIỮ / BỎ / GỘP (đối chiếu hiện trạng → sau rút gọn)

### 1.1 Cấu trúc dự án

| Hiện tại (v1) | v2 | Xử lý |
| --- | --- | --- |
| `automation/pom.xml` (parent, 7 module, 2 profile) | 1 `pom.xml` đặt tại `BTCK/` | **GỘP** – bỏ `<modules>`, bỏ `<profiles>`, bỏ `<dependencyManagement>` (khai báo version thẳng cho dễ đọc) |
| `automation/sut-core/` | `src/main/java/` | **GỘP** – code nghiệp vụ về `src/main/java` |
| `automation/test-support/` (`LocalWebServer`) | `src/main/java/.../support/` | **GỘP** – 1 lớp, không đáng một module |
| `automation/unit-tests/` | `src/test/java/.../unit/` | **GỘP** |
| `automation/web-selenium/` | `src/test/java/.../web/` | **GỘP** |
| `automation/web-playwright/` | `src/test/java/.../pw/` | **GỘP** |
| `automation/mobile-appium/` | `src/test/java/.../mobile/` | **GỘP** |
| `automation/coverage-report/` (JaCoCo aggregate) | — | **BỎ** – khi chỉ còn 1 module thì không cần gộp; khai báo `jacoco-maven-plugin` thẳng trong `pom.xml` |
| `sut-web/` (HTML/CSS/JS) | giữ nguyên vị trí ở gốc | **GIỮ** – đường dẫn tương đối không đổi, lại mở `index.html` xem trực tiếp được |

### 1.2 Tính năng kỹ thuật

| Thành phần | v2 | Lý do |
| --- | --- | --- |
| Profile `-Pui`, `-Pmobile` | **BỎ** | Chỉ còn 1 module, `mvn test` là đủ |
| Selenium **Grid** | **BỎ khỏi code**, chỉ **nói + chiếu 1 phút** trong video | Phải chạy thêm một tiến trình, dễ hỏng lúc quay, giá trị học tập thấp so với chi phí |
| `parallel="classes" thread-count="2"` | **BỎ** (chạy tuần tự) | Chính là nguồn gốc DEF-01 trong `Report.md`; bỏ đi thì suite ổn định hẳn |
| `RetryAnalyzer` + listener retry | **BỎ** | Che giấu test flaky, khó giải thích cho người mới học |
| `testng-debug.xml`, `testng-smoke.xml` | **BỎ**, chỉ giữ `testng.xml` | 3 file suite cho 10 test case là thừa |
| `WebDriverFactory` (local/grid/firefox/headless) | **RÚT GỌN** còn Chrome + cờ headless | Bỏ nhánh Grid và nhánh Firefox |
| Data-driven bằng `login-data.csv` | **GIỮ** | Đúng thế mạnh `@DataProvider` của TestNG, chỉ cần 4 dòng dữ liệu |
| Page Object Model (5 page class) | **GIỮ** | Kiến thức cốt lõi của automation web, không cắt |
| EasyMock | **GIỮ** – 2 test case | Yêu cầu của Bài 10 |
| JaCoCo | **GIỮ** – chạy thẳng `mvn test`, mở `target/site/jacoco/index.html` | Chứng minh "phủ mã lệnh" của Bài 7 |
| Allure | **GIỮ** – annotation cơ bản + link ngược Jira | Điểm hội tụ của báo cáo |
| 3 file `.bat` | **GỘP** còn `ChayTest.bat` | Một lệnh chạy tất cả |

### 1.3 Số lượng test case

| Tầng | v1 | v2 (mục tiêu) | Cách cắt |
| --- | --- | --- | --- |
| Unit (JUnit 5) | 144 | **~40** | Giảm số dòng trong `@CsvSource`/`@MethodSource`, giữ đúng các giá trị biên tiêu biểu |
| Web UI (TestNG + Selenium) | 36 | **10** | Login 4, Cart 3, Checkout 2, E2E 1 |
| Playwright | 5 | **3** | Login đúng, login sai, thêm giỏ hàng |
| Appium | 3 | **2** | Mở app, kiểm tra một màn hình |
| **Tổng** | **188** | **~55** | Vẫn vượt xa mức "tối thiểu 20 test case" của Bài 8 |

> **Nguyên tắc cắt:** cắt theo *số dòng dữ liệu*, **không cắt theo kỹ thuật**. Mỗi kỹ thuật trong `TestDesign-Techniques.md` phải còn ít nhất 2 test case đại diện để ma trận truy vết không bị thủng.

### 1.4 Tài liệu

| File | v2 |
| --- | --- |
| `Requirements.md`, `DeBai-SUT.md` | **GIỮ** nguyên |
| `Plan.md` | **THAY** bằng bản v2 này |
| `TestDesign-Techniques.md` | **GIỮ** – phần lõi kiến thức, chỉ sửa lại số lượng TC cho khớp |
| `TestCases.md` | **GIỮ**, cắt bảng cho khớp ~55 TC |
| `TraceabilityMatrix.md` | **GỘP** vào cuối `TestCases.md` |
| `TestPlan.md` | **GIỮ** nhưng rút còn khoảng 1 trang |
| `Setup-Guide.md` | **VIẾT LẠI** theo Eclipse (bỏ hết phần NetBeans) |
| `Report.md` | **GIỮ**, cập nhật số liệu sau khi cắt |

---

## 2. KIẾN TRÚC v2

### 2.1 Dây chuyền kết hợp tool (giữ tinh thần, bớt nhánh)

```text
   ┌──────────── JIRA (quản lý test case, ưu tiên, giao việc, bug) ────────────┐
   ▼                                                                          │
[1] Thiết kế TC ─► [2] GITHUB (branch/commit/PR gắn mã Jira)                   │
                          ├──► [3a] GITHUB ACTIONS : chạy unit test mỗi PR     │
                          └──► [3b] JENKINS        : chạy full suite + Allure  │
                                        │                                      │
                        ┌───────────────┼───────────────┬──────────────┐       │
                        ▼               ▼               ▼              ▼       │
                  [4] JUNIT 5     [5] TESTNG +    [6] PLAYWRIGHT  [7] APPIUM    │
                  + EasyMock       SELENIUM        (đối chứng)     (mobile)     │
                  + JaCoCo         (Page Object)                                │
                        └───────────────┴───────────────┴──────────────┘       │
                                        ▼                                      │
                              [8] ALLURE ── @Issue trỏ ngược ───────────────────┘
```

Khác v1: **bỏ nhánh Selenium Grid** và **bỏ chạy song song**. Mọi mắt xích còn lại giữ nguyên, nên phần trả lời phản biện "vì sao dùng tool này" không đổi.

### 2.2 Cấu trúc thư mục sau khi gộp

```text
BTCK/                                 ← đây chính là Eclipse project
├─ pom.xml                            ← DUY NHẤT 1 file pom
├─ sut-web/                           ← web app tĩnh (SUT tầng giao diện)
│  └─ index.html   css/style.css   js/app.js
├─ src/main/java/vn/edu/ktpm/minishop/
│  ├─ auth/     LoginService, User, UserRepository, LoginResult
│  ├─ shop/     Cart, Catalog, Product, DiscountPolicy, CheckoutService,
│  │            CheckoutValidator, ValidationResult, CartException
│  ├─ math/     Calculator (W8), Rational + Illegal (W11)
│  └─ support/  LocalWebServer          ← chuyển từ module test-support
├─ src/test/java/vn/edu/ktpm/minishop/
│  ├─ unit/     LoginServiceTest, CartTest, DiscountPolicyTest,
│  │            CheckoutValidatorTest, CheckoutServiceTest (EasyMock),
│  │            CalculatorTest, RationalTest              ← JUnit 5
│  ├─ web/      pages/ (5 lớp Page Object) + BaseTest, WebDriverFactory
│  │            tests/ LoginTest, CartTest, CheckoutTest, E2ETest   ← TestNG
│  ├─ pw/       MiniShopPlaywrightTest                     ← JUnit 5
│  └─ mobile/   MiniShopMobileTest                         ← JUnit 5
├─ src/test/resources/  testng.xml, login-data.csv, allure.properties
├─ docs/       (xem mục 1.4)
├─ ci/         Jenkinsfile, workflows/ci.yml
├─ selenium-ide/  *.side
└─ ChayTest.bat
```

### 2.3 Điểm kỹ thuật phải xử lý khi gộp (quan trọng nhất)

Khi **JUnit 5 và TestNG cùng nằm trong một module**, `maven-surefire-plugin` mặc định chỉ chọn **một** provider ⇒ có nguy cơ **âm thầm bỏ qua** một trong hai nhóm test mà build vẫn báo SUCCESS. Xử lý theo thứ tự ưu tiên:

1. **Cách chính:** thêm dependency `org.junit.support:testng-engine` để TestNG chạy **trên JUnit Platform** ⇒ `mvn test` chạy được cả hai loại.
   *Nghiệm thu bắt buộc:* sau khi gộp, `mvn test` phải in tổng số test **≥ 50**. Nếu chỉ thấy ~40 (thiếu nhóm UI) hoặc ~10 (thiếu nhóm unit) thì provider đang bỏ sót → **chưa đạt**, không được đi tiếp.
2. **Cách dự phòng:** để `mvn test` chỉ chạy JUnit 5, còn TestNG suite chạy bằng **plugin TestNG for Eclipse** (chuột phải `testng.xml` → *Run As → TestNG Suite*). Cách này lại rất hợp để quay video vì thao tác trực quan.

---

## 3. CÁC BƯỚC REFACTOR (làm tuần tự, mỗi bước chạy xanh mới sang bước sau)

> Mỗi bước = 1 commit riêng gắn mã Jira, để nếu hỏng thì `git revert` đúng một bước.

**B0 – Sao lưu.** Tạo nhánh `backup/v1-daydu` và đẩy lên GitHub (giữ bản đầy đủ để đối chiếu, phòng khi cần quay lại). Plan v1 đã lưu tại `docs/Plan-v1-daydu.md.bak`.

**B1 – Viết `pom.xml` mới ở `BTCK/`.** Một file duy nhất, gồm:
- `maven.compiler.release` 17, encoding UTF-8
- dependency: `junit-jupiter`, `testng` + `testng-engine`, `easymock`, `selenium-java`, `playwright`, `appium java-client`, `allure-junit5`, `allure-testng`, `aspectjweaver`
- plugin: `maven-compiler-plugin`, `maven-surefire-plugin` (cấu hình `argLine` cho AspectJ của Allure), `jacoco-maven-plugin` (`prepare-agent` + `report`)
- **Không** `<modules>`, **không** `<profiles>`, **không** `<dependencyManagement>`

**B2 – Di chuyển mã nguồn.**
- `automation/sut-core/src/main/java/**` → `src/main/java/**`
- `automation/test-support/src/main/java/**` → `src/main/java/**` (cùng cây package)
- `automation/*/src/test/java/**` → `src/test/java/**`, đổi package con cho khớp mục 2.2
- `automation/*/src/test/resources/**` → `src/test/resources/` (gộp 3 file `allure.properties` thành 1)
- Xoá thư mục `automation/` sau khi kiểm tra không sót file

**B3 – Rút gọn `WebDriverFactory` và `BaseTest`.** Chỉ còn Chrome + cờ `-Dheadless=true|false`; xoá nhánh Grid, nhánh Firefox; xoá `RetryAnalyzer.java`.

**B4 – Rút gọn `testng.xml`.** Bỏ `parallel`/`thread-count`, bỏ listener retry, giữ listener Allure; xoá `testng-debug.xml` và `testng-smoke.xml`.

**B5 – Chuyển Playwright và Appium sang JUnit 5.** Sau bước này chỉ nhóm Selenium dùng TestNG ⇒ ranh giới "JUnit cho tầng unit, TestNG cho tầng UI" vẫn rõ mà lại bớt được một mặt phải cấu hình.

**B6 – Cắt test case** theo bảng mục 4, đồng thời cập nhật `TestCases.md`.

**B7 – Chạy nghiệm thu.** `mvn clean test` xanh, tổng số test ≥ 50; `target/site/jacoco/index.html` mở được; `allure serve target/allure-results` mở được.

**B8 – Gộp `.bat`** còn một file `ChayTest.bat` (gọi `..\apache-maven-3.9.16\bin\mvn.cmd clean test`).

**B9 – Cập nhật tài liệu** theo bảng 1.4 (đặc biệt `Setup-Guide.md` chuyển sang Eclipse) và `README.md`.

---

## 4. CHI TIẾT CẮT TEST CASE (bảng làm việc cho B6)

| File test | TC hiện có | TC giữ lại | Giữ những gì (theo kỹ thuật đã học) |
| --- | --- | --- | --- |
| `unit/LoginServiceTest` | 10 | **6** | Đăng nhập đúng; sai 1 lần; sai lần 3 → khoá; hết 15 phút tự mở; user không tồn tại → **sơ đồ chuyển trạng thái (W6)** phủ đủ transition |
| `unit/CartTest` | 10 | **6** | **Giá trị biên (W7)** cho số lượng: 0, 1, 10, 11 + thêm/xoá sản phẩm |
| `unit/DiscountPolicyTest` | 8 | **5** | **Bảng quyết định (W5)**: mỗi rule sau rút gọn 1 test case |
| `unit/CheckoutValidatorTest` | 11 | **6** | **Phân hoạch tương đương (W7)**: họ tên, địa chỉ, số điện thoại (hợp lệ / rỗng / quá dài / sai định dạng) |
| `unit/CheckoutServiceTest` | 4 | **2** | **Mock bằng EasyMock (W10)**: 1 ca thành công, 1 ca phụ thuộc trả lỗi |
| `unit/CalculatorTest` | 8 | **6** | Bài 8: add/sub/mul/div + **equals & notEquals** + `assertThrows` chia 0 |
| `unit/RationalTest` | 14 | **8** | Bài 11: mẫu số 0 → `Illegal`, rút gọn phân số, 4 phép toán, `equals`, `toString`; kèm **flow graph + cyclomatic** cho `divide()` |
| `web/LoginTest` | 6 | **4** | Đúng; sai mật khẩu; bỏ trống; tài khoản bị khoá (**`@DataProvider` đọc CSV**) |
| `web/CartTest` | 10 | **3** | Thêm; xoá; kiểm tra tổng tiền |
| `web/CheckoutTest` | 4 | **2** | Thành công; thiếu thông tin bắt buộc |
| `web/E2ETest` | 1 | **1** | Luồng đầu–cuối: login → chọn hàng → thanh toán |
| `pw/MiniShopPlaywrightTest` | 5 | **3** | Đúng 3 TC trùng với Selenium để lập bảng so sánh |
| `mobile/MiniShopMobileTest` | 3 | **2** | Mở app + kiểm tra một màn hình |

**Tổng sau cắt: ~54 test case** — đủ minh chứng, đủ thời lượng demo, và **vẫn phủ hết 5 kỹ thuật**.

---

## 5. LÀM VIỆC VỚI ECLIPSE (thay NetBeans)

### 5.1 Cài đặt

1. Tải **Eclipse IDE for Java Developers** (bản 2024-03 trở lên) – đã tích hợp sẵn **m2e**, không cần cài thêm gì cho Maven.
2. *Window → Preferences → Java → Installed JREs* → trỏ tới **JDK 17**.
3. *Help → Eclipse Marketplace* → cài **TestNG for Eclipse** (để chạy `testng.xml` bằng chuột phải).
4. Tuỳ chọn: **EGit** (thường có sẵn) để commit/push thẳng từ Eclipse, quay video cho liền mạch với phần GitHub.

### 5.2 Import dự án

*File → Import… → Maven → Existing Maven Projects* → chọn thư mục `BTCK` → Finish.
Eclipse tự đọc `pom.xml`, tự tải thư viện về `~/.m2`, tự dựng Build Path. **Không phải tải JAR thủ công.**

### 5.3 Cách chạy từng thứ trong Eclipse (cũng chính là thao tác sẽ quay video)

| Việc cần chạy | Thao tác trong Eclipse |
| --- | --- |
| 1 lớp unit test | Chuột phải `LoginServiceTest.java` → *Run As → JUnit Test* |
| Toàn bộ unit test | Chuột phải package `unit` → *Run As → JUnit Test* |
| Suite UI (TestNG) | Chuột phải `src/test/resources/testng.xml` → *Run As → TestNG Suite* |
| Toàn bộ qua Maven | Chuột phải project → *Run As → Maven test* |
| Báo cáo phủ mã | *Run As → Maven build…* với goal `test jacoco:report`, rồi mở `target/site/jacoco/index.html` |
| Chạy web app (SUT) | *Run As → Java Application* trên `LocalWebServer` → mở `http://localhost:8080` |

> **Mẹo quay video:** tạo sẵn các **Run Configuration** đặt tên rõ ràng (`1-Unit test`, `2-UI test`, `3-Coverage`) để lúc quay chỉ bấm chọn, không phải mò menu.

---

## 6. CÁC TOOL CÒN LẠI – MỨC TỐI GIẢN

| Tool | Mức tối giản cho v2 | Thời lượng demo |
| --- | --- | --- |
| **Jira** | 1 project Scrum, 5 Epic, 24 issue test case, 3 Task, **2 Bug thật**, 1 sprint 16/08–22/08 (nhập bằng `docs/jira-import.csv`, đã có sẵn ngày tạo / hạn / ngày hoàn thành / ước lượng / thời gian đã dùng ⇒ burndown và Sprint report có dữ liệu thật) | 2' |
| **GitHub** | 1 repo public, quy ước nhánh `feature/KTPM-xx-...`, 1 PR có review chéo | 1'30 |
| **GitHub Actions** | 1 workflow **chỉ chạy unit test** khi push/PR (không chạy UI trên cloud) | 1' |
| **Jenkins** | 1 Pipeline job, 4 stage: `Checkout → Build → Test → Allure` | 2' |
| **JUnit 5** | Annotation cơ bản + `@ParameterizedTest` + `assertThrows` + `@Nested` (bỏ `@RepeatedTest`, `@Order`, `assertTimeout`) | 3' |
| **TestNG** | `testng.xml` + `@DataProvider` + `priority` + `@BeforeMethod/@AfterMethod` (bỏ groups, parallel, retry, `dependsOnMethods`) | 2'30 |
| **Selenium** | IDE record 1 kịch bản + WebDriver Page Object + explicit wait (**nói về Grid nhưng không chạy**) | 3' |
| **Playwright** | 3 TC + trace viewer | 2' |
| **Appium** | 2 TC trên Emulator; máy yếu thì chỉ chiếu Appium Inspector + ảnh chụp | 1'30 |
| **Allure** | `@Epic/@Feature/@Severity/@Issue` + `allure serve` | 1'30 |

---

## 7. LỊCH TRÌNH (19/08 → 22/08/2026)

| Ngày | Nội dung | Đầu ra kiểm chứng được |
| --- | --- | --- |
| **D1 – 19/08 (hôm nay)** | Duyệt plan v2 → làm B0…B5: sao lưu, viết `pom.xml` mới, gộp code về 1 project, rút gọn `WebDriverFactory`/`testng.xml`, chuyển Playwright + Appium sang JUnit 5 | `mvn clean test` **xanh**, tổng test ≥ 50; import Eclipse thành công |
| **D2 – 20/08** | B6…B9: cắt test case còn ~54, gộp `.bat`, cập nhật `TestCases.md` + gộp RTM, viết lại `Setup-Guide.md` theo Eclipse, cập nhật `Report.md` và `README.md` | Docs khớp code; JaCoCo + Allure mở được |
| **D3 – 21/08** | Dọn Jira còn ~20 issue; đẩy GitHub + chạy Actions; dựng Jenkins job; **diễn tập trọn kịch bản video 1 lần** và bấm giờ | Actions xanh, Jenkins có build SUCCESS, ghi lại các chỗ vấp khi diễn tập |
| **D4 – 22/08** | Quay video theo mục 8, dựng, rà checklist mục 10, nộp | Video hoàn chỉnh, tag `v2.0`, nộp trước 22:00 |

**Thứ tự hy sinh nếu trễ:** ① Appium (thay bằng 1'30 giải thích + ảnh) → ② Jenkins (thay bằng GitHub Actions + Allure trên GitHub Pages) → ③ Playwright giảm còn 2 TC.
**Tuyệt đối không cắt:** phần thiết kế test case (5 kỹ thuật) và phần Jira ↔ GitHub ↔ Allure.

---

## 8. KỊCH BẢN VIDEO RÚT GỌN (mục tiêu **18–20 phút**, giảm từ 25 phút)

| # | Thời lượng | Nội dung | Người trình bày |
| --- | --- | --- | --- |
| 1 | 1'30 | Giới thiệu nhóm, đề tài, **chiếu sơ đồ dây chuyền mục 2.1** | A |
| 2 | 1'30 | SUT MiniShop + phạm vi kiểm thử | B |
| 3 | 2'30 | **Cài đặt**: JDK, Maven, **Eclipse + import Existing Maven Projects**, plugin TestNG, Allure CLI (tua nhanh) | A |
| 4 | 3'30 | **Thiết kế test case**: phân hoạch tương đương, giá trị biên, bảng quyết định, sơ đồ chuyển trạng thái, flow graph + cyclomatic | B |
| 5 | 2' | **Jira**: test case, độ ưu tiên, giao việc, sprint; liên kết GitHub | B |
| 6 | 3' | **JUnit 5 + EasyMock + JaCoCo** chạy ngay trong Eclipse (*Run As → JUnit Test*), TDD đỏ → xanh | A |
| 7 | 3' | **TestNG + Selenium**: IDE record → Page Object → *Run As → TestNG Suite* | A |
| 8 | 2' | **Playwright** 3 TC + trace viewer + **bảng so sánh với Selenium** | B |
| 9 | 2' | **CI/CD + Allure**: push → Actions → Jenkins → Allure → Bug trên Jira → fix → xanh | A + B |
| 10 | 1' | Kết quả, bài học, hạn chế — nói rõ **vì sao đã lược bỏ Grid / parallel / retry** (điểm cộng về tư duy kỹ thuật) | A + B |

**Chuẩn bị quay:** OBS 1920×1080; phóng to font Eclipse (*Preferences → General → Appearance → Colors and Fonts*); tắt thông báo; chạy thử trọn bộ 1 lần trước khi quay; chuẩn bị clip dự phòng cho Appium và Jenkins.

---

## 9. RỦI RO CỦA CHÍNH VIỆC RÚT GỌN

| Rủi ro | Ảnh hưởng | Xử lý |
| --- | --- | --- |
| Gộp module làm hỏng đường dẫn tài nguyên (`sut-web`, CSV, `allure.properties`) | Test đỏ hàng loạt sau B2 | Chạy `mvn test` ngay sau mỗi bước B; giữ `sut-web` ở gốc nên đường dẫn tương đối không đổi |
| Surefire chỉ chạy một provider ⇒ **âm thầm bỏ test** | Tưởng xanh nhưng thực ra thiếu hẳn một tầng | Nghiệm thu bằng **tổng số test ≥ 50** (mục 2.3), không chỉ nhìn dòng BUILD SUCCESS |
| Bỏ Grid/parallel bị hỏi "sao không dùng?" | Mất điểm phản biện | Chủ động nói ở mục 10 của video: đã thử ở v1, ghi nhận DEF-01 (suite sập khi chạy 3 luồng), quyết định bỏ vì chi phí lớn hơn lợi ích ở quy mô này — **có bằng chứng trong `Report.md`** |
| Cắt test case làm thủng ma trận truy vết | Mất điểm truy vết | Cắt theo dòng dữ liệu; mỗi kỹ thuật giữ ≥ 2 TC; cập nhật RTM trong cùng commit |
| Eclipse báo `Plugin execution not covered by lifecycle` với JaCoCo | Project đỏ trong Eclipse dù `mvn` chạy tốt | Dùng m2e bản mới, hoặc *Quick Fix → Mark goal as ignored in Eclipse build*; nên giải thích luôn trong video vì đây là lỗi kinh điển của m2e |
| Chỉ còn 3,5 ngày | Không kịp quay | D1–D2 chỉ động vào code/docs, **D3 diễn tập, D4 chỉ quay** — không sửa code từ D4 |

---

## 10. CHECKLIST NGHIỆM THU v2

**Về code**
- [ ] Đúng **1 file `pom.xml`**, không còn thư mục `automation/`
- [ ] Import vào Eclipse bằng *Existing Maven Projects* chạy được ngay, Build Path không lỗi đỏ
- [ ] `mvn clean test` xanh và in tổng số test **≥ 50**
- [ ] Chạy được trong Eclipse: *Run As → JUnit Test* và *Run As → TestNG Suite*
- [ ] `target/site/jacoco/index.html` có số liệu; `allure serve` mở được report
- [ ] Không còn: profile, module `coverage-report`, `RetryAnalyzer`, `parallel`, nhánh Grid, 2 file suite thừa

**Về tài liệu**
- [ ] `Setup-Guide.md` viết theo Eclipse, không còn nhắc NetBeans/IntelliJ
- [ ] `TestCases.md` khớp số TC thực tế và đã gộp ma trận truy vết
- [ ] `Report.md` cập nhật số liệu mới, **giữ phần giải thích vì sao bỏ Grid/parallel**
- [ ] `README.md`: cấu trúc mới, hướng dẫn import Eclipse, lệnh chạy

**Về bài nộp**
- [ ] Đủ 9 tool xuất hiện trong video kèm vai trò riêng
- [ ] Đủ 5 kỹ thuật đã học: phân hoạch tương đương, giá trị biên, bảng quyết định, sơ đồ chuyển trạng thái, phủ mã lệnh
- [ ] Video 18–20 phút, có tiếng của **cả 2 thành viên**
- [ ] Repo có nhánh `backup/v1-daydu` (bản đầy đủ) và tag `v2.0` (bản rút gọn)
- [ ] Nộp trước **00:00:00 ngày 23/08/2026**

---

## 11. PHÂN CÔNG

| Việc | Thành viên A | Thành viên B |
| --- | --- | --- |
| Refactor B0–B5 (gộp project, `pom.xml`, Selenium) | ✔ | |
| Cắt test case B6 + cập nhật `TestCases.md`/RTM | | ✔ |
| Viết lại `Setup-Guide.md` theo Eclipse + `README.md` | | ✔ |
| Jenkins + GitHub Actions + Allure | ✔ | |
| Dọn Jira còn ~20 issue, chuẩn bị Bug demo | | ✔ |
| Diễn tập & quay | Mục 3, 6, 7, 9 | Mục 2, 4, 5, 8, 9 |

---

**Bước kế tiếp ngay sau khi plan v2 được duyệt:** làm **B0 → B2** (sao lưu nhánh `backup/v1-daydu`, viết `pom.xml` mới ở `BTCK/`, gộp toàn bộ mã nguồn về `src/main/java` và `src/test/java`), rồi chạy `mvn clean test` để nghiệm thu tổng số test.
