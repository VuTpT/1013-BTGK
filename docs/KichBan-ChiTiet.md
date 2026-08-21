# KỊCH BẢN CHI TIẾT & CÁC BƯỚC CẦN THỰC HIỆN

> Tài liệu này gồm 3 phần:
> **Phần I** – 8 nhóm việc phải làm **trước khi quay** (đánh số, có lệnh cụ thể, có ô "xong khi").
> **Phần II** – **Lời thoại nguyên văn** từng cảnh, ghép sát với thao tác trên màn hình.
> **Phần III** – Hậu kỳ và nộp bài.
>
> Ai làm cảnh nào: xem [PhanCong-QuayVideo.md](PhanCong-QuayVideo.md). Chỗ nào ghi **[A]** / **[B]** là
> việc của thành viên A / B; **[A+B]** là làm chung.
>
> Ký hiệu trong lời thoại: `…` là chỗ ngắt để thao tác chuột; **[thao tác]** là chỉ dẫn, **không đọc**.

---

# PHẦN I — CÁC BƯỚC CẦN THỰC HIỆN TRƯỚC KHI QUAY

Tổng thời gian ước tính: **6–7 giờ cho cả hai người**, chia gọn trong 2 ngày (D3 21/08 làm việc 1–7, D4 22/08 quay).

## Nhóm 1 — Kiểm tra máy chạy được (30 phút) **[A+B, mỗi người tự làm trên máy mình]**

| # | Việc | Lệnh / thao tác | Xong khi |
| --- | --- | --- | --- |
| 1.1 | Kiểm tra Java, Maven | `java -version` · `mvn -v` | Hiện `17.0.x` và `3.9.16` |
| 1.2 | Chạy toàn bộ test | `cd BTCK` rồi `mvn clean test` | In ra **`Tests run: 80, Failures: 0, Errors: 0, Skipped: 2`** |
| 1.3 | Sinh báo cáo phủ mã | `mvn test jacoco:report` | Mở được `target/site/jacoco/index.html`, thấy **83,8%** |
| 1.4 | Xem báo cáo Allure | `allure serve target/allure-results` | Dashboard mở, tab **Suites** có 3 nhóm `unit` · `web` · `pw` |
| 1.5 | Chạy có hiện trình duyệt | `ChayTest.bat hien` | Thấy Chrome tự thao tác trên MiniShop |

> ⚠️ Nếu bước 1.2 in ra số khác 80 (ví dụ 11 hoặc 64): xem **DEF-03** trong [Report.md](Report.md) — thiếu `junit-bom` thì một tầng test bị bỏ qua âm thầm.

## Nhóm 2 — Eclipse (45 phút) **[A]**

| # | Việc | Thao tác | Xong khi |
| --- | --- | --- | --- |
| 2.1 | Cài Eclipse IDE for Java Developers | Tải bản 2024-03+, giải nén, chạy | Mở được workspace |
| 2.2 | Trỏ JDK 17 | *Window → Preferences → Java → Installed JREs* | JDK 17 được tick |
| 2.3 | Import dự án | *File → Import → Maven → Existing Maven Projects* → thư mục `BTCK` | Project Explorer hiện `minishop-automation`, **không có ✕ đỏ** |
| 2.4 | Xử lý lỗi m2e nếu có | Chuột phải dòng lỗi trong `pom.xml` → *Quick Fix → Mark goal as ignored in Eclipse build* | Hết gạch đỏ |
| 2.5 | Cài plugin TestNG | *Help → Eclipse Marketplace* → "TestNG for Eclipse" → Install → restart | Chuột phải `testng.xml` có mục *Run As → TestNG Suite* |
| 2.6 | Tạo 3 Run Configuration đặt tên sẵn | *Run → Run Configurations…* → tạo:<br>`1-Unit test` (JUnit, gói `unit`)<br>`2-UI test` (TestNG, `testng.xml`)<br>`3-Coverage` (Maven build, goal `test jacoco:report`) | Menu Run xổ ra thấy đúng 3 tên tiếng Việt rõ ràng |
| 2.7 | Phóng to chữ | *Preferences → General → Appearance → Colors and Fonts* → Text Font cỡ **14–16** | Chữ đọc được khi thu nhỏ video |

> 🎬 **Quan trọng cho cảnh 3**: sau khi làm xong hết, **xóa project khỏi workspace** (chuột phải → Delete, **KHÔNG** tick "Delete on disk") để lúc quay import lại từ đầu cho thật.

## Nhóm 3 — GitHub (45 phút) **[A đẩy code, B cấu hình Actions]**

| # | Việc | Lệnh / thao tác | Xong khi |
| --- | --- | --- | --- |
| 3.1 | Tạo repo public `KTPM-Automation-Seminar` | github.com → New repository | Có URL repo |
| 3.2 | Đẩy code | `git init` · `git add .` · `git commit -m "KTPM-1 khoi tao du an MiniShop automation"` · `git branch -M main` · `git remote add origin <URL>` · `git push -u origin main` | Repo hiện đủ `src/`, `docs/`, `pom.xml` |
| 3.3 | Bật CI | Chép `ci/workflows/ci.yml` → `.github/workflows/ci.yml` ở **gốc repo**, commit, push | Tab **Actions** có workflow chạy **xanh** |
| 3.4 | Tạo nhánh backup bản v1 | Giải nén `../BTCK-v1-daydu.zip` ra nhánh `backup/v1-daydu` rồi push | Trên GitHub thấy 2 nhánh |
| 3.5 | Thêm badge CI vào README | Copy badge từ tab Actions → dán đầu `README.md` | Badge xanh hiển thị trên trang repo |

