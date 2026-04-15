# FashionShopOOP - Project Presentation

## Slide 1 - Muc tieu du an

- Xay dung he thong quan ly cua hang thoi trang bang Java Swing + MySQL.
- Ap dung OOP day du: encapsulation, abstraction, inheritance, polymorphism.
- To chuc theo kien truc `MVC + DAO` de de mo rong va bao tri.
- Ho tro 2 vai tro: `ADMIN` va `CUSTOMER`.

---

## Slide 2 - Cong nghe su dung

- `Java (JDK 17+)` cho backend logic va desktop UI.
- `Java Swing` cho giao dien desktop.
- `MySQL + JDBC` cho luu tru du lieu.
- `MySQL Connector/J` de ket noi CSDL.
- `Git/GitHub` cho quan ly ma nguon.

---

## Slide 3 - Cau truc thu muc tong quan

```text
src/
  DBpackage/   -> ket noi CSDL
  model/       -> cac lop du lieu (POJO)
  dao/         -> truy cap du lieu (CRUD, query, transaction)
  service/     -> nghiep vu thanh toan
  utils/       -> hash password, validate input
  view/        -> giao dien Swing (Login, Main, Product, Order, Stat)
```

- `model` khong phu thuoc vao `view`.
- `view` goi `dao/service`, khong chua SQL truc tiep.
- `dao` dong vai tro cau noi giua ung dung va CSDL.

---

## Slide 4 - Kien truc lop (MVC + DAO)

- **Model:** `User`, `Product`, `Order`, `OrderDetail`, `CartItem`, `Category`, `DiscountCode`.
- **View:** `LoginForm`, `MainForm`, `ProductPanel`, `OrderPanel`, `StatPanel`.
- **DAO:** `UserDAO`, `ProductDAO`, `OrderDAO`, `DiscountDAO` + interface tuong ung.
- **Service:** `PaymentService` xu ly tinh toan hoa don.

Luong goi:
`View -> Service/DAO -> DBConnection -> MySQL`

---

## Slide 5 - Luong dang nhap va phan quyen

1. User nhap username/password tai `LoginForm`.
2. `UserDAO.checkLogin()` truy van theo username.
3. Mat khau duoc xac thuc qua `HashUtils.verifyPassword()`.
4. Thanh cong -> mo `MainForm(userId, role, username)`.
5. Role quyet dinh tab duoc xem:
   - `ADMIN`: San pham + Don hang + Thong ke.
   - `CUSTOMER`: San pham (chi xem) + Don hang.

---

## Slide 6 - Quan ly san pham

- `ProductPanel` hien bang san pham va tim kiem theo ten.
- Admin co the:
  - Them san pham.
  - Cap nhat san pham.
  - Xoa san pham.
- Customer chi duoc xem va tim kiem.
- Validate dau vao bang `DataValidator`:
  - Ten khong rong.
  - Gia > 0.
  - Ton kho >= 0.

---

## Slide 7 - Gio hang va thanh toan

1. Chon san pham + so luong trong `OrderPanel`.
2. Kiem tra ton kho truoc khi them vao gio.
3. Ap ma giam gia (neu co) qua `PaymentService` + `DiscountDAO`.
4. Tinh tien:
   - Tong goc.
   - Tru giam gia.
   - Cong VAT 10%.
   - Ra thanh tien cuoi.
5. Xac nhan thanh toan va tao don.

---

## Slide 8 - Transaction tao don hang

`OrderDAO.createOrder()` chay trong mot transaction:

1. Insert vao bang `orders`.
2. Insert batch vao `order_details`.
3. Tru ton kho tung san pham (`products.stock_quantity = stock - qty`).
4. Neu bat ky buoc nao loi -> `ROLLBACK`.
5. Thanh cong toan bo -> `COMMIT`.

Y nghia:
- Dam bao tinh toan ven du lieu.
- Khong xay ra tinh trang tao don nhung khong tru kho (hoac nguoc lai).

---

## Slide 9 - Thong ke doanh thu

- `StatPanel` cho Admin.
- Co the chon nam de xem doanh thu theo thang.
- Du lieu lay tu `OrderDAO.getMonthlyRevenue(year)`.
- Tong doanh thu he thong lay tu `OrderDAO.getTotalRevenue()`.
- Hien thi bang + cot bieu do ky tu de nhin nhanh xu huong.

---

## Slide 10 - Bao mat va chat luong code

- Password khong luu plain text, dung `SHA-256 + random salt`.
- Chuan luu hash: `base64(salt):hex(hash)`.
- Dung `PreparedStatement` de tranh SQL Injection.
- `try-with-resources` de dong ket noi/statement/resultset tu dong.
- Tach interface DAO (`IUserDAO`, `IProductDAO`, ...) de de test/mock/thay doi implementation.

---

## Slide 11 - Cac bang du lieu chinh (suy ra tu code DAO)

- `users(user_id, username, password, email, role)`
- `products(product_id, category_id, product_name, price, size, color, stock_quantity)`
- `categories(category_id, category_name)`
- `orders(order_id, user_id, total_amount, status, order_date)`
- `order_details(detail_id, order_id, product_id, quantity, unit_price)`
- `discount_codes(code, description, discount_percent, is_active)`

---

## Slide 12 - Diem manh hien tai

- Kien truc ro rang, de doc cho do an OOP.
- Chuc nang xuyen suot tu login -> mua hang -> thong ke.
- Co phan quyen theo vai tro.
- Co transaction cho thao tac nhay cam (tao don + tru kho).
- Co tach lop nghiep vu (`PaymentService`) thay vi de logic trong UI.

---

## Slide 13 - Han che va huong cai tien

- UI Swing chua responsive theo quy mo lon.
- Chua co test tu dong (unit test/integration test).
- Logging hien tai chu yeu la `System.err.println`.
- Chua co migration/schema versioning cho DB.

Huong nang cap de xuat:
- Them `JUnit` cho `service` va `dao`.
- Bo sung logging framework (vi du `slf4j` + `logback`).
- Tach config theo moi truong (dev/test/prod).
- Dong goi thanh executable `.jar` co script chay nhanh.

---

## Slide 14 - Huong dan chay nhanh de demo

1. Tao `config.properties` dung thong tin MySQL local.
2. Import database schema + du lieu mau.
3. Chay class `view.LoginForm`.
4. Dang nhap:
   - Admin de xem day du chuc nang.
   - Customer de demo phan quyen.
5. Thu 3 scenario:
   - CRUD san pham.
   - Dat don co/khong co ma giam gia.
   - Xem thong ke doanh thu theo nam.

---

## Slide 15 - Ban do doc code (de on thi/bao cao)

- Bat dau o `view/LoginForm` -> `view/MainForm`.
- Doc tiep 3 panel theo thu tu:
  1. `ProductPanel` (CRUD + search + role UI).
  2. `OrderPanel` (cart + checkout flow).
  3. `StatPanel` (reporting).
- Sau do doc DAO lien quan:
  - `UserDAO`, `ProductDAO`, `OrderDAO`, `DiscountDAO`.
- Ket thuc bang:
  - `PaymentService`, `HashUtils`, `DBConnection`.

---

## Slide 16 - Ket luan ngan

- Du an da dat muc tieu mot he thong quan ly cua hang thoi trang theo OOP.
- Cac module da lien ket thanh luong nghiep vu hoan chinh.
- Nen tang hien tai phu hop de:
  - Bao cao do an,
  - Mo rong chuc nang,
  - Hoac chuyen doi dan sang kien truc web trong giai doan tiep theo.
