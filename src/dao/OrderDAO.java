package dao;

import java.sql.*;
import java.util.List;
import DBpackage.DBConnection;
import model.Order;
import model.OrderDetail;

public class OrderDAO {
    
    public boolean createOrder(Order order, List<OrderDetail> details) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bật chế độ Giao dịch (Transaction)

            // 1. Lưu vào bảng orders
            String sqlOrder = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, 'PENDING')";
            PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getUserId());
            psOrder.setDouble(2, order.getTotalAmount());
            psOrder.executeUpdate();

            // Lấy ID hóa đơn vừa tạo
            ResultSet rs = psOrder.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // 2. Lưu danh sách chi tiết đơn hàng
            String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            
            for (OrderDetail d : details) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, d.getProductId());
                psDetail.setInt(3, d.getQuantity());
                psDetail.setDouble(4, d.getUnitPrice());
                psDetail.addBatch(); // Gom lại để chạy một lần cho nhanh
            }
            psDetail.executeBatch();

            conn.commit(); // Hoàn tất giao dịch
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {} // Lỗi thì hủy hết
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Hàm tính tổng doanh thu từ trước đến nay
     * @return Tổng số tiền thu được (double)
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE status = 'PAID'"; 
        // Chỉ tính những đơn đã thanh toán
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Hàm thống kê doanh thu theo từng tháng trong năm
     * @param year Năm muốn thống kê
     * @return Một danh sách chứa (Tháng - Doanh thu)
     */
    public void printMonthlyRevenue(int year) {
        String sql = "SELECT MONTH(order_date) as thang, SUM(total_amount) as doanh_thu " +
                     "FROM orders WHERE YEAR(order_date) = ? AND status = 'PAID' " +
                     "GROUP BY MONTH(order_date)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("--- THỐNG KÊ DOANH THU NĂM " + year + " ---");
            while (rs.next()) {
                int month = rs.getInt("thang");
                double revenue = rs.getDouble("doanh_thu");
                System.out.println("Tháng " + month + ": " + String.format("%,.0f", revenue) + " VNĐ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        
        // 1. Xem tổng tiền trong két sắt
        double total = dao.getTotalRevenue();
        System.out.println("💰 TỔNG DOANH THU HỆ THỐNG: " + String.format("%,.0f", total) + " VNĐ");
        
        // 2. Xem doanh thu chi tiết các tháng trong năm 2026
        dao.printMonthlyRevenue(2026);
    }
}