## Nhóm 4 — Jira (60 phút) **[B]**

| # | Việc | Thao tác | Xong khi |
| --- | --- | --- | --- |
| 4.1 | Tạo site Jira Cloud free + project **KTPM** (Scrum) | jira.atlassian.com | Vào được board |
| 4.2 | Thêm issue type **Test** | *Project settings → Issue types* | Danh sách có "Test" |
| 4.3 | Bật Due date + Time tracking | *Project settings → Fields* và *Jira settings → Issues → Time tracking* | Form tạo issue có 2 trường này |
| 4.4 | Nhập CSV | *Settings → System → External System Import → CSV* → [jira-import.csv](jira-import.csv)<br>Date format: **`dd/MM/yyyy HH:mm`** · Time tracking: **giây** | Board có **36 issue** |
| 4.5 | Start sprint | *Backlog → KTPM Sprint 1 → Start sprint*, ngày **16/08/2026 → 22/08/2026** | Sprint đang chạy |
| 4.6 | Cài app GitHub for Jira | *Apps → Explore more apps* → "GitHub for Jira" → nối repo | Mở KTPM-191 thấy mục **Development** |
| 4.7 | Kiểm tra báo cáo | *Reports → Burndown* và *Sprint report* | Biểu đồ có dữ liệu, tổng **28 giờ ước lượng / 27,9 giờ thực tế** |
| 4.8 | Tạo sẵn 1 issue trống để dùng ở cảnh 9 | Chưa tạo — sẽ tạo **trực tiếp trước ống kính** | (chỉ cần nhớ đặt mã **KTPM-192**) |

## Nhóm 5 — Jenkins (60 phút, tùy chọn) **[A]**

| # | Việc | Thao tác | Xong khi |
| --- | --- | --- | --- |
| 5.1 | Chạy Jenkins | `java -jar jenkins.war --httpPort=8080` → mở `localhost:8080` → unlock | Vào được trang chủ |
| 5.2 | Cài plugin | Git · Pipeline · Maven Integration · JUnit · **Allure Jenkins Plugin** · HTML Publisher | Danh sách plugin có đủ |
| 5.3 | Khai báo Tools | *Manage Jenkins → Tools*: JDK tên `jdk17`, Maven tên `maven3`, Allure tên `allure` | Lưu không báo lỗi |
| 5.4 | Tạo job Pipeline | *New Item → Pipeline* → *Pipeline script from SCM* → repo, script path `BTCK/ci/Jenkinsfile` | Build được |
| 5.5 | Chạy **3 build** liên tiếp | Bấm *Build Now* 3 lần | Allure có **history trend** (biểu đồ xu hướng cần ≥ 3 build) |

> Nếu máy yếu hoặc hết thời gian: **bỏ nhóm 5**, cảnh 9 thay Jenkins bằng GitHub Actions + Allure, và nói rõ lý do trong video.

## Nhóm 6 — Selenium IDE (20 phút) **[A]**

| # | Việc | Thao tác | Xong khi |
| --- | --- | --- | --- |
| 6.1 | Cài extension Selenium IDE cho Chrome | Chrome Web Store | Icon hiện trên thanh extension |
| 6.2 | Chạy SUT | Mở `sut-web/index.html` hoặc chạy `LocalWebServer` | Trang đăng nhập hiện ra |
| 6.3 | Record thử **1 lần trước** | Record → đăng nhập → thêm giỏ → Stop → Play | Các bước đều dấu tích xanh |
| 6.4 | Lưu | Save project → `selenium-ide/minishop-login.side` | File nằm trong repo |

## Nhóm 7 — Chuẩn bị quay (30 phút) **[A+B]**

| # | Việc | Chi tiết |
| --- | --- | --- |
| 7.1 | Cài OBS Studio | Scene: *Display Capture* · Output 1920×1080 · 30fps · thu **Mic** |
| 7.2 | Test âm 30 giây | Quay thử, nghe lại: giọng rõ, không vọng, không tiếng quạt |
| 7.3 | Dọn màn hình | Tắt thông báo Windows (Focus assist), đóng Zalo/Messenger, dọn thanh bookmark, đặt hình nền trơn |
| 7.4 | Mở sẵn tab theo cảnh | Xem mục "Trước khi bấm Record" của từng cảnh ở Phần II |
| 7.5 | In/mở lời thoại ra màn hình phụ | Nếu chỉ có 1 màn hình: in ra giấy, **đừng đọc trong lúc quay màn hình chính** |

## Nhóm 8 — Diễn tập (60 phút) **[A+B]**

| # | Việc | Xong khi |
| --- | --- | --- |
| 8.1 | Chạy thử **toàn bộ demo 1 lượt không quay**, bấm giờ từng cảnh | Ghi lại cảnh nào vượt thời lượng |
| 8.2 | Thử riêng nhịp đỏ→xanh của cảnh 6 | Sửa `>=` thành `>` → chạy đỏ 2 test → sửa lại → xanh |
| 8.3 | Thử riêng vòng khép kín cảnh 9 | Push lỗi → Actions đỏ → sửa → xanh (rồi **xóa nhánh thử** đi) |
| 8.4 | Quay clip dự phòng | Jenkins, Emulator, Actions — phòng khi lúc quay thật bị lỗi mạng |

