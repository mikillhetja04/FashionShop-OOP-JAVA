package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidator {
    // Kiểm tra định dạng Email
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Kiểm tra mật khẩu (ví dụ phải trên 6 ký tự)
    public static boolean isStrongPassword(String password) {
        return password != null && password.length() >= 6;
    }
}