package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DBpackage.DBConnection;
import model.User;
import utils.HashUtils;

/**
 * Tầng truy cập dữ liệu người dùng — implement IUserDAO.
 * Sử dụng HashUtils (SHA-256 + Salt) để bảo mật mật khẩu.
 */
public class UserDAO implements IUserDAO {

    /**
     * Kiểm tra đăng nhập.
     * Vì mật khẩu được lưu dưới dạng "salt:hash", cần lấy hash từ DB về rồi dùng
     * HashUtils.verifyPassword() để so sánh — không thể dùng WHERE password = ?
     * trực tiếp.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu gốc
     * @return Đối tượng User nếu đúng, null nếu sai
     */
    @Override
    public User checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    // So sánh mật khẩu với salt
                    if (HashUtils.verifyPassword(password, storedHash)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    /**
     * Đăng ký tài khoản mới (mặc định role = CUSTOMER).
     *
     * @param u Đối tượng User chứa (username, password, email)
     * @return true nếu đăng ký thành công
     */
    @Override
    public boolean registerUser(User u) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'CUSTOMER')";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            // Băm + Salt mật khẩu trước khi lưu vào DB
            ps.setString(2, HashUtils.hashPassword(u.getPassword()));
            ps.setString(3, u.getEmail());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký (có thể trùng username): " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa.
     *
     * @param username Tên cần kiểm tra
     * @return true nếu đã tồn tại
     */
    @Override
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra username: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        User admin = new User();
        admin.setUsername("custom1");
        admin.setPassword("123456");
        admin.setEmail("custom1@shop.com");

        if (dao.registerUser(admin)) {
            System.out.println("✅ Đăng ký thành công! Giờ hãy vào MySQL gõ lệnh UPDATE role thành ADMIN nhé.");
        }
    }
}
