package view;

import javax.swing.*;
import java.awt.*;

/**
 * Cửa sổ chính của ứng dụng sau khi đăng nhập thành công.
 *
 * Phân quyền giao diện:
 *  - ADMIN   : 3 tab — Sản Phẩm (full CRUD), Đơn Hàng, Thống Kê Doanh Thu
 *  - CUSTOMER: 2 tab — Sản Phẩm (chỉ xem), Đơn Hàng
 */
public class MainForm extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainForm(int userId, String role, String username) {
        setTitle("FASHION SHOP  ·  " + username + "  [" + role + "]");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // Tab 1 — Quản lý sản phẩm (phân quyền trong ProductPanel)
        tabbedPane.addTab("📦 Sản Phẩm", new ProductPanel(role));

        // Tab 2 — Giỏ hàng & Thanh toán (truyền userId thật)
        tabbedPane.addTab("🛒 Đơn Hàng", new OrderPanel(userId));

        // Tab 3 — Thống kê doanh thu (chỉ Admin)
        if ("ADMIN".equalsIgnoreCase(role)) {
            tabbedPane.addTab("📊 Thống Kê Doanh Thu", new StatPanel());
        }

        // Thanh trạng thái
        JLabel statusBar = new JLabel(
            "  Đăng nhập: " + username + "  |  Quyền: " + role + "  |  ID: " + userId,
            JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setFont(new Font("Arial", Font.ITALIC, 11));
        statusBar.setForeground(Color.DARK_GRAY);

        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    /** Test nhanh — chạy trực tiếp bằng Admin giả lập */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainForm(1, "ADMIN", "TestAdmin").setVisible(true));
    }
}