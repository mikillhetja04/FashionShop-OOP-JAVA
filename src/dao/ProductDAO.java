package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DBpackage.DBConnection; 
import model.Product;

public class ProductDAO {

    // 1. Hàm Lấy toàn bộ danh sách sản phẩm
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Hàm Thêm mới sản phẩm
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (category_id, product_name, price, stock_quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBpackage.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Dùng getCategoryId() từ đối tượng — không ép cứng nữa
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getProductName());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQuantity());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("❌ Lỗi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // 3. Hàm Cập nhật sản phẩm
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET category_id = ?, product_name = ?, price = ?, size = ?, color = ?, stock_quantity = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getProductName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getSize());
            ps.setString(5, p.getColor());
            ps.setInt(6, p.getStockQuantity());
            ps.setInt(7, p.getProductId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Hàm Xóa sản phẩm
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi: Không thể xóa sản phẩm do dính ràng buộc hóa đơn!");
            return false;
        }
    }

    // 5. Hàm Tìm kiếm theo tên
    public List<Product> searchProductByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE product_name LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm phụ để tránh lặp code (Helper Method)
    private Product mapResultSetToProduct(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setProductName(rs.getString("product_name"));
        p.setPrice(rs.getDouble("price"));
        p.setSize(rs.getString("size"));
        p.setColor(rs.getString("color"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        return p;
    }

    // Hàm main để test nhanh
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        System.out.println("Kiểm tra kết nối và lấy dữ liệu: " + dao.getAllProducts().size() + " sản phẩm.");
    }
}