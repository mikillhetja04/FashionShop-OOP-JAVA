package dao;

import model.User;

/**
 * Interface định nghĩa hợp đồng cho tầng truy cập dữ liệu người dùng.
 * Thể hiện nguyên tắc Abstraction (Tính trừu tượng) trong OOP.
 */
public interface IUserDAO {
    User checkLogin(String username, String password);
    boolean registerUser(User u);
    boolean isUsernameExists(String username);
}
