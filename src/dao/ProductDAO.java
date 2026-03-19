package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DBpackage.DBConnection; // Nhập cây cầu của chúng ta vào
import model.Product;        // Nhập cái thùng chứa dữ liệu vào

public class ProductDAO {

    // Hàm lấy danh sách tất cả sản phẩm từ MySQL
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        // Câu lệnh SQL thân thuộc
        String sql = "SELECT * FROM products"; 

        try {
            // 1. Lấy kết nối
            Connection conn = DBConnection.getConnection();
            // 2. Chuẩn bị xe chở câu lệnh SQL
            PreparedStatement ps = conn.prepareStatement(sql);
            // 3. Chạy lệnh và nhận kết quả (ResultSet giống như một cái bảng excel trả về)
            ResultSet rs = ps.executeQuery();

            // 4. Duyệt từng dòng kết quả
            while (rs.next()) {
                Product p = new Product();
                // Đọc dữ liệu từ cột trong MySQL và nhét vào Object Java
                p.setProductId(rs.getInt("product_id"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setProductName(rs.getString("product_name"));
                p.setPrice(rs.getDouble("price"));
                p.setSize(rs.getString("size"));
                p.setColor(rs.getString("color"));
                p.setStockQuantity(rs.getInt("stock_quantity"));
                
                // Thêm sản phẩm vào danh sách
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * Hàm cập nhật thông tin sản phẩm đã có
     * @param p Đối tượng Product chứa thông tin mới (id phải khớp với id trong DB)
     * @return true nếu sửa thành công
     */
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET category_id = ?, product_name = ?, price = ?, "
                   + "size = ?, color = ?, stock_quantity = ? WHERE product_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getProductName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getSize());
            ps.setString(5, p.getColor());
            ps.setInt(6, p.getStockQuantity());
            ps.setInt(7, p.getProductId()); // Dùng ID để tìm đúng dòng cần sửa
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Hàm xóa sản phẩm theo ID
     * @param productId ID của sản phẩm cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Lưu ý: Nếu sản phẩm này đã có trong đơn hàng (OrderDetail), 
            // MySQL sẽ chặn không cho xóa để bảo vệ dữ liệu (Foreign Key Constraint).
            System.err.println("Không thể xóa sản phẩm này vì nó đang nằm trong hóa đơn!");
            return false;
        }
    }

    // Viết hàm main để tự test luôn cho nóng
//    public static void main(String[] args) {
//        ProductDAO dao = new ProductDAO();
//        List<Product> myProducts = dao.getAllProducts();
//        
//        System.out.println("--- DANH SÁCH SẢN PHẨM TRONG CỬA HÀNG ---");
//        for (Product p : myProducts) {
//            System.out.println("Tên: " + p.getProductName() 
//                             + " | Giá: " + p.getPrice() 
//                             + " | Size: " + p.getSize() 
//                             + " | Màu: " + p.getColor());
//        }
//    }
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();

        // 1. Test Sửa sản phẩm số 1 (Áo thun)
        Product pUpdate = new Product();
        pUpdate.setProductId(1); // ID sản phẩm muốn sửa
        pUpdate.setCategoryId(1);
        pUpdate.setProductName("Áo thun Cotton CAO CẤP"); // Đổi tên
        pUpdate.setPrice(199000.0); // Đổi giá
        pUpdate.setSize("XL");
        pUpdate.setColor("Xanh Navy");
        pUpdate.setStockQuantity(100);

        if (dao.updateProduct(pUpdate)) {
            System.out.println("✅ Đã cập nhật sản phẩm số 1 thành công!");
        }

        // 2. Test Xóa sản phẩm số 5
        if (dao.deleteProduct(5)) {
            System.out.println("🗑️ Đã xóa sản phẩm số 5 khỏi hệ thống!");
        } else {
            System.out.println("❌ Xóa thất bại (Sản phẩm không tồn tại hoặc dính hóa đơn).");
        }
    }
}