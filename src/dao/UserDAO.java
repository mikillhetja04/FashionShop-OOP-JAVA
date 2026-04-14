package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DBpackage.DBConnection;
import model.User;
import utils.HashUtils;

public class UserDAO {

    /**
     * Hàm kiểm tra đăng nhập
     * @param username Tên đăng nhập từ giao diện
     * @param password Mật khẩu từ giao diện
     * @return Đối tượng User nếu đúng, null nếu sai tài khoản/mật khẩu
     */
    public User checkLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        // Dùng try-with-resources → tự động đóng conn, ps, rs — không rò rỉ kết nối
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            // Băm mật khẩu trước khi so sánh với DB
            ps.setString(2, HashUtils.hashPassword(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    /**
     * Hàm đăng ký tài khoản mới cho khách hàng
     * @param u Đối tượng User chứa (username, password, email)
     * @return true nếu đăng ký thành công, false nếu thất bại (trùng username...)
     */
    public boolean registerUser(User u) {
        // Mặc định khi đăng ký qua app sẽ là khách hàng (CUSTOMER)
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'CUSTOMER')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            // Băm mật khẩu trước khi lưu vào DB
            ps.setString(2, HashUtils.hashPassword(u.getPassword()));
            ps.setString(3, u.getEmail());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký (có thể trùng username): " + e.getMessage());
            return false;
        }
    }

    /**
     * Hàm kiểm tra xem tên đăng nhập đã tồn tại trong DB chưa
     * @param username Tên cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa có
     */
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
}