---

# PHẦN II — KỊCH BẢN LỜI THOẠI

**Cách dùng**: cột trái là thao tác, cột phải là lời đọc. Nói **chậm hơn bình thường một chút** (~150 từ/phút).
Chỗ in đậm là chỗ nhấn giọng. Chỗ có `[…]` là dừng nói để thao tác cho người xem kịp nhìn.

---

## CẢNH 1 — Mở đầu · **[A]** · 1'15

**Trước khi bấm Record**: mở `docs/Plan.md` ở chế độ preview, cuộn tới **mục 2.1 – sơ đồ dây chuyền**, phóng to 150%.

| Thao tác | Lời thoại |
| --- | --- |
| Màn hình sơ đồ | "Chào thầy và các bạn. Nhóm em gồm **[tên A]** và **[tên B]**, trình bày seminar môn Kiểm thử phần mềm, chủ đề **Automation Test**." |
| — | "Đề bài yêu cầu **kết hợp** các công cụ: JUnit, TestNG, Selenium, Playwright, Appium, Allure, Jira, Jenkins và GitHub. Nhóm em không trình bày rời rạc từng tool, mà ghép chúng thành **một dây chuyền kiểm thử tự động khép kín** như sơ đồ này." |
| Con trỏ đi từ Jira → GitHub | "Bắt đầu từ **Jira** — nơi quản lý test case, độ ưu tiên và giao việc. Test case được hiện thực thành mã, đẩy lên **GitHub**." |
| Con trỏ xuống 2 nhánh CI | "Mỗi Pull Request kích hoạt **GitHub Actions** chạy nhanh tầng unit để chặn lỗi ngay ở cửa. Sau khi merge, **Jenkins** chạy đầy đủ mọi tầng test." |
| Con trỏ quét ngang 4 tầng | "Bốn tầng đó là: **JUnit 5** cho tầng unit, **TestNG với Selenium** cho giao diện web, **Playwright** để đối chứng, và **Appium** cho thiết bị di động." |
| Con trỏ về Allure rồi vòng lên Jira | "Kết quả cả bốn tầng gộp về **một** báo cáo **Allure**, và từ Allure bấm được **ngược** về đúng issue trên Jira — vòng khép kín." |
| Nhìn thẳng | "Điều nhóm em muốn nhấn mạnh: quan trọng không phải **dùng được bao nhiêu tool**, mà là **mỗi tool giữ đúng một mắt xích** — bỏ một cái là đứt dây chuyền. Cuối video, chúng em sẽ chạy thật trọn vòng này với **một lỗi có thật**." |

---

## CẢNH 2 — Hệ thống được kiểm thử · **[B]** · 1'15

**Trước khi bấm Record**: Chrome mở `sut-web/index.html` (đã đăng xuất, đã xoá localStorage bằng F12 → Application → Clear site data).

| Thao tác | Lời thoại |
| --- | --- |
| Màn hình đăng nhập MiniShop | "Hệ thống được kiểm thử là **MiniShop** — một web bán hàng thu nhỏ do **nhóm em tự viết**." |
| — | "Nhóm chọn tự viết thay vì dùng site demo trên mạng vì ba lý do: chạy được **offline**, kết quả **tái lập 100%**, và quan trọng nhất — cùng một đặc tả được kiểm thử ở **hai mức**: thư viện Java ở tầng unit, và giao diện web ở tầng UI. Nhờ vậy ma trận truy vết mới thực sự có ý nghĩa." |
| Đăng nhập `standard_user` / `secret_sauce` | "MiniShop có ba tài khoản mẫu: khách thường, khách VIP, và một tài khoản bị quản trị viên khoá. Em đăng nhập bằng khách thường… `[…]`" |
| Thêm sản phẩm, mở giỏ, nhập `SALE10` | "Nghiệp vụ gồm: giỏ hàng giới hạn số lượng từ 1 đến 10, tối đa 5 loại sản phẩm… mã giảm giá `SALE10`… và chính sách khuyến mãi phụ thuộc **ba điều kiện**: khách VIP, đơn từ 500 nghìn, và có mã hợp lệ." |
| Đăng xuất, nhập sai 3 lần | "Phần đáng chú ý nhất là đăng nhập: sai **dưới** ba lần thì báo còn mấy lượt… `[…]` sai **đủ** ba lần thì **khoá tài khoản**. Đặc tả ghi khoá 15 phút; bản web rút xuống 60 giây để test giao diện chạy được trong thời gian hợp lý." |
| Chỉ vào thông báo khoá | "Đây chính là ví dụ nhóm em sẽ dùng lại ở phần thiết kế test case bằng **sơ đồ chuyển trạng thái**. Ngoài phạm vi kiểm thử lần này: hiệu năng, bảo mật và kiểm thử API." |

---

## CẢNH 3 — Cài đặt và mở dự án bằng Eclipse · **[A]** · 2'30

**Trước khi bấm Record**: đã xoá project khỏi workspace Eclipse; mở sẵn 1 cửa sổ PowerShell ở thư mục `BTCK`.

