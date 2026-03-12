package model;

public class User {
    // 1. Thuộc tính (Attributes) - Bắt buộc phải là private (Tính đóng gói)
    private int userId;
    private String username;
    private String password;
    private String email;
    private String role;

    // 2. Hàm khởi tạo (Constructor) không tham số
    public User() {
    }

    // 3. Hàm khởi tạo (Constructor) đầy đủ tham số
    public User(int userId, String username, String password, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // 4. Các hàm Getter và Setter để truy xuất dữ liệu an toàn
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}