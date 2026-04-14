package model;

/**
 * Model đại diện cho 1 mã giảm giá trong hệ thống.
 * Tương ứng với bảng `discount_codes` trong Database.
 *
 * SQL tạo bảng:
 * CREATE TABLE discount_codes (
 *     code         VARCHAR(20)    NOT NULL PRIMARY KEY,
 *     description  VARCHAR(100),
 *     discount_percent INT        NOT NULL DEFAULT 0,   -- Phần trăm giảm (0-100)
 *     is_active    BOOLEAN        NOT NULL DEFAULT TRUE
 * );
 *
 * Dữ liệu mẫu:
 * INSERT INTO discount_codes VALUES ('SALE10', 'Giảm 10% mùa hè', 10, TRUE);
 * INSERT INTO discount_codes VALUES ('VIP20',  'Ưu đãi VIP 20%',  20, TRUE);
 * INSERT INTO discount_codes VALUES ('SUMMER50','Sale hè 50%',    50, TRUE);
 */
public class DiscountCode {
    private String code;           // Mã code (VD: "SALE10")
    private String description;    // Mô tả ngắn
    private int discountPercent;   // % giảm giá (VD: 10 = giảm 10%)
    private boolean active;        // Mã còn hiệu lực hay không

    // --- CONSTRUCTOR ---

    public DiscountCode() {}

    public DiscountCode(String code, String description, int discountPercent, boolean active) {
        this.code = code;
        this.description = description;
        this.discountPercent = discountPercent;
        this.active = active;
    }

    // --- GETTERS & SETTERS ---

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("Mã: [%s] | Giảm: %d%% | %s | Hiệu lực: %s",
                code, discountPercent, description, active ? "CÒN" : "HẾT HẠN");
    }
}