| Thao tác | Lời thoại |
| --- | --- |
| PowerShell: `java -version` rồi `mvn -v` *(tua nhanh 4× khi dựng)* | "Phần cài đặt. Dự án cần **JDK 17** và **Maven 3.9**… `[…]` Cả hai đã có trong PATH." |
| Mở Eclipse | "IDE nhóm em dùng là **Eclipse**. Gói *Eclipse IDE for Java Developers* đã tích hợp sẵn **m2e** nên không phải cài thêm gì cho Maven." |
| *Preferences → Installed JREs* | "Việc đầu tiên là trỏ Eclipse tới đúng JDK 17… `[…]`" |
| *File → Import → Maven → Existing Maven Projects* | "Import dự án: **File – Import – Maven – Existing Maven Projects**… chọn thư mục `BTCK`… Eclipse nhận ra ngay **một** file `pom.xml`… Finish." |
| Chờ tải thư viện | "Lần đầu Eclipse sẽ tải thư viện về, mất khoảng một tới ba phút." |
| Mở rộng cây dự án | "Và đây là điểm nhóm em muốn nói: toàn bộ dự án chỉ có **một module Maven duy nhất**. `src/main/java` là mã nghiệp vụ của MiniShop — tức là **đối tượng bị kiểm thử**. `src/test/java` chia làm **bốn gói ứng với bốn tầng test**: `unit`, `web`, `pw` và `mobile`." |
| Chỉ vào `pom.xml` | "Bản đầu tiên nhóm em làm có **bảy** module Maven. Sau đó nhóm chủ động gộp lại còn một, vì mục tiêu của seminar là **dễ hiểu và dễ dựng lại**, chứ không phải phô diễn cấu trúc phức tạp. Lý do từng quyết định ghi trong `docs/Plan.md`." |
| *(nếu có lỗi đỏ)* Quick Fix | "Nếu Eclipse gạch đỏ `pom.xml` với thông báo *Plugin execution not covered by lifecycle* — đây là lỗi kinh điển của m2e, không phải lỗi dự án. Chuột phải, **Quick Fix**, chọn *Mark goal as ignored in Eclipse build* là xong; chạy `mvn` ngoài dòng lệnh vẫn bình thường." |
| *Help → Eclipse Marketplace* | "Thứ duy nhất phải cài thêm là plugin **TestNG for Eclipse**, để chạy được file `testng.xml` bằng chuột phải." |
| PowerShell: `mvn clean test` | "Và bước kiểm chứng cuối cùng: chạy `mvn clean test`… `[…]` **80 test, 0 fail, 2 skip**. Hai test skip là tầng Appium — máy này không bật Emulator nên chúng tự bỏ qua, build vẫn xanh." |

---

## CẢNH 4 — Thiết kế test case: 5 kỹ thuật đã học · **[B]** · 3'00

**Trước khi bấm Record**: mở `docs/TestDesign-Techniques.md` (preview) ở tab 1, `docs/TestCases.md` ở tab 2.

| Thao tác | Lời thoại |
| --- | --- |
| Mở TestDesign-Techniques.md | "Đây là phần **áp dụng kiến thức đã học của môn**. Toàn bộ test case của nhóm được **thiết kế trước, viết mã sau** — không phải viết mã rồi đặt tên cho có." |
| Cuộn tới bảng lớp tương đương | "**Kỹ thuật một — phân hoạch tương đương**, bài tuần 7. Với trường họ tên khi thanh toán, nhóm chia miền dữ liệu thành các lớp: chuỗi chữ hợp lệ, chuỗi rỗng, chuỗi có chữ số, chuỗi có ký tự lạ. Nguyên tắc là **mỗi lớp chỉ cần một đại diện** — vì nếu `Nguyen Van 1` sai thì `Nguyen Van 2` cũng sai, kiểm thêm không có giá trị." |
| Chuyển tab, chỉ TC-UNIT-063 | "Tương ứng trong bộ test là `TC-UNIT-063`." |
| Về bảng biên | "**Kỹ thuật hai — phân tích giá trị biên**. Số lượng sản phẩm hợp lệ từ 1 đến 10, nhóm kiểm bốn giá trị: **0, 1, 10 và 11** — tức là ngay dưới biên, đúng biên dưới, đúng biên trên, và ngay trên biên. Lý do rất thực tế: lỗi lập trình hầu như luôn nằm ở biên, hiếm khi nằm ở giữa miền." |
| Chỉ TC-UNIT-040, 041 | "Đây là `TC-UNIT-040` và `041`. Lát nữa ở phần demo, các bạn sẽ thấy đúng một lỗi biên có thật được bộ test này bắt được." |
| Cuộn tới bảng quyết định | "**Kỹ thuật ba — bảng quyết định**, bài tuần 5. Chính sách khuyến mãi có **ba điều kiện**: khách VIP, đơn từ 500 nghìn, và mã giảm giá hợp lệ. Ba điều kiện nhị phân cho **2 mũ 3 bằng 8 luật**, và nhóm em kiểm **đủ cả 8**, không rút gọn luật nào." |
| Chỉ TC-UNIT-050 | "Cả 8 luật nằm trong một test tham số hoá duy nhất là `TC-UNIT-050`." |
| Cuộn tới sơ đồ trạng thái | "**Kỹ thuật bốn — sơ đồ chuyển trạng thái**, bài tuần 6. Phiên đăng nhập có các trạng thái: chưa đăng nhập, đã đăng nhập, và khoá tạm. Nguyên tắc thiết kế: **mỗi mũi tên trên sơ đồ là một test case**." |
| Chỉ nhóm TC-UNIT-030…037 | "Ví dụ mũi tên *đang khoá – nhập đúng mật khẩu – vẫn bị từ chối* là `TC-UNIT-033`; mũi tên *hết 15 phút – tự mở khoá* là `TC-UNIT-035`. Test này chạy trong vài mili-giây chứ không chờ thật 15 phút, nhờ một đồng hồ giả lập — phần sau bạn A sẽ chỉ rõ." |
| Cuộn tới lưu đồ `divide()` | "**Kỹ thuật năm — kiểm thử hộp trắng**. Với hàm `divide` của lớp `Rational`, nhóm vẽ lưu đồ, tính **độ phức tạp Cyclomatic**, từ đó suy ra **số đường đi độc lập cần phủ** — và đó chính là số test case tối thiểu, chứ không phải con số đoán mò." |
| Chuyển tab TestCases.md, cuộn xuống mục 5 | "Cuối cùng, tất cả được ràng lại bằng **ma trận truy vết**: 24 yêu cầu chức năng, mỗi yêu cầu ánh xạ tới test case cụ thể, mã Jira cụ thể và file mã nguồn cụ thể. **Độ phủ yêu cầu 24 trên 24 — 100%**, không có yêu cầu nào bị bỏ sót và cũng không có test case nào mồ côi." |

