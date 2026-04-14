package view;

import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    private JTabbedPane tabbedPane;

    public MainForm(String role, String username) {
        setTitle("HỆ THỐNG QUẢN LÝ FASHION SHOP - [User: " + username + "]");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Khởi tạo thanh Tab
        tabbedPane = new JTabbedPane();

        // 2. Thêm các Tab chức năng (Hiện tại mình tạo Panel trống, sau này lắp code vào)
        tabbedPane.addTab("📦 Quản lý Sản phẩm", createProductPanel());
        tabbedPane.addTab("🛒 Quản lý Đơn hàng", createOrderPanel());
        
        // Chỉ Admin mới thấy Tab Thống kê
        if (role.equalsIgnoreCase("ADMIN")) {
            tabbedPane.addTab("📊 Thống kê Doanh thu", createStatPanel());
        }

        add(tabbedPane);
    }

    // Giao diện Quản lý sản phẩm (Nơi sẽ hiện cái bảng)
    private JPanel createProductPanel() {
    	return new ProductPanel();
    }

    // Giao diện Đơn hàng
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Giao diện Hóa đơn & Giỏ hàng sẽ hiện ở đây", SwingConstants.CENTER));
        return panel;
    }

    // Giao diện Thống kê
    private JPanel createStatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Biểu đồ & Báo cáo doanh thu sẽ hiện ở đây", SwingConstants.CENTER));
        return panel;
    }

    public static void main(String[] args) {
        // Chạy thử với quyền Admin
        SwingUtilities.invokeLater(() -> new MainForm("ADMIN", "Hiep").setVisible(true));
    }
}