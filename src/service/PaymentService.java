package service;

import java.util.List;
import dao.DiscountDAO;
import model.CartItem;
import model.DiscountCode;

/**
 * SERVICE chịu trách nhiệm toàn bộ logic THANH TOÁN.
 * ====================================================
 * Luồng tính toán:
 *   1. Tổng tiền gốc từ giỏ hàng
 *   2. Áp mã giảm giá (nếu có) → giảm theo %
 *   3. Cộng thuế VAT 10%
 *   4. Trả về PaymentResult để View tự hiển thị
 *
 * Nguyên tắc OOP:
 *   - Tách biệt khỏi DAO và View (SRP)
 *   - Không truy cập DB trực tiếp, ủy quyền cho DiscountDAO
 *   - Không in ấn trực tiếp ra console — View quyết định cách hiển thị
 */
public class PaymentService {

    public static final double DEFAULT_TAX_RATE = 0.10; // VAT 10%

    private final DiscountDAO discountDAO;

    public PaymentService() {
        this.discountDAO = new DiscountDAO();
    }

    // ==================================================
    //  BƯỚC 1: TÍNH TỔNG TIỀN GỐC
    // ==================================================

    /**
     * Cộng tổng thành tiền của tất cả sản phẩm trong giỏ.
     * Ví dụ: [Áo 150k ×2] + [Quần 200k ×1] = 500.000đ
     */
    public double tinhTongTienGoc(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return 0;
        double tongTien = 0;
        for (CartItem item : cartItems) {
            tongTien += item.getSubTotal();
        }
        return tongTien;
    }

    // ==================================================
    //  BƯỚC 2: ÁP MÃ GIẢM GIÁ
    // ==================================================

    /**
     * Tra cứu mã giảm giá và tính số tiền được giảm.
     * @return Số tiền được giảm (>= 0). Trả 0 nếu mã không hợp lệ.
     */
    public double tinhSoTienGiam(double tongTienGoc, String maGiamGia) {
        if (maGiamGia == null || maGiamGia.trim().isEmpty()) return 0;
        DiscountCode dc = discountDAO.findActiveCode(maGiamGia);
        if (dc == null) return 0;
        return tongTienGoc * (dc.getDiscountPercent() / 100.0);
    }

    /**
     * Lấy thông tin mã giảm giá (để View hiển thị thông báo).
     * @return DiscountCode nếu hợp lệ, null nếu không tìm thấy
     */
    public DiscountCode timMaGiamGia(String maGiamGia) {
        if (maGiamGia == null || maGiamGia.trim().isEmpty()) return null;
        return discountDAO.findActiveCode(maGiamGia);
    }

    // ==================================================
    //  BƯỚC 3: TÍNH THUẾ VAT
    // ==================================================

    /** Tính tiền VAT trên số tiền sau giảm giá */
    public double tinhTienThue(double tienSauGiam, double taxRate) {
        return tienSauGiam * taxRate;
    }

    // ==================================================
    //  BƯỚC 4: TỔNG HỢP — TÍNH THÀNH TIỀN CUỐI CÙNG
    // ==================================================

    /**
     * Phương thức CHÍNH — tính toàn bộ và trả về kết quả thanh toán.
     * View gọi khi người dùng bấm "Thanh Toán".
     *
     * @param cartItems Giỏ hàng hiện tại
     * @param maGiamGia Mã giảm giá (để trống nếu không có)
     * @return PaymentResult chứa đủ thông tin để View hiển thị
     */
    public PaymentResult tinhThanhToan(List<CartItem> cartItems, String maGiamGia) {
        double tongGoc      = tinhTongTienGoc(cartItems);
        double soTienGiam   = tinhSoTienGiam(tongGoc, maGiamGia);
        DiscountCode dc     = timMaGiamGia(maGiamGia);
        double tienSauGiam  = tongGoc - soTienGiam;
        double tienThue     = tinhTienThue(tienSauGiam, DEFAULT_TAX_RATE);
        double thanhTienCuoi = tienSauGiam + tienThue;

        return new PaymentResult(tongGoc, soTienGiam, tienThue, thanhTienCuoi, dc);
    }

    // ==================================================
    //  INNER CLASS: KẾT QUẢ THANH TOÁN
    // ==================================================

    /**
     * Đóng gói kết quả tính toán — View nhận về rồi tự hiển thị theo ý muốn.
     */
    public static class PaymentResult {
        private final double tongTienGoc;
        private final double soTienGiam;
        private final double tienThue;
        private final double thanhTienCuoi;
        private final DiscountCode discountCode; // null nếu không dùng mã

        public PaymentResult(double tongTienGoc, double soTienGiam,
                             double tienThue, double thanhTienCuoi,
                             DiscountCode discountCode) {
            this.tongTienGoc    = tongTienGoc;
            this.soTienGiam     = soTienGiam;
            this.tienThue       = tienThue;
            this.thanhTienCuoi  = thanhTienCuoi;
            this.discountCode   = discountCode;
        }

        public double getTongTienGoc()    { return tongTienGoc; }
        public double getSoTienGiam()     { return soTienGiam; }
        public double getTienThue()       { return tienThue; }
        public double getThanhTienCuoi()  { return thanhTienCuoi; }
        public DiscountCode getDiscountCode() { return discountCode; }

        /** Kiểm tra có áp mã giảm giá không */
        public boolean hasDiscount() {
            return discountCode != null && soTienGiam > 0;
        }

        /** Trả về chuỗi hóa đơn — View có thể dùng để in hoặc hiển thị */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Tổng tiền gốc :  %,15.0f VNĐ%n", tongTienGoc));
            if (hasDiscount()) {
                sb.append(String.format("Giảm giá [%s]  : -%,15.0f VNĐ%n",
                        discountCode.getCode(), soTienGiam));
            }
            sb.append(String.format("Thuế VAT (10%%) :  %,15.0f VNĐ%n", tienThue));
            sb.append(String.format("THÀNH TIỀN     :  %,15.0f VNĐ", thanhTienCuoi));
            return sb.toString();
        }
    }

    /** Test console */
    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        model.Product ao   = new model.Product(1, "Áo Phông Basic", 150_000, 10);
        model.Product quan = new model.Product(2, "Quần Jogger",    300_000, 5);

        List<CartItem> cart = new java.util.ArrayList<>();
        cart.add(new CartItem(ao,   2));
        cart.add(new CartItem(quan, 1));

        System.out.println("=== Kịch bản 1: Dùng mã SALE10 ===");
        PaymentResult r1 = service.tinhThanhToan(cart, "SALE10");
        System.out.println(r1);

        System.out.println("\n=== Kịch bản 2: Không mã giảm giá ===");
        PaymentResult r2 = service.tinhThanhToan(cart, "");
        System.out.println(r2);
    }
}