---

## CẢNH 5 — Quản lý test case bằng Jira · **[B]** · 2'00

**Trước khi bấm Record**: đăng nhập Jira, mở board, cuộn lên đầu.

| Thao tác | Lời thoại |
| --- | --- |
| Board Scrum | "Toàn bộ test case được quản lý trên **Jira**. Board hiện có **36 issue**: 5 Epic tương ứng 5 tầng công việc, 24 issue loại **Test**, 3 Task cấu hình CI/CD và 2 Bug." |
| Mở KTPM-120 | "Mở thử một test case — `KTPM-120`, phần bảng quyết định. Mô tả ghi đủ **các bước, kết quả mong đợi và kỹ thuật thiết kế** đã dùng… `[…]`" |
| Chỉ lần lượt các trường | "**Priority** ở mức Highest vì đây là nghiệp vụ tính tiền… **Assignee** cho biết ai chịu trách nhiệm… **Due date** là hạn hoàn thành… và **Original Estimate / Time Spent** ghi lại công sức thực tế." |
| Chạy JQL | "Jira cho lọc bằng JQL. Ví dụ, lấy mọi test case chưa xong, sắp theo độ ưu tiên giảm dần… `[…]` — đây là danh sách việc cần làm trước." |
| *Reports → Burndown* | "Nhờ có đủ ngày tháng và ước lượng, **biểu đồ burndown** vẽ ra được tiến độ thật của nhóm trong sprint từ 16 đến 22 tháng 8…" |
| *Sprint report* | "…và **Sprint report** tổng kết: ước lượng **28 giờ**, thực tế **27,9 giờ** cho hai người." |
| Mở KTPM-191, chỉ mục Development | "Điểm nối với công cụ tiếp theo nằm ở đây: mục **Development**. Nhóm quy ước đặt tên nhánh là `feature/KTPM-xx` và commit bắt đầu bằng mã issue. Nhờ quy ước đó, Jira **tự** hiển thị nhánh, commit và Pull Request tương ứng — không phải khai báo thủ công." |

---

## CẢNH 6 — JUnit 5 + EasyMock + JaCoCo + TDD · **[A]** · 3'00

**Trước khi bấm Record**: Eclipse mở sẵn 4 tab: `CalculatorTest.java`, `LoginServiceTest.java`, `DiscountPolicy.java`, `DiscountPolicyTest.java`.

