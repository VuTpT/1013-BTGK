# HƯỚNG DẪN CÀI ĐẶT STEP-BY-STEP (bản v2 — dùng **Eclipse**)

> Tài liệu này là kịch bản cho **mục 3 của video** ("Cài đặt"). Mỗi bước có ô **✅ Kiểm chứng** —
> quay đúng cảnh kiểm chứng đó là đủ chứng minh bước cài đặt thành công.
>
> Thứ tự bên dưới đã được sắp để **bước nào cần cho demo sớm thì cài trước**; các bước đánh dấu
> *(tùy chọn)* có thể bỏ nếu thiếu thời gian mà không ảnh hưởng phần chấm chính.

## Bảng công cụ

| Công cụ | Bắt buộc | Dùng cho |
| --- | --- | --- |
| JDK 17 | ✅ | Biên dịch và chạy toàn bộ dự án |
| Maven 3.9 | ✅ | Quản lý thư viện, chạy test bằng dòng lệnh |
| **Eclipse IDE for Java Developers** | ✅ | IDE chính (thay NetBeans của bản v1) |
| Chrome | ✅ | Tầng UI (Selenium + Playwright) |
| Git + tài khoản GitHub | ✅ | Quản lý mã nguồn, GitHub Actions |
| Jira Cloud (free) | ✅ | Quản lý test case, sprint, bug |
| Allure CLI | ✅ | Xem báo cáo kết quả |
| Selenium IDE (extension) | Tùy chọn | Record & playback trong video |
| Jenkins | Tùy chọn | Dây chuyền CI chạy tại máy |
| Android Studio + Appium | Tùy chọn | Tầng mobile |

---

## Bước 1 — JDK 17

1. Tải **JDK 17 LTS** (Oracle hoặc Temurin), cài với tùy chọn mặc định.
2. Đặt biến môi trường `JAVA_HOME` trỏ tới thư mục cài đặt, thêm `%JAVA_HOME%\bin` vào `PATH`.

✅ **Kiểm chứng**: mở PowerShell, gõ `java -version` → hiện `17.0.x`.

---

## Bước 2 — Maven

1. Tải **Apache Maven 3.9.x** (bản binary zip), giải nén vào thư mục không có dấu cách,
   ví dụ `D:\Tool\apache-maven-3.9.16`.
2. Thêm `D:\Tool\apache-maven-3.9.16\bin` vào biến `PATH` của người dùng.
3. Mở **cửa sổ terminal mới** (PATH chỉ có hiệu lực với tiến trình mới).

✅ **Kiểm chứng**: `mvn -v` → hiện `Apache Maven 3.9.16` kèm đúng đường dẫn JDK 17.

> Nếu báo `'mvn' is not recognized`: PATH chưa đúng hoặc chưa mở terminal mới.

---

## Bước 3 — Eclipse và import dự án

1. Tải **Eclipse IDE for Java Developers** (bản 2024-03 trở lên) tại eclipse.org, giải nén và chạy.
   Gói này **đã có sẵn m2e** (plugin Maven) nên không phải cài thêm gì cho Maven.
2. *Window → Preferences → Java → Installed JREs* → bảo đảm **JDK 17** được chọn.
3. Import dự án: *File → Import… → Maven → **Existing Maven Projects*** → **Browse** tới thư mục
   `BTCK` → thấy `/pom.xml  vn.edu.ktpm:minishop-automation:2.0.0` → **Finish**.
4. Chờ Eclipse tải thư viện lần đầu (thanh tiến trình góc dưới phải, cần Internet, khoảng 1–3 phút).

✅ **Kiểm chứng**: cây **Project Explorer** hiển thị `minishop-automation` với 4 nút:
`src/main/java`, `src/test/java`, `src/test/resources`, `Maven Dependencies` — và **không có dấu ✕ đỏ**.

> **Lỗi kinh điển của m2e**: nếu `pom.xml` gạch đỏ với thông báo
> *"Plugin execution not covered by lifecycle configuration: jacoco-maven-plugin"* →
> nhấn chuột phải vào dòng lỗi → *Quick Fix* → *Mark goal as ignored in Eclipse build*.
> Lỗi này **chỉ ảnh hưởng Eclipse**, `mvn test` ngoài dòng lệnh vẫn chạy bình thường.
> Đây là điểm nên giải thích trong video.

---

## Bước 4 — Cài plugin TestNG cho Eclipse

