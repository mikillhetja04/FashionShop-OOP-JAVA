🛒 Fashion Shop - Hệ Thống Quản Lý Cửa Hàng Thời Trang
Đồ án môn học: Lập trình hướng đối tượng (OOP) > Ngôn ngữ: Java | Database: MySQL | Kiến trúc: MVC & DAO Pattern

🌟 Tổng Quan Dự Án
Hệ thống quản lý và bán hàng thời trang trực tuyến được xây dựng dựa trên các nguyên tắc của lập trình hướng đối tượng. Dự án tập trung vào tính bảo mật, hiệu suất truy vấn và trải nghiệm người dùng tối ưu.

🛠️ Tech Stack & Công Cụ
Ngôn ngữ: Java 25 (JDK 25)

Thư viện: JDBC (MySQL Connector/J 9.6.0)

Cơ sở dữ liệu: MySQL 8.0+

Công cụ: Eclipse IDE, Git/GitHub, MySQL Workbench

🏗️ Cấu Trúc Hệ Thống (Mô Hình MVC)
Hệ thống được chia thành các Package rành mạch:

model: Chứa các lớp đối tượng (POJO) áp dụng tính Đóng gói (Encapsulation).

dao: Tầng xử lý logic dữ liệu (Data Access Object) với JDBC.

DBpackage: Quản lý kết nối Cơ sở dữ liệu tập trung.

lib: Chứa thư viện kết nối ngoại vi (.jar).

🚀 Các Tính Năng Đã Hoàn Thành (Backend Core)
1. Quản lý Người dùng & Bảo mật
[x] Authentication: Đăng nhập phân quyền (Admin/Customer).

[x] Registration: Đăng ký thành viên mới với cơ chế kiểm tra trùng lặp tài khoản.

[x] Security: Chống tấn công SQL Injection bằng PreparedStatement.

2. Quản lý Kho hàng (CRUD)
[x] Product Management: Thêm, Sửa, Xóa sản phẩm trực tiếp từ Java.

[x] Smart Search: Tìm kiếm sản phẩm theo từ khóa linh hoạt (LIKE %keyword%).

3. Hệ thống Giỏ hàng & Giao dịch
[x] Transaction Processing: Xử lý thanh toán an toàn với cơ chế Commit/Rollback.

[x] Batch Processing: Tối ưu hiệu suất khi lưu chi tiết hóa đơn số lượng lớn.

4. Báo cáo & Thống kê (Dành cho Admin)
[x] Revenue Analytics: Thống kê tổng doanh thu thực tế.

[x] Monthly Reporting: Phân tích doanh thu theo từng tháng trong năm bằng SQL nâng cao.

📖 Hướng Dẫn Cài Đặt (Cho Thành Viên Nhóm/Giảng Viên)
Clone dự án: git clone https://github.com/mikillhetja04/FashionShop-OOP-JAVA.git

Import Database: Chạy file Script SQL (đã cung cấp) trong MySQL Workbench.

Cấu hình kết nối: Sửa username và password trong file DBConnection.java cho khớp với máy cá nhân.

Run: Chạy file Main hoặc các file DAO để kiểm tra logic.

Thực hiện bởi: TV2 - Backend Developer
Trạng thái: Hoàn thành 100% Core Backend (Tuần 4)

