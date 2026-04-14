package dao;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DBpackage.DBConnection;
import model.Order;
import model.OrderDetail;

/**
 * Tầng truy cập dữ liệu đơn hàng — implement IOrderDAO.
 *
 * Cải tiến so với phiên bản cũ:
 * - Giảm tồn kho (stock_quantity) sau khi đặt hàng thành công
 * - Hỗ trợ cập nhật trạng thái đơn hàng → PAID
 */
public class OrderDAO implements IOrderDAO {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";

    /**
     * Tạo đơn hàng mới gồm 3 bước trong 1 transaction:
     * 1. INSERT vào bảng orders
     * 2. INSERT danh sách chi tiết vào order_details (batch)
     * 3. Giảm stock_quantity cho từng sản phẩm đã mua
     *
     * Nếu bất kỳ bước nào lỗi → ROLLBACK toàn bộ.
     */
    @Override
    public boolean createOrder(Order order, List<OrderDetail> details) {
        String sqlOrder = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                    PreparedStatement psStock = conn.prepareStatement(sqlUpdateStock)) {

                // Bước 1: Tạo đầu hóa đơn
                psOrder.setInt(1, order.getUserId());
                psOrder.setDouble(2, order.getTotalAmount());
                psOrder.setString(3, STATUS_PAID); // Thanh toán ngay → PAID
                psOrder.executeUpdate();

                int orderId = -1;
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next())
                        orderId = rs.getInt(1);
                }

                if (orderId == -1) {
                    conn.rollback();
                    return false;
                }

                // Bước 2 & 3: Thêm chi tiết + giảm tồn kho
                for (OrderDetail d : details) {
                    // Lưu chi tiết đơn hàng
                    psDetail.setInt(1, orderId);
                    psDetail.setInt(2, d.getProductId());
                    psDetail.setInt(3, d.getQuantity());
                    psDetail.setDouble(4, d.getUnitPrice());
                    psDetail.addBatch();

                    // Giảm tồn kho — kiểm tra đủ hàng trước khi giảm
                    psStock.setInt(1, d.getQuantity());
                    psStock.setInt(2, d.getProductId());
                    psStock.setInt(3, d.getQuantity()); // stock_quantity >= qty
                    int affected = psStock.executeUpdate();
                    if (affected == 0) {
                        // Không đủ hàng → rollback
                        conn.rollback();
                        System.err.println("Không đủ tồn kho cho sản phẩm ID: " + d.getProductId());
                        return false;
                    }
                }

                psDetail.executeBatch();
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Lỗi tạo đơn hàng, đang Rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tổng doanh thu từ các đơn hàng có trạng thái PAID.
     */
    @Override
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, STATUS_PAID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng doanh thu: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Doanh thu theo từng tháng trong năm.
     *
     * @param year Năm cần thống kê
     * @return LinkedHashMap<Tháng, DoanhThu> — đã sắp xếp theo tháng
     */
    @Override
    public Map<Integer, Double> getMonthlyRevenue(int year) {
        String sql = "SELECT MONTH(order_date) AS thang, SUM(total_amount) AS doanh_thu " +
                "FROM orders WHERE YEAR(order_date) = ? AND status = ? " +
                "GROUP BY MONTH(order_date) ORDER BY thang ASC";

        Map<Integer, Double> revenueMap = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setString(2, STATUS_PAID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    revenueMap.put(rs.getInt("thang"), rs.getDouble("doanh_thu"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê doanh thu theo tháng: " + e.getMessage());
        }
        return revenueMap;
    }

    /** Test console */
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        double total = dao.getTotalRevenue();
        System.out.println("💰 TỔNG DOANH THU: " + String.format("%,.0f", total) + " VNĐ");

        int year = 2026;
        Map<Integer, Double> monthly = dao.getMonthlyRevenue(year);
        System.out.println("\n--- DOANH THU NĂM " + year + " ---");
        if (monthly.isEmpty()) {
            System.out.println("Chưa có dữ liệu.");
        } else {
            monthly.forEach((month, revenue) -> System.out.printf("Tháng %2d: %,.0f VNĐ%n", month, revenue));
        }
    }
}