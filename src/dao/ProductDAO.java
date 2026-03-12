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

    // Viết hàm main để tự test luôn cho nóng
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        List<Product> myProducts = dao.getAllProducts();
        
        System.out.println("--- DANH SÁCH SẢN PHẨM TRONG CỬA HÀNG ---");
        for (Product p : myProducts) {
            System.out.println("Tên: " + p.getProductName() 
                             + " | Giá: " + p.getPrice() 
                             + " | Size: " + p.getSize() 
                             + " | Màu: " + p.getColor());
        }
    }
}