| Thao tác | Lời thoại |
| --- | --- |
| Mở `CalculatorTest` | "Tầng unit dùng **JUnit 5**. Bắt đầu bằng ví dụ đơn giản nhất — lớp `Calculator` của bài tuần 8." |
| Chỉ lần lượt annotation | "`@Test` đánh dấu một phương thức là test… `@DisplayName` đặt tên tiếng Việt dễ đọc… `@BeforeEach` chạy trước **mỗi** test để dựng lại dữ liệu sạch…" |
| Chỉ `@ParameterizedTest` | "Đáng chú ý nhất là **`@ParameterizedTest`** đi kèm `@CsvSource`: **một** phương thức chạy với **nhiều** bộ dữ liệu. Đây chính là cách nhóm em hiện thực kỹ thuật giá trị biên — mỗi dòng dữ liệu là một giá trị biên." |
| Chỉ `assertThrows`, `assertNotEquals` | "Đề bài tuần 8 yêu cầu có cả kiểm chứng **đúng** và kiểm chứng **sai**: đây là `assertNotEquals`… còn đây là `assertThrows` cho trường hợp chia cho 0." |
| Mở `LoginServiceTest` | "Sang ví dụ phức tạp hơn: dịch vụ đăng nhập. Hàm này phụ thuộc **kho dữ liệu người dùng**, nhưng test không được phụ thuộc cơ sở dữ liệu thật." |
| Chỉ `mock` → `expect` → `replay` → `verify` | "Nên nhóm dùng **EasyMock** đúng như bài tuần 10: `mock` tạo đối tượng giả… `expect` khai báo khi gọi hàm này thì trả về cái này… `replay` chuyển sang chế độ chạy… và `verify` kiểm tra rằng hàm **đã thực sự được gọi** đúng như kỳ vọng." |
| Chỉ `MutableClock` | "Còn đây là mẹo quan trọng: `MutableClock` — đồng hồ giả lập. Nhờ nó, test *tự động mở khoá sau 15 phút* chạy xong trong **vài mili-giây** thay vì chờ thật. Muốn làm được vậy thì lớp `LoginService` phải **nhận đồng hồ qua constructor** — tức là **khả năng kiểm thử phải được thiết kế từ đầu**." |
| Chuột phải gói `unit` → *Run As → JUnit Test* | "Chạy toàn bộ tầng unit… `[…]` **64 test, thanh xanh**." |
| Mở `DiscountPolicy.java`, tìm dòng `subtotal >= THRESHOLD` | "Giờ em xin demo quy trình **đỏ – xanh**. Đây là dòng kiểm tra đơn hàng có đạt ngưỡng 500 nghìn hay không. Em cố tình gây một lỗi rất kinh điển: **bỏ dấu bằng**, đổi `lớn hơn hoặc bằng` thành `lớn hơn`." |
| Sửa `>=` → `>`, Ctrl+S, chạy lại | "Lưu lại… chạy lại… `[…]`" |
| Chỉ 2 test đỏ | "**Hai test đỏ ngay**, ở **hai tầng khác nhau**: `TC-UNIT-051` là test giá trị biên đúng ngưỡng 500 nghìn, và `TC-UNIT-075` là test đặt hàng. Một dấu bằng bị mất — bộ test bắt được ngay. **Đây chính là giá trị của kỹ thuật phân tích giá trị biên** mà bạn B vừa trình bày." |
| Sửa lại `>=`, chạy lại | "Sửa lại cho đúng… chạy lại… **xanh trở lại**." |
| Chạy Run Configuration `3-Coverage`, mở `index.html` | "Cuối cùng là **độ phủ mã lệnh** đo bằng **JaCoCo**: **83,8% lệnh**, **78,7% nhánh**." |
| Mở lớp `Rational` trong báo cáo | "Vào từng lớp còn xem được **dòng nào đã chạy, dòng nào chưa**. Nhóm em muốn nói rõ: độ phủ là **chỉ số hỗ trợ, không phải mục tiêu**. Bản đầu của nhóm có 144 unit test đạt 91%; bản rút gọn còn 64 test nên còn 83,8%. Đó là **đánh đổi có ý thức** — biết mình mất gì khi làm gọn lại." |

---

## CẢNH 7 — TestNG + Selenium · **[A]** · 3'00

**Trước khi bấm Record**: SUT đang chạy; Selenium IDE sẵn sàng; Eclipse mở `LoginPage.java`, `LoginTest.java`, `testng.xml`.

| Thao tác | Lời thoại |
| --- | --- |
| Mở Selenium IDE, bấm Record | "Sang tầng giao diện web. Cách nhanh nhất để có test đầu tiên là **Selenium IDE** — ghi lại thao tác thật." |
| Thao tác đăng nhập trên MiniShop, Stop, Play | "Em ghi kịch bản đăng nhập… dừng ghi… và phát lại… `[…]` Các bước đều xanh." |
| *Export → Java JUnit*, mở file | "IDE còn xuất được ra mã Java. Nhưng nhìn kỹ: locator do IDE **tự sinh**, bám vào cấu trúc HTML — giao diện đổi một chút là gãy. Nên IDE hợp để **tạo bản nháp nhanh**, còn để bảo trì lâu dài thì phải viết theo mẫu **Page Object**." |
| Mở `LoginPage.java` | "Đây là Page Object của trang đăng nhập. Mỗi trang là một lớp; các thao tác được gói thành phương thức có tên nghiệp vụ như `loginAs`. Locator dùng thuộc tính **`data-testid`** do chính nhóm đặt trong HTML — đổi bố cục giao diện cũng không phải sửa test." |
| Mở `BasePage`, chỉ `WebDriverWait` | "Phần chờ dùng **explicit wait** với `WebDriverWait` và `ExpectedConditions`. Lưu ý khác so với bài tuần 12: Selenium 4 nhận **`Duration.ofSeconds`** chứ không nhận số nguyên nữa. Và từ Selenium 4.6, **Selenium Manager tự tải driver**, nên không còn phải khai báo `System.setProperty` cho ChromeDriver như trước." |
| Mở `LoginTest.java` | "Tầng UI nhóm dùng **TestNG** thay vì JUnit, để khai thác đúng thế mạnh của nó." |
| Chỉ `@DataProvider` | "Thứ nhất: **`@DataProvider`** đọc dữ liệu từ file CSV bên ngoài — muốn thêm ca kiểm thử chỉ cần thêm một dòng dữ liệu, không phải sửa mã." |
| Mở `login-data.csv` | "Ba dòng này tương ứng ba lớp lỗi: sai mật khẩu, bỏ trống, và tài khoản bị quản trị viên khoá." |
| Mở `testng.xml` | "Thứ hai: **file suite XML** — chọn chạy lớp nào, gắn listener của Allure, tất cả **không cần biên dịch lại**." |
| Chuột phải `testng.xml` → *Run As → TestNG Suite* | "Chạy suite… `[…]` **11 test pass**." |
| *(tuỳ chọn)* chạy `ChayTest.bat hien` | "Và đây là khi chạy ở chế độ hiện trình duyệt — Selenium thao tác đúng như người dùng thật." |
| Nhìn thẳng | "Một ghi chú về **Selenium Grid**: bản đầu tiên nhóm em có dùng Grid và chạy song song hai luồng. Bản này đã bỏ, vì đã ghi nhận một lỗi thật — chạy song song ba luồng làm suite sập hàng loạt do máy không đủ tài nguyên. Chi tiết nằm trong `Report.md`, mục nhật ký lỗi." |