1. *Help → Eclipse Marketplace…* → tìm **“TestNG”** → cài **TestNG for Eclipse** → khởi động lại Eclipse.

✅ **Kiểm chứng**: chuột phải vào `src/test/resources/testng.xml` → menu *Run As* có mục **TestNG Suite**.

---

## Bước 5 — Chạy thử toàn bộ dự án

Ba cách, nên quay cả ba trong video vì mỗi cách minh họa một công cụ:

| Cách | Thao tác | Kết quả mong đợi |
| --- | --- | --- |
| Trong Eclipse — JUnit | Chuột phải gói `vn.edu.ktpm.minishop.unit` → *Run As → JUnit Test* | Thanh **xanh**, 64 test |
| Trong Eclipse — TestNG | Chuột phải `src/test/resources/testng.xml` → *Run As → TestNG Suite* | 11 test pass |
| Dòng lệnh — Maven | Trong thư mục `BTCK`: `mvn clean test` | `Tests run: 80, Failures: 0, Errors: 0, Skipped: 2` |

✅ **Kiểm chứng quan trọng nhất**: dòng tổng kết phải là **`Tests run: 80`**.
Nếu chỉ thấy ~11 hoặc ~64 nghĩa là Surefire đang bỏ sót một tầng test — xem DEF-03 trong
[Report.md](Report.md) để biết cách xử lý.

> Trên Windows có thể bấm trực tiếp `ChayTest.bat` (chạy ẩn trình duyệt),
> `ChayTest.bat hien` (hiện trình duyệt để quay video).

---

## Bước 6 — Xem SUT bằng mắt

1. Trong Eclipse mở `src/main/java/vn/edu/ktpm/minishop/support/LocalWebServer.java`.
2. Hoặc đơn giản: mở thẳng tập tin `sut-web/index.html` bằng Chrome.

✅ **Kiểm chứng**: hiện màn hình đăng nhập MiniShop; đăng nhập `standard_user` / `secret_sauce` vào được trang sản phẩm.

---

## Bước 7 — Allure CLI (báo cáo)

1. Cài Node.js LTS rồi chạy: `npm install -g allure-commandline`
   (hoặc `scoop install allure` nếu đã có scoop).
2. Sau khi chạy test, trong thư mục `BTCK` gõ: `allure serve target/allure-results`

✅ **Kiểm chứng**: trình duyệt tự mở dashboard Allure, tab **Suites** thấy đủ cả 3 tầng
(`unit`, `web`, `pw`); bấm vào một test có gắn `@Issue` sẽ nhảy sang Jira.

> Muốn link Allure → Jira hoạt động, sửa `src/test/resources/allure.properties`,
> thay `<ten-site>` bằng tên site Jira của nhóm.

---

## Bước 8 — Báo cáo độ phủ (JaCoCo)

1. Chạy `mvn test jacoco:report` (hoặc trong Eclipse: *Run As → Maven build…* với goal `test jacoco:report`).
2. Mở `target/site/jacoco/index.html`.

✅ **Kiểm chứng**: bảng độ phủ hiện ~**83,8%** instruction, ~**78,7%** branch.

---

## Bước 9 — Git và GitHub

1. Cài **Git for Windows**. Tạo repo public trên GitHub, ví dụ `KTPM-Automation-Seminar`.
2. Trong thư mục dự án:

   ```bash
   git init
   git add .
   git commit -m "KTPM-1 khoi tao du an MiniShop automation"
   git branch -M main
   git remote add origin https://github.com/<tai-khoan>/KTPM-Automation-Seminar.git
   git push -u origin main
   ```

3. Chép `ci/workflows/ci.yml` sang `.github/workflows/ci.yml` ở **thư mục gốc của repo** rồi push.

✅ **Kiểm chứng**: tab **Actions** trên GitHub hiện workflow chạy và **xanh**.

> Eclipse có sẵn **EGit**: có thể commit/push thẳng từ IDE (*Team → Commit…*) để quay video liền mạch.

---

## Bước 10 — Jira Cloud

1. Tạo site Jira Cloud free, tạo project **KTPM** (template **Scrum**).
2. *Project settings → Issue types* → thêm loại **Test** (hoặc dùng nhãn `test-case`).
3. Bật các trường thời gian trước khi nhập:
   - *Project settings → Fields*: bảo đảm có **Due date**
   - *Jira settings → Issues → Time tracking*: bật **Time Tracking** (đơn vị mặc định: giờ)
