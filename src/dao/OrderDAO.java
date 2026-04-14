package dao;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DBpackage.DBConnection;
import model.Order;
import model.OrderDetail;

public class OrderDAO {
    
    // Khai báo hằng số trạng thái để dễ quản lý và tái sử dụng
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";

    public boolean createOrder(Order order, List<OrderDetail> details) {
        String sqlOrder = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        // Sử dụng try-with-resources để tự động đóng Connection (chống rò rỉ kết nối)
        try (Connection conn = DBConnection.getConnection()) {
            // Tắt auto commit để quản lý transaction đồng bộ
            conn.setAutoCommit(false); 
            
            // Mở trước 2 PrepareStatement
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
                
                // 1. Lưu vào bảng orders
                psOrder.setInt(1, order.getUserId());
                psOrder.setDouble(2, order.getTotalAmount());
                psOrder.setString(3, STATUS_PENDING);
                psOrder.executeUpdate();

                // Lấy ID hóa đơn vừa tạo
                int orderId = -1;
                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }
                
                if (orderId == -1) {
                    conn.rollback();
                    return false;
                }

                // 2. Lưu danh sách chi tiết đơn hàng
                for (OrderDetail d : details) {
                    psDetail.setInt(1, orderId);
                    psDetail.setInt(2, d.getProductId());
                    psDetail.setInt(3, d.getQuantity());
                    psDetail.setDouble(4, d.getUnitPrice());
                    psDetail.addBatch(); // Gom lại để chạy batch tăng tốc độ cho loop
                }
                psDetail.executeBatch();

                // Hoàn tất giao dịch nếu toàn bộ phía trên trơn tru
                conn.commit(); 
                return true;
                
            } catch (SQLException e) {
                conn.rollback(); // Lỗi ở chi tiết mã thì hủy luôn cả đơn hàng đã insert để vẹn toàn Data
                System.err.println("Giao dịch tạo đơn hàng lỗi, đang tiến hành Rollback: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hàm tính tổng doanh thu từ trước đến nay
     * @return Tổng số tiền thu được (double)
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE status = ?"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, STATUS_PAID); 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng doanh thu: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Hàm thống kê doanh thu theo từng tháng trong năm (Đã Refactor SRP)
     * @param year Năm muốn thống kê
     * @return Một Map chứa (Tháng -> Doanh thu)
     */
    public Map<Integer, Double> getMonthlyRevenue(int year) {
        String sql = "SELECT MONTH(order_date) as thang, SUM(total_amount) as doanh_thu " +
                     "FROM orders WHERE YEAR(order_date) = ? AND status = ? " +
                     "GROUP BY MONTH(order_date) ORDER BY thang ASC";
                     
        // Sử dụng LinkedHashMap để duy trì thứ tự tháng chèn vào từ Query (Sắp xếp theo tháng tăng dần)
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
            System.err.println("Lỗi trích xuất doanh thu theo tháng: " + e.getMessage());
        }
        return revenueMap;
    }
    
    // Main Method đóng vai trò như một Console Client (View) giả lập
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        
        // 1. Xem tổng tiền trong két sắt
        double total = dao.getTotalRevenue();
        System.out.println("💰 TỔNG DOANH THU HỆ THỐNG: " + String.format("%,.0f", total) + " VNĐ");
        
        // 2. Nhận Dataset Data Layer đẩy về (Không in trực tiếp trong DAO nữa)
        int yearToAnalyze = 2026;
        Map<Integer, Double> monthlyData = dao.getMonthlyRevenue(yearToAnalyze);
        
        // View tự quyết định format hiển thị
        System.out.println("\n--- THỐNG KÊ DOANH THU NĂM " + yearToAnalyze + " ---");
        if (monthlyData.isEmpty()) {
            System.out.println("Chưa có dữ liệu doanh thu cho năm " + yearToAnalyze + ".");
        } else {
            for (Map.Entry<Integer, Double> entry : monthlyData.entrySet()) {
                System.out.println("Tháng " + entry.getKey() + ": " + String.format("%,.0f", entry.getValue()) + " VNĐ");
            }
        }
    }
}