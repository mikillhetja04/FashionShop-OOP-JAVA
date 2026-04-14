package DBpackage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Quản lý kết nối CSDL tập trung.
 * Đọc cấu hình từ file config.properties (KHÔNG hardcode thông tin nhạy cảm vào code).
 * Cần đảm bảo file config.properties nằm trong classpath khi chạy.
 */
public class DBConnection {

    private static String url;
    private static String user;
    private static String password;

    // Static block: chỉ đọc config 1 lần khi class được load
    static {
        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                    "Không tìm thấy file config.properties trong classpath!\n" +
                    "Vui lòng tạo file config.properties với nội dung:\n" +
                    "  db.url=jdbc:mysql://localhost:3306/fashion_shop_db\n" +
                    "  db.user=root\n" +
                    "  db.password=YOUR_PASSWORD"
                );
            }

            Properties props = new Properties();
            props.load(input);

            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file config.properties: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy một kết nối mới tới cơ sở dữ liệu.
     * Gọi trong try-with-resources để tự động đóng sau khi dùng xong.
     *
     * @return Connection — kết nối JDBC
     * @throws SQLException nếu không thể kết nối
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy MySQL Driver. Kiểm tra lại mysql-connector.jar!", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    /** Test kết nối — chỉ dùng để debug */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Kết nối CSDL thành công! Schema: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Kết nối thất bại: " + e.getMessage());
        }
    }
}