4. Nhập hàng loạt test case: *Settings → System → External System Import → CSV* → chọn
   [jira-import.csv](jira-import.csv). Ở màn hình ánh xạ trường:

   | Cột trong CSV | Trường Jira | Lưu ý |
   | --- | --- | --- |
   | `Created`, `Due Date`, `Resolved` | Created / Due date / Resolved | Chọn **Date format** = `dd/MM/yyyy HH:mm` |
   | `Original Estimate`, `Time Spent` | Original Estimate / Time Spent | Giá trị tính bằng **giây** (3600 = 1 giờ) |
   | `Sprint` | Sprint | Jira tự tạo sprint `KTPM Sprint 1` nếu chưa có |
   | `Epic Link` | Epic Link | Khớp theo cột `Summary` của 5 Epic |

5. Mở sprint: *Backlog → KTPM Sprint 1 → Start sprint*, đặt ngày **16/08/2026 → 22/08/2026**
   (đúng khoảng ngày trong file CSV thì burndown mới vẽ đúng).
6. Cài app **GitHub for Jira** để issue hiển thị nhánh/commit/PR.

✅ **Kiểm chứng**: board Scrum có 36 issue (5 Epic · 24 Test · 3 Task · 2 Bug), 34 issue ở trạng thái
**Done**; mở một issue thấy đủ **Created / Due date / Resolved / Original Estimate / Time Spent**;
biểu đồ **Burndown** và **Sprint report** có dữ liệu.

> Tổng công sức đã ghi nhận: **ước lượng 28 giờ · thực tế 27,9 giờ** cho cả nhóm 2 người trong 1 tuần —
> con số này lên thẳng *Sprint report*, rất đáng chiếu trong video ở phần quản lý tiến độ.

---

## Bước 10b — Nối Allure với Jira (hai chiều)

Bước này ghép hai công cụ lại thành vòng khép kín. Code đã gắn sẵn, chỉ còn phần cấu hình.

### Chiều xuôi: từ Allure bấm sang Jira

Đã làm sẵn trong mã nguồn, chỉ cần kiểm tra:

1. [`src/test/resources/allure.properties`](../src/test/resources/allure.properties) trỏ đúng site
   Jira của nhóm (thay phần `<ten-site>` nếu tạo site mới).
2. Mỗi test đã gắn `@TmsLink("KTPM-xxx")` theo ma trận truy vết mục 5.1 của
   [TestCases.md](TestCases.md) — tổng 56 test, phủ đủ 24 mã.

> Phân biệt hai annotation: `@TmsLink` dùng cho issue **loại Test** (KTPM-101…151, hiện ở mục
> *Test cases* của report), `@Issue` dùng cho issue **loại Bug** (KTPM-191, 192, hiện ở mục
> *Defects*). Dùng nhầm sẽ làm report hiển thị test case như một lỗi.

✅ **Kiểm chứng**: `allure serve target/allure-results` → mở một test bất kỳ → mục **Links** có mã
KTPM bấm được, mở đúng issue trên Jira.

### Chiều ngược: CI đẩy kết quả về Jira

1. **Bật GitHub Pages**: *Settings → Pages → Source* = **Deploy from a branch**, chọn nhánh
   `gh-pages`, thư mục `/ (root)`. Nhánh này workflow tự tạo ở lần chạy đầu.
2. **Tạo API token**: vào `id.atlassian.com/manage-profile/security/api-tokens` → *Create API token*
   → copy lại (chỉ hiện một lần).
3. **Khai secrets**: *Settings → Secrets and variables → Actions → New repository secret*:

   | Tên secret | Giá trị |
   | --- | --- |
   | `JIRA_BASE_URL` | `https://sinhvien-team-xej6z3t8.atlassian.net` (không có dấu `/` cuối) |
   | `JIRA_EMAIL` | email tài khoản Atlassian |
   | `JIRA_API_TOKEN` | token vừa tạo ở bước 2 |

4. *(tuỳ chọn)* Tab **Variables** → thêm `JIRA_LAUNCH_ISSUE` = mã issue nhận bình luận tổng kết.
   Không khai thì mặc định là `KTPM-1`.

