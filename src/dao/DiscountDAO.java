package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import DBpackage.DBConnection;
import model.DiscountCode;

/**
 * Tầng truy cập dữ liệu mã giảm giá — implement IDiscountDAO.
 * Chỉ làm việc với bảng discount_codes (Single Responsibility).
 */
public class DiscountDAO implements IDiscountDAO {

    /**
     * Tra cứu mã giảm giá còn hiệu lực.
     *
     * @param code Mã người dùng nhập (VD: "sale10" → chuẩn hóa thành "SALE10")
     * @return DiscountCode nếu hợp lệ, null nếu không tồn tại hoặc hết hạn
     */
    @Override
    public DiscountCode findActiveCode(String code) {
        String sql = "SELECT * FROM discount_codes WHERE code = ? AND is_active = TRUE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code.trim().toUpperCase()); // Chuẩn hóa: bỏ khoảng trắng, HOA
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
        return null;
    }

    /** Test nhanh */
    public static void main(String[] args) {
        DiscountDAO dao = new DiscountDAO();

        DiscountCode dc = dao.findActiveCode("SALE10");
        System.out.println(dc != null ? "✅ " + dc : "❌ Mã không hợp lệ.");

        DiscountCode dc2 = dao.findActiveCode("MAGIASAI");
        System.out.println(dc2 == null ? "✅ Mã sai bị từ chối đúng." : "❌ Lỗi logic!");
    }
}
