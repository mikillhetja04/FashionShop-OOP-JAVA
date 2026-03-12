package model;

import java.sql.Timestamp; // Bắt buộc phải import cái này cho ngày tháng

public class Order {
    private int orderId;
    private int userId;
    private Timestamp orderDate; // Lưu ngày giờ đặt hàng
    private double totalAmount;  // Tổng tiền phải là double
    private String status;       // Trạng thái là chữ (String)
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Timestamp getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}