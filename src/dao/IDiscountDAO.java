package dao;

import model.DiscountCode;

/**
 * Interface định nghĩa hợp đồng cho tầng truy cập dữ liệu mã giảm giá.
 * Thể hiện nguyên tắc Abstraction (Tính trừu tượng) trong OOP.
 */
public interface IDiscountDAO {
    DiscountCode findActiveCode(String code);
}
