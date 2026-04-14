package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tiện ích kiểm tra dữ liệu đầu vào.
 * Tập hợp các hàm validate được dùng ở nhiều nơi trong ứng dụng.
 */
public class DataValidator {

    // --- Email ---

    /** Kiểm tra định dạng Email hợp lệ */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email.trim());
        return matcher.matches();
    }

    // --- Mật khẩu ---

    /** Kiểm tra mật khẩu phải >= 6 ký tự */
    public static boolean isStrongPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // --- Chuỗi ---

    /** Kiểm tra chuỗi không rỗng và không chỉ có khoảng trắng */
    public static boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }

    // --- Số ---

    /** Kiểm tra số double có hợp lệ không (> 0) */
    public static boolean isPositiveDouble(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            return Double.parseDouble(text.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Kiểm tra số nguyên có hợp lệ không (>= 0) */
    public static boolean isNonNegativeInt(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(text.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Kiểm tra số nguyên có hợp lệ không (> 0) */
    public static boolean isPositiveInt(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(text.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}