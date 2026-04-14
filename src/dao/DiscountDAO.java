package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import DBpackage.DBConnection;
import model.DiscountCode;

/**
 * DAO chịu trách nhiệm tra cứu mã giảm giá từ Database.
 * Tuân theo nguyên tắc Single Responsibility: chỉ làm việc với bảng discount_codes.
 */
public class DiscountDAO {

    /**
     * Tra cứu mã giảm giá theo code mà người dùng nhập vào.
     * Chỉ trả về mã CÒN HIỆU LỰC (is_active = TRUE).
     *
     * @param code Mã giảm giá người dùng nhập (VD: "SALE10")
     * @return Đối tượng DiscountCode nếu hợp lệ, null nếu không tồn tại / hết hạn
     */
    public DiscountCode findActiveCode(String code) {
        // Chỉ lấy mã còn hiệu lực — tránh dùng mã hết hạn
        String sql = "SELECT * FROM discount_codes WHERE code = ? AND is_active = TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim().toUpperCase()); // Chuẩn hóa: bỏ khoảng trắng, viết HOA
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DiscountCode(
                        rs.getString("code"),
                        rs.getString("description"),
                        rs.getInt("discount_percent"),
                        rs.getBoolean("is_active")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi tra cứu mã giảm giá: " + e.getMessage());
        }
        return null; // Trả về null nếu không tìm thấy hoặc đã hết hạn
    }

    // Main để test nhanh trước khi bàn giao
    public static void main(String[] args) {
        DiscountDAO dao = new DiscountDAO();

        System.out.println("--- TEST MÃ GIẢM GIÁ ---");

        // Test mã hợp lệ
        DiscountCode dc = dao.findActiveCode("SALE10");
        if (dc != null) {
            System.out.println("✅ Tìm thấy: " + dc);
        } else {
            System.out.println("❌ Mã không hợp lệ hoặc đã hết hạn.");
        }

        // Test mã sai
        DiscountCode dc2 = dao.findActiveCode("MAGIASAI");
        System.out.println(dc2 == null ? "✅ Mã sai bị từ chối đúng." : "❌ Lỗi logic!");
    }
}
