package DBpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Đường dẫn đến "căn phòng" fashion_shop_db của chúng ta
    private static final String URL = "jdbc:mysql://localhost:3306/fashion_shop_db";
    private static final String USER = "root"; // Tài khoản mặc định của MySQL
    
    // ĐIỀN MẬT KHẨU MYSQL CỦA BẠN VÀO GIỮA 2 DẤU NGOẶC KÉP BÊN DƯỚI:
    // (Thường sinh viên hay đặt là root, 123456, admin, hoặc để trống "")
    private static final String PASSWORD = "Tdh@040404"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Khai báo với Java là tôi muốn dùng thư viện của MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Mở cửa bước vào database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Tuyệt vời! Kết nối Database fashion_shop_db thành công! 🎉");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Lỗi 1: Không tìm thấy file thư viện (mysql-connector.jar). Bạn kiểm tra lại bước Add External JARs nhé!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi 2: Sai tên Database hoặc sai Mật khẩu. Bạn kiểm tra lại dòng PASSWORD nhé!");
            e.printStackTrace();
        }
        return conn;
    }

    // Hàm main để chạy test thử nghiệm
    public static void main(String[] args) {
        getConnection();
    }
}