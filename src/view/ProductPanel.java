package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import dao.ProductDAO;
import model.Product;
import utils.DataValidator;

/**
 * Panel quản lý sản phẩm — hỗ trợ phân quyền theo role.
 *
 * Cải tiến:
 *  - Thêm ô tìm kiếm → gọi ProductDAO.searchProductByName()
 *  - Validate đầu vào (tên không rỗng, giá > 0, tồn kho >= 0)
 *  - Ẩn nút CRUD nếu role là CUSTOMER
 */
public class ProductPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtPrice, txtStock, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnSearch;
    private ProductDAO productDAO = new ProductDAO();
    private final boolean isAdmin;

    /** Constructor mặc định — dùng khi chưa có thông tin role (ADMIN mode) */
    public ProductPanel() {
        this("ADMIN");
    }

    /** Constructor có role — phân quyền giao diện */
    public ProductPanel(String role) {
        this.isAdmin = "ADMIN".equalsIgnoreCase(role);
        initUI();
        loadData();
        attachListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Tiêu đề ---
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SẢN PHẨM", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        add(lblTitle, BorderLayout.NORTH);

        // --- Bảng hiển thị ---
        String[] columns = {"ID", "Tên Sản Phẩm", "Giá (VNĐ)", "Tồn Kho"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; } // Không cho sửa trực tiếp
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Khu vực phía dưới ---
        JPanel bottomPanel = new JPanel(new GridLayout(isAdmin ? 3 : 1, 1, 5, 5));

        // Thanh tìm kiếm (hiện với tất cả)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("🔍 Tìm kiếm:"));
        txtSearch = new JTextField(20);
        btnSearch  = new JButton("Tìm");
        btnRefresh = new JButton("Làm Mới");
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        bottomPanel.add(searchPanel);

        if (isAdmin) {
            // Ô nhập liệu — chỉ Admin
            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            inputPanel.add(new JLabel("Tên SP:"));
            txtName = new JTextField(15); inputPanel.add(txtName);
            inputPanel.add(new JLabel("Giá (VNĐ):"));
            txtPrice = new JTextField(10); inputPanel.add(txtPrice);
            inputPanel.add(new JLabel("Tồn kho:"));
            txtStock = new JTextField(6); inputPanel.add(txtStock);
            bottomPanel.add(inputPanel);

            // Nút bấm — chỉ Admin
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnAdd    = new JButton("➕ Thêm Mới");
            btnUpdate = new JButton("✏️ Cập Nhật");
            btnDelete = new JButton("🗑️ Xóa");
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnDelete);
            bottomPanel.add(buttonPanel);
        }

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        // Nút Tìm kiếm
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            loadData(keyword.isEmpty() ? null : keyword);
        });
        // Enter trong ô tìm kiếm
        txtSearch.addActionListener(e -> btnSearch.doClick());

        // Nút Làm mới
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            if (isAdmin) clearFields();
            loadData();
        });

        if (!isAdmin) return; // CUSTOMER không có các nút CRUD

        // Nút Thêm mới
        btnAdd.addActionListener(e -> {
            if (!validateInputs()) return;
            Product p = new Product(0, txtName.getText().trim(),
                    Double.parseDouble(txtPrice.getText().trim()),
                    Integer.parseInt(txtStock.getText().trim()));
            if (productDAO.addProduct(p)) {
                JOptionPane.showMessageDialog(this, "✅ Thêm sản phẩm thành công!");
                loadData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Thêm thất bại! Kiểm tra lại dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Xóa
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!"); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa sản phẩm ID: " + id + "?\n(Không thể xóa nếu sản phẩm đã có trong đơn hàng)",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (productDAO.deleteProduct(id)) {
                    JOptionPane.showMessageDialog(this, "✅ Đã xóa sản phẩm ID: " + id);
                    loadData(); clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Không thể xóa! Sản phẩm đang được dùng trong đơn hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Nút Cập nhật
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!"); return; }
            if (!validateInputs()) return;
            int id = (int) tableModel.getValueAt(row, 0);
            Product p = new Product(id, txtName.getText().trim(),
                    Double.parseDouble(txtPrice.getText().trim()),
                    Integer.parseInt(txtStock.getText().trim()));
            if (productDAO.updateProduct(p)) {
                JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Click vào bảng → điền dữ liệu vào ô nhập
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row != -1) {
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                txtStock.setText(tableModel.getValueAt(row, 3).toString());
            }
        });
    }

    /** Validate các ô nhập liệu trước khi gọi DAO */
    private boolean validateInputs() {
        if (!DataValidator.isNotBlank(txtName.getText())) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus(); return false;
        }
        if (!DataValidator.isPositiveDouble(txtPrice.getText())) {
            JOptionPane.showMessageDialog(this, "Giá phải là số dương (> 0)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtPrice.requestFocus(); return false;
        }
        if (!DataValidator.isNonNegativeInt(txtStock.getText())) {
            JOptionPane.showMessageDialog(this, "Tồn kho phải là số nguyên >= 0!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtStock.requestFocus(); return false;
        }
        return true;
    }

    /** Load toàn bộ hoặc theo từ khóa tìm kiếm */
    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<Product> list = (keyword == null)
                ? productDAO.getAllProducts()
                : productDAO.searchProductByName(keyword);
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                p.getProductId(), p.getProductName(),
                String.format("%,.0f", p.getPrice()), p.getStockQuantity()
            });
        }
    }

    private void loadData() { loadData(null); }

    private void clearFields() {
        if (!isAdmin) return;
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        table.clearSelection();
    }
}