Sau khi push vào `main`, workflow sẽ tự động: sinh báo cáo Allure HTML → publish lên GitHub Pages →
bình luận vào Jira. Test nào hỏng thì [`ci/notify-jira.py`](../ci/notify-jira.py) đọc `@TmsLink` của
test đó và bình luận thẳng vào đúng issue KTPM tương ứng.

**Chạy thử tại máy trước khi push** (không gọi Jira thật):

```bash
mvn clean test
python ci/notify-jira.py --results target/allure-results     --report-url "https://vutpt.github.io/1013-BTGK/"     --launch-issue KTPM-1 --build "thu-tai-may" --dry-run
```

Thêm `--create-bug KTPM` nếu muốn CI tự tạo issue **Bug** mỗi khi có test hỏng (đúng kịch bản cảnh 9).

✅ **Kiểm chứng**: tab **Actions** xanh → mở `https://<tai-khoan>.github.io/<ten-repo>/` thấy dashboard
Allure → mở issue `KTPM-1` trên Jira thấy bình luận mới kèm link báo cáo.

> Allure bản mã nguồn mở **không** đẩy thẳng kết quả vào Jira Cloud được: plugin chính thức
> (`ALLURE_JIRA_ENABLED`) bắt buộc app *Allure for Jira*, mà app đó chỉ có bản **Jira Server** và đã
> ngừng hỗ trợ. Vì vậy chiều ngược ở đây đi bằng REST API v2 của Jira Cloud.

---

## Bước 11 *(tùy chọn)* — Selenium IDE

1. Cài extension **Selenium IDE** cho Chrome.
2. Record kịch bản đăng nhập MiniShop → Play lại → *Export → Java JUnit*.
3. Lưu tập tin `.side` vào thư mục `selenium-ide/`.

✅ **Kiểm chứng**: chạy lại kịch bản đã record, tất cả bước hiện dấu tích xanh.

---

## Bước 12 *(tùy chọn)* — Jenkins

1. Tải `jenkins.war`, chạy `java -jar jenkins.war --httpPort=8080`, mở `http://localhost:8080`, unlock.
2. Cài plugin: **Git, Pipeline, Maven Integration, JUnit, Allure Jenkins Plugin, HTML Publisher**.
3. *Manage Jenkins → Tools*: khai báo JDK tên `jdk17`, Maven tên `maven3`, Allure Commandline tên `allure`.
4. *New Item → Pipeline* → *Pipeline script from SCM* → trỏ tới repo, đường dẫn script `BTCK/ci/Jenkinsfile`.

✅ **Kiểm chứng**: build thành công, trang job có mục **Allure Report** và **JaCoCo Coverage**.

---

## Bước 13 *(tùy chọn)* — Appium cho tầng mobile

1. `npm install -g appium` → `appium driver install uiautomator2`.
2. Cài **Android Studio**, tạo Emulator (API 30+ có sẵn Chrome), bật Emulator.
3. Chạy `appium` ở một cửa sổ terminal riêng.
4. Chạy test: `mvn test -Dmobile.tests=true`

✅ **Kiểm chứng**: `Tests run: 82` (thêm 2 test mobile so với 80 test thường ngày).

> Không bật Emulator thì hai test này **tự động bị bỏ qua** nhờ `@EnabledIfSystemProperty`,
> build vẫn xanh — nên có thể bỏ hẳn bước này nếu máy yếu.

---

## Danh sách kiểm tra cuối (rà trước khi quay video)

- [ ] `mvn -v` chạy được
- [ ] Eclipse import dự án không lỗi đỏ
- [ ] *Run As → JUnit Test* xanh (64 test)
- [ ] *Run As → TestNG Suite* xanh (11 test)
- [ ] `mvn clean test` in ra **`Tests run: 80`**
- [ ] `allure serve target/allure-results` mở được dashboard
- [ ] `target/site/jacoco/index.html` có số liệu
- [ ] Repo GitHub có Actions xanh
- [ ] Jira board có issue và sprint
- [ ] `allure.properties` trỏ đúng site Jira, mục **Links** trong report bấm sang Jira được
- [ ] Đã khai 3 secret `JIRA_*` và bật GitHub Pages nhánh `gh-pages`
- [ ] Trang `https://<tai-khoan>.github.io/<ten-repo>/` mở được báo cáo Allure
- [ ] Đã tạo sẵn Run Configuration đặt tên rõ ràng trong Eclipse (`1-Unit test`, `2-UI test`, `3-Coverage`)
