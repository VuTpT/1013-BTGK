# MiniShop – Seminar Automation Test (môn Kiểm thử phần mềm 1013)

Dự án minh họa một **dây chuyền kiểm thử tự động khép kín**, kết hợp 9 công cụ theo yêu cầu đề bài:
**Jira · GitHub · GitHub Actions · Jenkins · JUnit 5 · TestNG · Selenium (IDE/WebDriver) · Playwright · Appium · Allure**.

> **Bản v2 – rút gọn cho mục đích học tập**: gộp 7 module Maven thành **một project duy nhất**,
> mở trực tiếp bằng **Eclipse**, cắt số test case xuống còn 1/3. Kế hoạch rút gọn và lý do từng
> quyết định: [docs/Plan.md](docs/Plan.md).

| Chỉ số | Giá trị |
| --- | --- |
| Test case | **80** (64 unit · 11 UI · 3 đối chứng · 2 mobile) — bản v1 có 188 |
| Kết quả gần nhất | **78 pass / 0 fail / 2 skip** (mobile, cần Emulator) |
| Độ phủ mã nghiệp vụ | 83,8% lệnh · 78,7% nhánh |
| Thời gian chạy đầy đủ | **28 giây** (`mvn clean test`) |
| Chạy offline | ✅ (SUT tự phục vụ bằng web server nhúng trong JDK) |

## Chạy nhanh

```bash
cd BTCK

mvn clean test                  # CẢ 3 tầng: unit + Selenium + Playwright  (~28 giây)
mvn clean test -Dheadless=false # hiện trình duyệt (dùng khi quay video)
mvn test -Dmobile.tests=true    # thêm tầng Appium (cần Emulator + appium server)
mvn test jacoco:report          # báo cáo độ phủ mã lệnh
```

Trên Windows có thể bấm trực tiếp `ChayTest.bat` (thêm tham số `hien` hoặc `mobile`).

> Lần đầu cần Internet để Maven tải thư viện. Nếu `mvn` chưa có trong `PATH`, xem
> [docs/Setup-Guide.md](docs/Setup-Guide.md) bước 2.

## Mở bằng Eclipse

*File → Import… → Maven → **Existing Maven Projects*** → chọn thư mục `BTCK` → **Finish**.

| Việc cần chạy | Thao tác trong Eclipse |
| --- | --- |
| Toàn bộ unit test | Chuột phải gói `vn.edu.ktpm.minishop.unit` → *Run As → JUnit Test* |
| Suite UI (TestNG) | Chuột phải `src/test/resources/testng.xml` → *Run As → TestNG Suite* |
| Toàn bộ qua Maven | Chuột phải project → *Run As → Maven test* |
| Xem web app (SUT) | Chạy `LocalWebServer` → *Run As → Java Application* |

Chi tiết: [docs/Setup-Guide.md](docs/Setup-Guide.md) (cần cài thêm plugin **TestNG for Eclipse**).

## Cấu trúc

```
BTCK/                          ← chính là Eclipse project
├─ pom.xml                     DUY NHẤT 1 file pom
├─ sut-web/                    SUT: web app tĩnh (HTML/CSS/JS) – mở index.html xem được ngay
├─ src/main/java/vn/edu/ktpm/minishop/
│  ├─ auth/   shop/   math/    Nghiệp vụ MiniShop (đối tượng của tầng unit)
│  └─ support/                 LocalWebServer – web server nhúng phục vụ tầng UI
├─ src/test/java/vn/edu/ktpm/minishop/
│  ├─ unit/                    JUnit 5 + EasyMock          – 64 TC
│  ├─ web/                     TestNG + Selenium (Page Object) – 11 TC
│  ├─ pw/                      Playwright (đối chứng)      – 3 TC
│  └─ mobile/                  Appium (tùy chọn)           – 2 TC
├─ src/test/resources/         testng.xml · login-data.csv · allure.properties
├─ ci/
│  ├─ Jenkinsfile              Dây chuyền chính + Allure
│  └─ workflows/ci.yml         Cổng kiểm tra mỗi PR (chép sang .github/workflows/)
├─ selenium-ide/               Kịch bản record bằng Selenium IDE (.side)
└─ ChayTest.bat
```

## Tài liệu

| Tập tin | Nội dung |
| --- | --- |
| [docs/Requirements.md](docs/Requirements.md) | Đề bài gốc của môn học |
| [docs/DeBai-SUT.md](docs/DeBai-SUT.md) | **Đặc tả hệ thống MiniShop** (đề bài tự đặt) |
| [docs/Plan.md](docs/Plan.md) | **Kế hoạch rút gọn v2** – giữ/bỏ/gộp cái gì và vì sao |
| [docs/TestPlan.md](docs/TestPlan.md) | Kế hoạch kiểm thử (phạm vi, chiến lược, tiêu chí vào/ra) |
| [docs/TestDesign-Techniques.md](docs/TestDesign-Techniques.md) | **5 kỹ thuật đã học** áp dụng vào SUT |
| [docs/TestCases.md](docs/TestCases.md) | Danh sách 80 test case **+ ma trận truy vết** (mục 5) |
| [docs/Setup-Guide.md](docs/Setup-Guide.md) | Hướng dẫn cài đặt 13 bước theo **Eclipse**, có ô kiểm chứng |
| [docs/Report.md](docs/Report.md) | Kết quả, defect log, so sánh công cụ, bài học |
| [docs/PhanCong-QuayVideo.md](docs/PhanCong-QuayVideo.md) | **Phân công quay video 2 người**: 10 cảnh, ai quay gì, checklist, dự phòng |
| [docs/KichBan-ChiTiet.md](docs/KichBan-ChiTiet.md) | **Lời thoại nguyên văn + 8 nhóm việc phải làm trước khi quay** |
| [docs/jira-import.csv](docs/jira-import.csv) | Nhập hàng loạt 36 issue vào Jira — kèm **ngày tạo, hạn, ngày hoàn thành, ước lượng và thời gian đã dùng** |

## Tài khoản mẫu của MiniShop

| Username | Password | Vai trò |
| --- | --- | --- |
| `standard_user` | `secret_sauce` | Khách thường |
| `vip_user` | `secret_sauce` | Khách VIP (được giảm giá thêm) |
| `locked_user` | `secret_sauce` | Bị quản trị viên khóa |

Mã giảm giá hợp lệ: `SALE10`. Sai 3 lần liên tiếp → tài khoản bị khóa (60 giây trên bản web, 15 phút theo đặc tả).

## Báo cáo

```bash
allure serve target/allure-results     # cả 3 tầng test nằm chung một báo cáo
```

Độ phủ mã lệnh: `target/site/jacoco/index.html`

## Ghi chú về bản v1

Bản đầy đủ (7 module Maven, 188 test case, Selenium Grid, chạy song song, retry analyzer) được lưu tại
`../BTCK-v1-daydu.zip`. Các phần đã lược bỏ và lý do được ghi trong [docs/Plan.md](docs/Plan.md) mục 1
và [docs/Report.md](docs/Report.md).
