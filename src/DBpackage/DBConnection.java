package DBpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Đường dẫn đến "căn phòng" fashion_shop_db của chúng ta
    private static final String URL = "jdbc:mysql://localhost:3306/fashion_shop_db";
    private static final String USER = "root"; // Tài khoản mặc định của MySQL

    // ⚠️ BẢO MẬT: KHÔNG điền mật khẩu thật vào đây rồi push lên GitHub!
    // Mỗi thành viên tự điền mật khẩu MySQL CỦA MÌNH vào máy local.
    // (Thường đặt là: root, 123456, hoặc để trống "")
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy MySQL Driver. Kiểm tra lại mysql-connector.jar!", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Hàm main để chạy test thử nghiệm
    public static void main(String[] args) {
        getConnection();
    }
}