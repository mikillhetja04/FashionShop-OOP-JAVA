package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.ProductDAO;
import model.Product;

public class ProductPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPrice, txtStock;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private ProductDAO productDAO = new ProductDAO();

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SẢN PHẨM", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Bảng hiển thị
        String[] columns = {"ID", "Tên Sản Phẩm", "Giá (VNĐ)", "Tồn Kho"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Khu vực nhập liệu & Nút bấm
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Tên SP:")); txtName = new JTextField(15); inputPanel.add(txtName);
        inputPanel.add(new JLabel("Giá:")); txtPrice = new JTextField(8); inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("Kho:")); txtStock = new JTextField(5); inputPanel.add(txtStock);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Thêm Mới");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm Mới");
        
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnRefresh);

        bottomPanel.add(inputPanel);
        bottomPanel.add(buttonPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        // Load dữ liệu ban đầu
        loadData();

        // 1. Nút Làm mới
        btnRefresh.addActionListener(e -> {
            clearFields();
            loadData();
        });

        // 2. Nút Thêm mới
        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText());
                int stock = Integer.parseInt(txtStock.getText());
                
                Product p = new Product(0, name, price, stock);
                if (productDAO.addProduct(p)) {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                    loadData();
                    clearFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!");
            }
        });

        // 3. Nút Xóa
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
                return;
            }
            int id = (int) table.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa SP ID: " + id + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                if (productDAO.deleteProduct(id)) {
                    loadData();
                    clearFields();
                }
            }
        });

        // 4. Nút Sửa
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa!");
                return;
            }
            try {
                int id = (int) table.getValueAt(row, 0);
                Product p = new Product(id, txtName.getText(), Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtStock.getText()));
                if (productDAO.updateProduct(p)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu!");
            }
        });

        // 5. Click vào bảng để hiện dữ liệu lên ô nhập
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                txtName.setText(table.getValueAt(row, 1).toString());
                txtPrice.setText(table.getValueAt(row, 2).toString());
                txtStock.setText(table.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Product> list = productDAO.getAllProducts();
        for (Product p : list) {
            tableModel.addRow(new Object[]{p.getProductId(), p.getProductName(), p.getPrice(), p.getStockQuantity()});
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        table.clearSelection();
    }
}