# Kịch bản Selenium IDE

Thư mục này chứa các tập tin `.side` được ghi lại (record) bằng tiện ích **Selenium IDE**.

## Cách tạo (quay vào video – phần 7 của kịch bản)

1. Cài tiện ích Selenium IDE cho Chrome/Firefox.
2. Khởi động SUT: mở trực tiếp `sut-web/index.html`, hoặc chạy
   `python -m http.server 8000` trong thư mục `sut-web` rồi mở `http://localhost:8000`.
3. Bấm **Record** → thao tác kịch bản đăng nhập → thêm giỏ hàng → thanh toán.
4. Bấm **Play** để chạy lại, lưu dự án thành `minishop-login.side` trong thư mục này.
5. `Export → Java JUnit` để sinh mã, đặt tên `MinishopLoginIDE.java` (chỉ để đối chiếu, không đưa vào suite).

## Điểm cần nói khi so sánh với Page Object viết tay

| | Selenium IDE | Page Object (gói `web`) |
| --- | --- | --- |
| Thời gian tạo test đầu tiên | Vài phút | Lâu hơn (phải dựng khung) |
| Locator | Tự sinh, dễ gãy khi giao diện đổi | `data-testid` do nhóm chủ động đặt |
| Tái sử dụng | Gần như không | Cao (5 lớp Page Object dùng cho 11 test case) |
| Dữ liệu tham số hóa | Hạn chế | `@DataProvider` đọc CSV |
| Chạy trong CI | Phải export sang mã | Chạy trực tiếp bằng Maven |

→ Kết luận: IDE hợp để **tạo bản nháp nhanh**, mã Page Object hợp để **bảo trì lâu dài**.
