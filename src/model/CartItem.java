package model;

public class CartItem {
    private Product product;
    private int quantity;

    // Constructor, Getters, Setters (Bạn tự Generate nhé)
    public double getSubTotal() {
        return product.getPrice() * quantity;
    }
}