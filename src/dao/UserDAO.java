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
    /**
     * Hàm đăng ký tài khoản mới cho khách hàng
     * @param u Đối tượng User chứa (username, password, email)
     * @return true nếu đăng ký thành công, false nếu thất bại (trùng username...)
     */
    public boolean registerUser(User u) {
        // Mặc định khi đăng ký qua web/app sẽ là khách hàng (CUSTOMER)
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'CUSTOMER')";
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getEmail());
            
            int result = ps.executeUpdate();
            return result > 0; // Trả về true nếu có ít nhất 1 dòng được chèn vào
        } catch (Exception e) {
            // Nếu trùng username, MySQL sẽ báo lỗi và nhảy vào đây
            System.err.println("Lỗi đăng ký: " + e.getMessage());
            return false;
        }
    }

    // Hàm Main để TV2 tự kiểm chứng trước khi bàn giao cho nhóm
//    public static void main(String[] args) {
//        UserDAO dao = new UserDAO();
//        
//        // Thử đăng nhập với tài khoản admin đã thêm ở Tuần 1
//        User u = dao.checkLogin("admin01", "654321");
//        
//        if (u != null) {
//            System.out.println("✅ Đăng nhập THÀNH CÔNG!");
//            System.out.println("Chào mừng " + u.getRole() + ": " + u.getUsername());
//        } else {
//            System.out.println("❌ Sai tài khoản hoặc mật khẩu rồi bạn ơi!");
//        }
//    }
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        // Tạo một đối tượng người dùng mới để đi đăng ký
        User newUser = new User();
        newUser.setUsername("khachhang_moi");
        newUser.setPassword("password678");
        newUser.setEmail("khach@gmail.com");

        if (dao.registerUser(newUser)) {
            System.out.println("🎉 Đăng ký THÀNH CÔNG! Chào mừng khách hàng mới.");
        } else {
            System.out.println("❌ Đăng ký THẤT BẠI! (Có thể do trùng tên đăng nhập).");
        }
    }
}