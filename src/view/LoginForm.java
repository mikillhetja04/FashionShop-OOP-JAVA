package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import dao.UserDAO;
import model.User;
import utils.DataValidator;

/**
 * Màn hình đăng nhập.
 *
 * Cải tiến:
 *  - Validate đầu vào dùng DataValidator
 *  - Truyền đủ thông tin (userId, role, username) sang MainForm
 *  - Hỗ trợ Enter ở ô mật khẩu để đăng nhập nhanh
 */
public class LoginForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;

    public LoginForm() {
        setTitle("Fashion Shop — Đăng Nhập Hệ Thống");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(4, 1, 8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Panel tên đăng nhập
        JPanel p1 = new JPanel(new BorderLayout(8, 0));
        p1.add(new JLabel("Tên đăng nhập:"), BorderLayout.WEST);
        txtUsername = new JTextField();
        p1.add(txtUsername, BorderLayout.CENTER);

        // Panel mật khẩu
        JPanel p2 = new JPanel(new BorderLayout(8, 0));
        p2.add(new JLabel("Mật khẩu:      "), BorderLayout.WEST);
        txtPassword = new JPasswordField();
        p2.add(txtPassword, BorderLayout.CENTER);

        // Nút đăng nhập
        JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("    Đăng Nhập    ");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        p3.add(btnLogin);

        // Nhãn gợi ý
        JLabel lblHint = new JLabel("© Fashion Shop Management System", JLabel.CENTER);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);

        add(p1); add(p2); add(p3); add(lblHint);

        // Sự kiện: nút bấm + Enter ở ô mật khẩu
        ActionListener loginAction = e -> xuLyDangNhap();
        btnLogin.addActionListener(loginAction);
        txtPassword.addActionListener(loginAction); // Enter trong ô mật khẩu
    }

    private void xuLyDangNhap() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validate đầu vào bằng DataValidator
        if (!DataValidator.isNotBlank(username)) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (!DataValidator.isNotBlank(password)) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.checkLogin(username, password);

        if (user != null) {
            this.dispose();
            // Truyền đủ userId, role, username sang MainForm
            SwingUtilities.invokeLater(() ->
                new MainForm(user.getUserId(), user.getRole(), user.getUsername()).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this,
                "Sai tài khoản hoặc mật khẩu! Vui lòng thử lại.",
                "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}