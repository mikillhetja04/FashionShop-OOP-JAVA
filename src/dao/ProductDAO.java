package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import DBpackage.DBConnection;
import model.Product;

/**
 * Tầng truy cập dữ liệu sản phẩm — implement IProductDAO.
 */
public class ProductDAO implements IProductDAO {

    /** Lấy toàn bộ danh sách sản phẩm */
    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY product_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh sách sản phẩm: " + e.getMessage());
        }
        return list;
    }

    /** Thêm sản phẩm mới */
    @Override
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (category_id, product_name, price, stock_quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getProductName());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQuantity());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Lỗi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }

    /** Cập nhật sản phẩm */
    @Override
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET category_id = ?, product_name = ?, price = ?, " +
                     "size = ?, color = ?, stock_quantity = ? WHERE product_id = ?";

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
            System.err.println("Lỗi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }

    /** Xóa sản phẩm theo ID */
    @Override
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Lỗi xóa sản phẩm (có thể đang được dùng trong đơn hàng): " + e.getMessage());
            return false;
        }
    }

    /** Tìm kiếm sản phẩm theo tên (LIKE %keyword%) */
    @Override
    public List<Product> searchProductByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE product_name LIKE ? ORDER BY product_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi tìm kiếm sản phẩm: " + e.getMessage());
        }
        return list;
    }

    /** Helper: map ResultSet → Product object */
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

    /** Test nhanh */
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        System.out.println("Tổng sản phẩm: " + dao.getAllProducts().size());
    }
}