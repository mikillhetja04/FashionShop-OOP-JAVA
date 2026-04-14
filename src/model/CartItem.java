package model;

/**
 * Đại diện cho 1 dòng sản phẩm trong giỏ hàng.
 * Chứa thông tin sản phẩm và số lượng người dùng chọn.
 */
public class CartItem {
    private Product product;
    private int quantity;

    // --- CONSTRUCTOR ---

    /** Constructor không tham số (bắt buộc cho một số trường hợp) */
    public CartItem() {}

    /** Constructor đầy đủ — dùng khi thêm sản phẩm vào giỏ */
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // --- GETTERS & SETTERS ---

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Cập nhật số lượng — không cho phép số âm hoặc bằng 0.
     * Nếu muốn xóa khỏi giỏ, hãy remove hẳn item ra khỏi List.
     */
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    // --- BUSINESS LOGIC ---

    /**
     * Tính thành tiền của 1 dòng trong giỏ hàng.
     * Ví dụ: Áo phông 150.000đ x 3 cái = 450.000đ
     * @return Đơn giá × Số lượng
     */
    public double getSubTotal() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("[%s] SL: %d | Thành tiền: %,.0f VNĐ",
                product.getProductName(), quantity, getSubTotal());
    }
}