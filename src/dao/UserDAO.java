package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        // Câu lệnh SQL tìm người dùng khớp cả user và pass
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
         
            // Truyền tham số để chống SQL Injection
            ps.setString(1, username);
         // Khi kiểm tra, cũng phải băm mật khẩu người dùng vừa nhập thì mới khớp với DB
            ps.setString(2, HashUtils.hashPassword(password));
            
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
         // Trước khi set vào PreparedStatement, hãy băm nó!
        	ps.setString(2, HashUtils.hashPassword(u.getPassword()));
            
            
            ps.setString(3, u.getEmail());
            
            int result = ps.executeUpdate();
            return result > 0; // Trả về true nếu có ít nhất 1 dòng được chèn vào
        } catch (Exception e) {
            // Nếu trùng username, MySQL sẽ báo lỗi và nhảy vào đây
            System.err.println("Lỗi đăng ký: " + e.getMessage());
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
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Nếu count > 0 nghĩa là đã có người dùng tên này
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        String targetUser = "khachhang_vip1"; // Tên muốn đăng ký

        System.out.println("--- ĐANG KIỂM TRA TÊN ĐĂNG NHẬP ---");
        if (dao.isUsernameExists(targetUser)) {
            System.out.println("❌ Tên '" + targetUser + "' đã có người sử dụng. Vui lòng chọn tên khác!");
        } else {
            System.out.println("✅ Tên '" + targetUser + "' còn trống. Tiến hành đăng ký...");
            
            User newUser = new User();
            newUser.setUsername(targetUser);
            newUser.setPassword("matkhau1234");
            newUser.setEmail("vip1@gmail.com");

            if (dao.registerUser(newUser)) {
                System.out.println("🎉 Đăng ký THÀNH CÔNG tài khoản: " + targetUser);
            }
        }
    }
}