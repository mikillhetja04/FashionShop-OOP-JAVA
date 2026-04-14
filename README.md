# 🛒 Fashion Shop — Hệ Thống Quản Lý Cửa Hàng Thời Trang

> **Đồ án môn học:** Lập trình hướng đối tượng (OOP)  
> **Ngôn ngữ:** Java | **Database:** MySQL | **Kiến trúc:** MVC & DAO Pattern

---

## 🌟 Tổng Quan Dự Án

Hệ thống quản lý và bán hàng thời trang được xây dựng trên nền tảng Java Swing & JDBC, áp dụng đầy đủ các nguyên tắc OOP: **Encapsulation**, **Inheritance**, **Polymorphism**, **Abstraction**.

---

## 🛠️ Tech Stack

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java (JDK 17+) |
| Giao diện | Java Swing (MVC) |
| CSDL | MySQL 8.0+ |
| Kết nối DB | JDBC — MySQL Connector/J 9.6.0 |
| Bảo mật | SHA-256 + Salt (SecureRandom) |
| Công cụ | Eclipse IDE, Git/GitHub, MySQL Workbench |

---

## 🏗️ Cấu Trúc Hệ Thống (MVC + DAO)

```
src/
├── DBpackage/         — Kết nối CSDL (đọc từ config.properties)
│   └── DBConnection.java
├── model/             — POJO (Encapsulation)
│   ├── User.java
│   ├── Product.java
│   ├── Order.java
│   ├── OrderDetail.java
│   ├── CartItem.java
│   ├── Category.java
│   └── DiscountCode.java
├── dao/               — Data Access Object (Abstraction)
│   ├── IProductDAO.java  ← Interface
│   ├── IUserDAO.java     ← Interface
│   ├── IOrderDAO.java    ← Interface
│   ├── IDiscountDAO.java ← Interface
│   ├── ProductDAO.java
│   ├── UserDAO.java
│   ├── OrderDAO.java
│   └── DiscountDAO.java
├── service/           — Business Logic
│   └── PaymentService.java
├── utils/             — Tiện ích
│   ├── HashUtils.java    ← SHA-256 + Salt
│   └── DataValidator.java
├── view/              — Giao diện Swing (MVC - View)
│   ├── LoginForm.java
│   ├── MainForm.java
│   ├── ProductPanel.java ← Phân quyền Admin/Customer
│   ├── OrderPanel.java   ← Giỏ hàng & Thanh toán
│   └── StatPanel.java    ← Thống kê doanh thu (Admin only)
└── config.properties  — Cấu hình DB (KHÔNG push lên GitHub)
```

---

## 🚀 Tính Năng Đã Hoàn Thành

### 1. Quản lý Người dùng & Bảo mật
- ✅ Đăng nhập phân quyền Admin / Customer
- ✅ Đăng ký tài khoản mới
- ✅ Bảo mật mật khẩu: **SHA-256 + Salt ngẫu nhiên** (chống Rainbow Table)
- ✅ Chống SQL Injection bằng `PreparedStatement`

### 2. Quản lý Kho hàng (Admin)
- ✅ Thêm / Sửa / Xóa sản phẩm
- ✅ Tìm kiếm sản phẩm theo tên (LIKE %keyword%)
- ✅ Validate đầu vào: tên không rỗng, giá > 0, tồn kho >= 0
- ✅ Phân quyền: Customer chỉ xem, không thấy nút CRUD

### 3. Giỏ hàng & Thanh toán
- ✅ Thêm / Xóa sản phẩm khỏi giỏ
- ✅ Kiểm tra tồn kho trước khi thêm vào giỏ
- ✅ Áp mã giảm giá (tra cứu từ DB)
- ✅ Tự động tính: Tổng gốc → Giảm giá → Thuế VAT 10% → Thành tiền
- ✅ Transaction an toàn: tạo đơn + giảm tồn kho trong 1 giao dịch (Commit/Rollback)

### 4. Thống kê & Báo cáo (Admin)
- ✅ Doanh thu theo tháng (biểu đồ cột trực quan)
- ✅ Tổng doanh thu toàn hệ thống
- ✅ Lọc theo năm

### 5. Kiến trúc & Chất lượng Code
- ✅ Interface DAO: `IProductDAO`, `IUserDAO`, `IOrderDAO`, `IDiscountDAO`
- ✅ `toString()` đầy đủ cho tất cả Model
- ✅ Cấu hình DB từ `config.properties` (không hardcode)
- ✅ Auto-close kết nối với try-with-resources (chống rò rỉ)

---

## 📖 Hướng Dẫn Cài Đặt

### 1. Clone & Import
```bash
git clone https://github.com/mikillhetja04/FashionShop-OOP-JAVA.git
```
Import vào Eclipse: **File → Import → Existing Projects into Workspace**

### 2. Import Database
Chạy file SQL script trong MySQL Workbench

### 3. Tạo file config.properties
Tạo file `src/config.properties` (không được push lên GitHub):
```properties
db.url=jdbc:mysql://localhost:3306/fashion_shop_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
db.user=root
db.password=YOUR_PASSWORD_HERE
```

### 4. Chạy ứng dụng
Chạy class `view.LoginForm` — đăng nhập là xong!

---

> Thực hiện bởi: **TV2 — Backend Developer**  
> Trạng thái: ✅ **Hoàn thành đầy đủ Backend + Frontend (Swing GUI)**
