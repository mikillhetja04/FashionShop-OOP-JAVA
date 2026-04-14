package model;

public class Category {
    private int categoryId;
    private String categoryName;

    // Constructor không tham số
    public Category() {}

    // Constructor đầy đủ — dùng khi tạo đối tượng từ DB
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() { return categoryName; } // Tiện hiển thị trong JComboBox
}