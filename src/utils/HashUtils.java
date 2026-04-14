package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Tiện ích bảo mật mật khẩu.
 *
 * Thuật toán: SHA-256 + Salt ngẫu nhiên (16 bytes)
 * Format lưu DB: "BASE64(salt):HEX(hash)"
 * Ví dụ: "rXj3kL9m...==:a3f1c2d4..."
 *
 * So với SHA-256 thuần, thêm Salt giúp:
 *  - Ngăn tấn công Rainbow Table (cùng pass → khác hash)
 *  - Ngăn tấn công Dictionary Attack trên hash đã lấy được
 */
public class HashUtils {

    private static final String ALGORITHM = "SHA-256";
    private static final String SEPARATOR = ":";
    private static final int SALT_LENGTH = 16; // bytes

    /**
     * Băm mật khẩu với Salt ngẫu nhiên.
     * Dùng khi ĐĂNG KÝ tài khoản mới.
     *
     * @param password Mật khẩu gốc người dùng nhập
     * @return Chuỗi dạng "BASE64(salt):HEX(hash)" để lưu vào DB
     */
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        String hash = hashWithSalt(password, salt);
        return Base64.getEncoder().encodeToString(salt) + SEPARATOR + hash;
    }

    /**
     * Kiểm tra mật khẩu người dùng nhập có khớp với hash trong DB không.
     * Dùng khi ĐĂNG NHẬP.
     *
     * @param rawPassword    Mật khẩu gốc người dùng nhập
     * @param storedPassword Chuỗi "BASE64(salt):HEX(hash)" lưu trong DB
     * @return true nếu khớp, false nếu sai
     */
    public static boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || !storedPassword.contains(SEPARATOR)) {
            return false;
        }
        String[] parts = storedPassword.split(SEPARATOR, 2);
        if (parts.length != 2) return false;

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String expectedHash = parts[1];
        String actualHash = hashWithSalt(rawPassword, salt);
        return actualHash.equals(expectedHash);
    }

    // --- Private Helpers ---

    private static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }

    private static String hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi thuật toán mã hóa SHA-256!", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}