package service;

import java.util.List;
import dao.DiscountDAO;
import model.CartItem;
import model.DiscountCode;

/**
 * SERVICE chịu trách nhiệm toàn bộ logic THANH TOÁN.
 * ==================================================
 * Luồng tính toán chuẩn:
 *   1. Tính tổng gốc từ các sản phẩm trong giỏ
 *   2. Áp mã giảm giá (nếu có) → giảm theo %
 *   3. Cộng thuế VAT vào
 *   4. Ra số tiền cuối cùng
 *
 * Nguyên tắc OOP:
 *   - Tách biệt hoàn toàn khỏi DAO và View (SRP)
 *   - Không truy cập DB trực tiếp, ủy quyền cho DiscountDAO
 */
public class PaymentService {

    // Thuế VAT mặc định — 10% theo quy định Việt Nam
    public static final double DEFAULT_TAX_RATE = 0.10;

    private final DiscountDAO discountDAO;

    public PaymentService() {
        this.discountDAO = new DiscountDAO();
    }

    // ===================================================
    //  BƯỚC 1: TÍNH TỔNG TIỀN GỐC TỪ GIỎ HÀNG
    // ===================================================

    /**
     * Cộng tổng thành tiền của tất cả sản phẩm trong giỏ.
     * Ví dụ: [Áo 150k ×2] + [Quần 200k ×1] = 500.000đ
     *
     * @param cartItems Danh sách sản phẩm trong giỏ
     * @return Tổng tiền trước giảm giá và thuế
     */
    public double tinhTongTienGoc(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0;
        }
        double tongTien = 0;
        for (CartItem item : cartItems) {
            tongTien += item.getSubTotal(); // getSubTotal() = price × quantity
        }
        return tongTien;
    }

    // ===================================================
    //  BƯỚC 2: ÁP MÃ GIẢM GIÁ
    // ===================================================

    /**
     * Tra cứu mã giảm giá và tính số tiền được giảm.
     * Trả về 0 nếu mã không hợp lệ hoặc đã hết hạn.
     *
     * @param tongTienGoc Tổng tiền gốc chưa giảm
     * @param maGiamGia   Mã người dùng nhập (VD: "SALE10")
     * @return Số tiền được giảm (VD: 50.000đ)
     */
    public double tinhSoTienGiam(double tongTienGoc, String maGiamGia) {
        if (maGiamGia == null || maGiamGia.trim().isEmpty()) {
            return 0; // Không nhập mã → không giảm
        }

        DiscountCode dc = discountDAO.findActiveCode(maGiamGia);

        if (dc == null) {
            System.out.println("⚠️  Mã giảm giá không hợp lệ hoặc đã hết hạn.");
            return 0;
        }

        // Công thức: Số tiền giảm = Tổng gốc × (% giảm / 100)
        double soTienGiam = tongTienGoc * (dc.getDiscountPercent() / 100.0);
        System.out.printf("✅ Áp mã [%s] — Giảm %d%% = %,.0f VNĐ%n",
                dc.getCode(), dc.getDiscountPercent(), soTienGiam);
        return soTienGiam;
    }

    // ===================================================
    //  BƯỚC 3: TÍNH THUẾ VAT
    // ===================================================

    /**
     * Tính tiền thuế VAT dựa trên số tiền SAU KHI ĐÃ GIẢM.
     * (Thuế tính trên giá thực phải trả, không phải giá gốc)
     *
     * @param tienSauGiam Số tiền còn lại sau khi áp mã giảm
     * @param taxRate     Tỷ lệ thuế (VD: 0.10 = 10%)
     * @return Tiền thuế phải cộng thêm
     */
    public double tinhTienThue(double tienSauGiam, double taxRate) {
        return tienSauGiam * taxRate;
    }

    // ===================================================
    //  BƯỚC 4: TỔNG HỢP — TÍNH THÀNH TIỀN CUỐI CÙNG
    // ===================================================

    /**
     * Phương thức CHÍNH — tính toàn bộ và trả về kết quả thanh toán.
     * Đây là phương thức View gọi khi người dùng bấm "Thanh Toán".
     *
     * @param cartItems   Giỏ hàng hiện tại
     * @param maGiamGia   Mã giảm giá (để trống nếu không có)
     * @return Đối tượng PaymentResult chứa đủ thông tin để hiển thị
     */
    public PaymentResult tinhThanhToan(List<CartItem> cartItems, String maGiamGia) {
        // 1. Tính tổng gốc
        double tongGoc = tinhTongTienGoc(cartItems);

        // 2. Tính giảm giá
        double soTienGiam = tinhSoTienGiam(tongGoc, maGiamGia);
        double tienSauGiam = tongGoc - soTienGiam;

        // 3. Tính thuế (10% trên giá sau giảm)
        double tienThue = tinhTienThue(tienSauGiam, DEFAULT_TAX_RATE);

        // 4. Thành tiền cuối = sau giảm + thuế
        double thanhTienCuoi = tienSauGiam + tienThue;

        return new PaymentResult(tongGoc, soTienGiam, tienThue, thanhTienCuoi);
    }

    // ===================================================
    //  INNER CLASS: KẾT QUẢ THANH TOÁN
    // ===================================================

    /**
     * Đóng gói toàn bộ kết quả tính toán vào 1 đối tượng.
     * View chỉ cần nhận PaymentResult rồi hiển thị — không cần tính gì thêm.
     */
    public static class PaymentResult {
        private final double tongTienGoc;   // Trước giảm giá, trước thuế
        private final double soTienGiam;    // Số tiền được giảm
        private final double tienThue;      // Tiền VAT
        private final double thanhTienCuoi; // Số tiền cuối cùng phải trả

        public PaymentResult(double tongTienGoc, double soTienGiam,
                             double tienThue, double thanhTienCuoi) {
            this.tongTienGoc   = tongTienGoc;
            this.soTienGiam    = soTienGiam;
            this.tienThue      = tienThue;
            this.thanhTienCuoi = thanhTienCuoi;
        }

        public double getTongTienGoc()   { return tongTienGoc; }
        public double getSoTienGiam()    { return soTienGiam; }
        public double getTienThue()      { return tienThue; }
        public double getThanhTienCuoi() { return thanhTienCuoi; }

        /** In ra hóa đơn tóm tắt — dùng cho console test hoặc log */
        @Override
        public String toString() {
            return String.format(
                "╔══════════════════════════════════╗\n" +
                "║         HÓA ĐƠN THANH TOÁN       ║\n" +
                "╠══════════════════════════════════╣\n" +
                "║ Tổng tiền gốc :  %,15.0f VNĐ ║\n" +
                "║ Giảm giá      : -%,15.0f VNĐ ║\n" +
                "║ Thuế VAT (10%%):  %,15.0f VNĐ ║\n" +
                "╠══════════════════════════════════╣\n" +
                "║ THÀNH TIỀN    :  %,15.0f VNĐ ║\n" +
                "╚══════════════════════════════════╝",
                tongTienGoc, soTienGiam, tienThue, thanhTienCuoi
            );
        }
    }

    // ===================================================
    //  MAIN — TEST TOÀN BỘ LUỒNG THANH TOÁN (KHÔNG CẦN DB)
    // ===================================================
    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        // Giả lập giỏ hàng: 2 áo phông 150k + 1 quần 300k
        model.Product ao = new model.Product(1, "Áo Phông Basic", 150_000, 10);
        model.Product quan = new model.Product(2, "Quần Jogger", 300_000, 5);

        List<CartItem> cart = new java.util.ArrayList<>();
        cart.add(new CartItem(ao, 2));   // 150k × 2 = 300k
        cart.add(new CartItem(quan, 1)); // 300k × 1 = 300k
        // Tổng gốc = 600.000đ

        System.out.println("=== DANH SÁCH GIỎ HÀNG ===");
        for (CartItem item : cart) {
            System.out.println("  " + item);
        }

        System.out.println("\n=== TÍNH TOÁN THANH TOÁN ===");

        // Scenario 1: Có mã giảm giá SALE10 (giảm 10%)
        System.out.println("\n>> Kịch bản 1: Dùng mã SALE10");
        PaymentResult result1 = service.tinhThanhToan(cart, "SALE10");
        System.out.println(result1);

        // Scenario 2: Không có mã giảm giá
        System.out.println("\n>> Kịch bản 2: Không dùng mã giảm giá");
        PaymentResult result2 = service.tinhThanhToan(cart, "");
        System.out.println(result2);
    }
}
