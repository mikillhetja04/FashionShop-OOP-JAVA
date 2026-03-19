package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import DBpackage.DBConnection;
import model.User;

public class UserDAO {

    /**
     * Hàm kiểm tra đăng nhập
     * @param username Tên đăng nhập từ giao diện
     * @param password Mật khẩu từ giao diện
     * @return Đối tượng User nếu đúng, null nếu sai tài khoản/mật khẩu
     */
    public User checkLogin(String username, String password) {
        // Câu lệnh SQL tìm người dùng khớp cả user và pass
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            // Truyền tham số để chống SQL Injection
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Nếu tìm thấy, đóng gói dữ liệu vào đối tượng User và trả về
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không khớp
    }

    // Hàm Main để TV2 tự kiểm chứng trước khi bàn giao cho nhóm
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        
        // Thử đăng nhập với tài khoản admin đã thêm ở Tuần 1
        User u = dao.checkLogin("admin01", "654321");
        
        if (u != null) {
            System.out.println("✅ Đăng nhập THÀNH CÔNG!");
            System.out.println("Chào mừng " + u.getRole() + ": " + u.getUsername());
        } else {
            System.out.println("❌ Sai tài khoản hoặc mật khẩu rồi bạn ơi!");
        }
    }
}