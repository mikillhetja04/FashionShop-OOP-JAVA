package model;

public class OrderDetail {
    private int detailId;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;

    public OrderDetail() {}

    public OrderDetail(int productId, int quantity, double unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    /** Tính thành tiền của dòng này */
    public double getSubTotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return String.format("OrderDetail[productId=%d, qty=%d, unitPrice=%,.0f VNĐ, subtotal=%,.0f VNĐ]",
                productId, quantity, unitPrice, getSubTotal());
    }
}