package dao;

import java.util.List;
import java.util.Map;
import model.Order;
import model.OrderDetail;

/**
 * Interface định nghĩa hợp đồng cho tầng truy cập dữ liệu đơn hàng.
 * Thể hiện nguyên tắc Abstraction (Tính trừu tượng) trong OOP.
 */
public interface IOrderDAO {
    boolean createOrder(Order order, List<OrderDetail> details);
    double getTotalRevenue();
    Map<Integer, Double> getMonthlyRevenue(int year);
}
