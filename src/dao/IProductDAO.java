package dao;

import java.util.List;
import model.Product;

/**
 * Interface định nghĩa hợp đồng cho tầng truy cập dữ liệu sản phẩm.
 * Thể hiện nguyên tắc Abstraction (Tính trừu tượng) trong OOP.
 */
public interface IProductDAO {
    List<Product> getAllProducts();
    boolean addProduct(Product p);
    boolean updateProduct(Product p);
    boolean deleteProduct(int productId);
    List<Product> searchProductByName(String keyword);
}
