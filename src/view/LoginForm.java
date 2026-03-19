package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import dao.UserDAO;
import model.User;

public class LoginForm extends JFrame {
	private static final long serialVersionUID = 1L;
    // Khai báo các thành phần giao diện
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginForm() {
        // 1. Cấu hình cửa sổ (Frame)
        setTitle("Fashion Shop - Đăng Nhập Hệ Thống");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        setLayout(new GridLayout(3, 1, 10, 10));

        // 2. Tạo các Panel chứa thành phần
        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(new JLabel("Tên đăng nhập: "));
        txtUsername = new JTextField(20);
        p1.add(txtUsername);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.add(new JLabel("Mật khẩu:          "));
        txtPassword = new JPasswordField(20);
        p2.add(txtPassword);

        JPanel p3 = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng Nhập");
        p3.add(btnLogin);

        // Thêm các Panel vào cửa sổ chính
        add(p1);
        add(p2);
        add(p3);

        // 3. Xử lý sự kiện khi bấm nút Đăng Nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });
    }

    private void xuLyDangNhap() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        // GỌI BACKEND CỦA BẠN (TV2) Ở ĐÂY!
        UserDAO dao = new UserDAO();
        User u = dao.checkLogin(user, pass);

        if (u != null) {
            JOptionPane.showMessageDialog(this, "Chào mừng " + u.getRole() + ": " + u.getUsername() + "\nĐăng nhập thành công!");
            // Sau này ở đây sẽ mở trang Admin hoặc Trang Chủ
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Chạy giao diện
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}