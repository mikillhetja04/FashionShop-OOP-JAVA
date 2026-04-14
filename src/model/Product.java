package model;

public class Product {
    private int productId;
    private int categoryId;
    private String productName;
    private double price;
    private String size;
    private String color;
    private int stockQuantity;

    // 1. Hàm khởi tạo KHÔNG tham số (BẮT BUỘC phải có cho ProductDAO)
    public Product() {
    }

    // 2. Hàm khởi tạo đầy đủ tham số (Dùng khi lấy dữ liệu từ DB)
    public Product(int productId, int categoryId, String productName, double price, String size, String color, int stockQuantity) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.price = price;
        this.size = size;
        this.color = color;
        this.stockQuantity = stockQuantity;
    }

    // 3. Hàm khởi tạo 4 tham số (Để khớp với file ProductPanel.java của bạn)
    public Product(int productId, String productName, double price, int stockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // --- GETTERS & SETTERS (Giữ nguyên như cũ của bạn) ---
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public String toString() {
        return String.format("Product[id=%d, name=%s, price=%,.0f VNĐ, stock=%d]",
                productId, productName, price, stockQuantity);
    }
}