---

## CẢNH 8 — Playwright đối chứng · **[B]** · 2'00

**Trước khi bấm Record**: đã chạy `mvn clean test` để có `target/traces/*.zip`; mở sẵn `MiniShopPlaywrightTest.java` và `Report.md` mục 5.1.

| Thao tác | Lời thoại |
| --- | --- |
| Mở `MiniShopPlaywrightTest` | "Câu hỏi tự nhiên là: **ngoài Selenium còn lựa chọn nào khác?** Nên nhóm em viết lại **đúng ba test case** đã có bằng Selenium, sang **Playwright** — cùng hệ thống, cùng dữ liệu, cùng khẳng định. Khác nhau **duy nhất** là công cụ, nhờ vậy so sánh mới công bằng." |
| Đặt 2 file cạnh nhau | "So sánh trực tiếp: bên Selenium, để nhập một ô phải tìm phần tử, xoá nội dung cũ, rồi gõ, và thường phải thêm lệnh chờ. Bên Playwright chỉ một dòng `page.fill`, và **không cần lệnh chờ** vì Playwright **tự đợi** phần tử sẵn sàng." |
| Chạy `mvn test -Dtest=MiniShopPlaywrightTest` | "Chạy thử… `[…]` ba test pass." |
| Mở trace viewer | "Điểm mạnh nhất của Playwright là **Trace Viewer**: mỗi lần chạy đều ghi lại vết… `[…]` xem lại được **từng bước**, kèm ảnh chụp và cả cây DOM tại thời điểm đó. Khi test lỗi thì đây là thứ tiết kiệm thời gian nhất." |
| Mở bảng so sánh trong Report.md | "Kết luận của nhóm, có số liệu kèm theo: **Playwright** thắng về tốc độ, cơ chế tự chờ và trải nghiệm gỡ lỗi. **Selenium** thắng về mức độ phổ biến, chuẩn W3C và hệ sinh thái — Grid, IDE, tích hợp sẵn trong mọi công cụ CI." |
| — | "Nên khuyến nghị là: dự án mới nên chọn Playwright; còn doanh nghiệp đã có sẵn hạ tầng Selenium thì giữ Selenium, không cần đổi." |

---

## CẢNH 8b *(tuỳ chọn)* — Appium · **[B]** · 1'00

**Trước khi bấm Record**: Emulator đã bật, `appium` đang chạy ở một cửa sổ terminal.

| Thao tác | Lời thoại |
| --- | --- |
| Cho thấy Emulator + terminal appium | "Tầng cuối là kiểm thử trên **thiết bị di động** bằng **Appium** — đây là tầng không công cụ nào ở trên thay thế được." |
| Chạy `mvn test -Dmobile.tests=true` | "Em chạy với tham số bật tầng mobile… `[…]` Appium điều khiển Chrome **trên máy ảo Android** thao tác đúng kịch bản đăng nhập và thêm giỏ hàng." |
| Chỉ dòng `10.0.2.2` trong mã | "Một chi tiết kỹ thuật đáng lưu ý: máy ảo Android gọi về máy thật qua địa chỉ **10.0.2.2** chứ không phải `127.0.0.1`." |
| Chỉ kết quả `Tests run: 82` | "Tổng số test lúc này là **82**. Và nếu không bật Emulator, hai test này **tự bỏ qua** nhờ annotation `@EnabledIfSystemProperty` — build vẫn xanh, CI không bị gãy." |

---

## CẢNH 9 — Vòng khép kín · **[A + B]** · 2'30 ⭐

**Trước khi bấm Record**: Eclipse + terminal (A) · Chrome mở sẵn 3 tab: GitHub Actions, Jira, Jenkins (B). Quay **một mạch**.

| Ai | Thao tác | Lời thoại |
| --- | --- | --- |
| B | — | "Phần cuối là phần trả lời trực tiếp câu hỏi của đề bài: **kết hợp các tool để làm gì?** Chúng em sẽ tạo **một lỗi thật** và đi theo nó qua đủ năm công cụ." |
| A | Sửa `>=` → `>`, commit, push | "Em lặp lại đúng lỗi lúc nãy — bỏ dấu bằng ở ngưỡng khuyến mãi. Commit với mã issue `KTPM-192`… và đẩy lên GitHub… `[…]`" |
| B | Mở tab Actions, F5 | "Ngay khi push, **GitHub Actions** tự chạy… `[…]` và **đỏ**. Mở log ra thấy đúng **hai test fail** — không phải đoán, mà chỉ thẳng tên test và dòng khẳng định sai." |
| B | Tạo Bug KTPM-192 trên Jira | "Em tạo issue loại **Bug** trên Jira: `KTPM-192`, mức Highest, dán link build vào mô tả, và **giao cho bạn A**… `[…]` Đây là lúc quy trình quản lý lỗi bắt đầu." |
| A | Mở Jenkins → build đỏ → Allure | "Bên Jenkins, dây chuyền đầy đủ cũng đỏ… `[…]` Vào **báo cáo Allure**, chọn test thất bại — có sẵn thông báo lỗi, và với test giao diện thì có cả **ảnh chụp màn hình** tại thời điểm fail." |
| A | Sửa lại `>=`, commit, push | "Em sửa lại… commit vẫn gắn mã `KTPM-192` để Jira nối được… và push." |
| B | Actions xanh → Jenkins xanh → Allure trend | "**Actions xanh**… `[…]` **Jenkins build SUCCESS**… `[…]` và **history trend** của Allure hồi phục — biểu đồ này cho thấy chất lượng theo thời gian chứ không chỉ một lần chạy." |
| B | Kéo KTPM-192 sang Done | "Cuối cùng, kéo issue sang **Done**. Vòng khép lại đúng chỗ nó bắt đầu." |
| B | Nhìn thẳng | "Đó là lý do phải kết hợp tool: **một dấu bằng bị mất** đã đi qua GitHub, GitHub Actions, Jenkins, Allure và Jira, rồi quay về đúng issue ban đầu. Không có mắt xích nào thừa." |

---

## CẢNH 10 — Kết luận · **[A + B]** · 1'00

**Trước khi bấm Record**: mở `docs/Report.md` mục 1.

| Ai | Lời thoại |
| --- | --- |
| A | "Tổng kết bằng số liệu: **80 test case**, 78 pass, **0 fail**, 2 skip là tầng mobile; chạy trọn bộ trong **28 giây**; độ phủ mã **83,8%**. Trong quá trình làm, nhóm ghi nhận **hai lỗi hạ tầng có thật**, đều đã đóng." |
| A | "Lỗi đáng nhớ nhất là lỗi thứ hai: sau khi gộp dự án, `mvn test` báo **BUILD SUCCESS** nhưng thực ra **toàn bộ 144 unit test đã bị bỏ qua âm thầm** do xung đột phiên bản thư viện. Bài học rút ra: **màu xanh của build không đủ để tin — phải nhìn tổng số test đã chạy**." |
| B | "Bài học thứ hai là về sự đánh đổi: nhóm chủ động cắt 57% số test case cho gọn, đổi lại độ phủ giảm khoảng 7 điểm phần trăm. Việc của người kiểm thử là **biết mình đang đánh đổi cái gì**, chứ không phải cắt bừa." |
| B | "Hạn chế: chưa kiểm thử hiệu năng và bảo mật, tầng mobile phụ thuộc máy có Emulator. Hướng mở rộng đã ghi trong báo cáo. Nhóm em xin hết, cảm ơn thầy và các bạn đã theo dõi." |

---

# PHẦN III — HẬU KỲ VÀ NỘP BÀI

| # | Việc | Ai | Chi tiết |
| --- | --- | --- | --- |
| 1 | Ghép clip theo thứ tự 1 → 10 | B | Chèn tiêu đề chữ ở đầu mỗi cảnh (tên cảnh + tool đang trình bày) |
| 2 | Tua nhanh phần chờ | B | Các đoạn Maven tải thư viện, Eclipse import: tăng tốc **4×**, giữ tiếng nói ở tốc độ thường |
| 3 | Chuẩn hoá âm lượng hai giọng | B | Hai người thu ở hai máy nên âm lượng khác nhau — phải cân bằng |
| 4 | Rà theo bảng đối chiếu | A | [PhanCong-QuayVideo.md](PhanCong-QuayVideo.md) mục 6: đủ 9 tool · đủ 5 kỹ thuật · đủ 6 yêu cầu đề bài |
| 5 | Xuất video | B | H.264, CRF 23, 1920×1080; đặt tên `Nhom<XX>_AutomationTest.mp4` |
| 6 | Hoàn tất repo | A | `git tag v2.0` · push · kiểm tra README hiển thị đúng trên GitHub |
| 7 | Nộp mLearning | A+B | **Trước 22:00 ngày 22/08/2026**; mở lại file vừa nộp để chắc chắn không hỏng |

## Danh sách kiểm tra cuối cùng trước khi nộp

- [ ] Video có tiếng của **cả hai** thành viên
- [ ] Cảnh 1 có sơ đồ dây chuyền, cảnh 9 có vòng khép kín chạy thật
- [ ] Đủ 9 tool, mỗi tool nói rõ vai trò riêng
- [ ] Đủ 5 kỹ thuật: phân hoạch tương đương · giá trị biên · bảng quyết định · sơ đồ chuyển trạng thái · phủ lệnh-nhánh
- [ ] Có ít nhất một lần **đỏ** và một lần **xanh** (cảnh 6 và cảnh 9)
- [ ] Repo public, Actions xanh, tag `v2.0`
- [ ] Video dưới 25 phút và mở được sau khi nộp
