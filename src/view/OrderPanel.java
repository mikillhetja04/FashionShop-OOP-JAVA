package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import dao.OrderDAO;
import dao.ProductDAO;
import model.CartItem;
import model.Order;
import model.OrderDetail;
import model.Product;
import service.PaymentService;
import service.PaymentService.PaymentResult;

/**
 * Panel giỏ hàng & thanh toán.
 *
 * Luồng sử dụng:
 *  1. Chọn sản phẩm từ dropdown → nhập số lượng → Thêm vào giỏ
 *  2. Nhập mã giảm giá (nếu có) → Áp dụng
 *  3. Xem hóa đơn tóm tắt → Bấm Thanh Toán
 */
public class OrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Giỏ hàng đang hoạt động
    private final List<CartItem> cart = new ArrayList<>();
    private final int userId;

    // DAO & Service
    private final ProductDAO    productDAO    = new ProductDAO();
    private final OrderDAO      orderDAO      = new OrderDAO();
    private final PaymentService paymentService = new PaymentService();

    // UI — Chọn sản phẩm
    private JComboBox<Product> cboProduct;
    private JSpinner           spnQuantity;
    private JButton            btnAddToCart;

    // UI — Giỏ hàng
    private JTable             tblCart;
    private DefaultTableModel  cartModel;
    private JButton            btnRemoveItem;

    // UI — Thanh toán
    private JTextField         txtDiscount;
    private JButton            btnApplyDiscount;
    private JLabel             lblTongGoc, lblGiamGia, lblThue, lblThanhTien;
    private JButton            btnCheckout;
    private JLabel             lblDiscountStatus;

    // Trạng thái giảm giá
    private String appliedDiscountCode = "";

    public OrderPanel(int userId) {
        this.userId = userId;
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("GIỎ HÀNG & THANH TOÁN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(39, 174, 96));
        add(lblTitle, BorderLayout.NORTH);

        // === TRÁI: Chọn sản phẩm + bảng giỏ hàng ===
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Thanh chọn sản phẩm
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.setBorder(BorderFactory.createTitledBorder("Chọn Sản Phẩm"));
        cboProduct   = new JComboBox<>();
        spnQuantity  = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        btnAddToCart = new JButton("➕ Thêm vào giỏ");
        selectPanel.add(new JLabel("Sản phẩm:"));
        selectPanel.add(cboProduct);
        selectPanel.add(new JLabel("Số lượng:"));
        selectPanel.add(spnQuantity);
        selectPanel.add(btnAddToCart);
        leftPanel.add(selectPanel, BorderLayout.NORTH);

        // Bảng giỏ hàng
        String[] cartCols = {"Sản Phẩm", "Đơn Giá (VNĐ)", "Số Lượng", "Thành Tiền (VNĐ)"};
        cartModel = new DefaultTableModel(cartCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(24);
        tblCart.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        JScrollPane scrollCart = new JScrollPane(tblCart);
        scrollCart.setBorder(BorderFactory.createTitledBorder("Giỏ Hàng"));
        leftPanel.add(scrollCart, BorderLayout.CENTER);

        btnRemoveItem = new JButton("🗑️ Xóa dòng đã chọn");
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removePanel.add(btnRemoveItem);
        leftPanel.add(removePanel, BorderLayout.SOUTH);

        // === PHẢI: Thanh toán ===
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Thanh Toán"));
        rightPanel.setPreferredSize(new Dimension(280, 0));

        // Mã giảm giá
        JPanel discountPanel = new JPanel(new GridLayout(3, 1, 3, 3));
        discountPanel.setBorder(BorderFactory.createTitledBorder("Mã Giảm Giá"));
        txtDiscount      = new JTextField();
        btnApplyDiscount = new JButton("Áp Dụng");
        lblDiscountStatus = new JLabel(" ", JLabel.CENTER);
        lblDiscountStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        discountPanel.add(txtDiscount);
        discountPanel.add(btnApplyDiscount);
        discountPanel.add(lblDiscountStatus);
        rightPanel.add(discountPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Bảng tóm tắt tiền
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 5, 8));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Hóa Đơn"));
        Font boldFont = new Font("Arial", Font.BOLD, 13);
        Font valueFont = new Font("Monospaced", Font.PLAIN, 13);

        lblTongGoc   = new JLabel("0 VNĐ", JLabel.RIGHT);
        lblGiamGia   = new JLabel("0 VNĐ", JLabel.RIGHT);
        lblThue      = new JLabel("0 VNĐ", JLabel.RIGHT);
        lblThanhTien = new JLabel("0 VNĐ", JLabel.RIGHT);
        lblThanhTien.setFont(new Font("Arial", Font.BOLD, 14));
        lblThanhTien.setForeground(new Color(192, 57, 43));

        lblTongGoc.setFont(valueFont); lblGiamGia.setFont(valueFont); lblThue.setFont(valueFont);

        JLabel l1 = new JLabel("Tổng tiền gốc:"); l1.setFont(boldFont);
        JLabel l2 = new JLabel("Giảm giá:");      l2.setFont(boldFont);
        JLabel l3 = new JLabel("Thuế VAT (10%):");l3.setFont(boldFont);
        JLabel l4 = new JLabel("THÀNH TIỀN:");    l4.setFont(boldFont);

        summaryPanel.add(l1); summaryPanel.add(lblTongGoc);
        summaryPanel.add(l2); summaryPanel.add(lblGiamGia);
        summaryPanel.add(l3); summaryPanel.add(lblThue);
        summaryPanel.add(l4); summaryPanel.add(lblThanhTien);
        rightPanel.add(summaryPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Nút thanh toán
        btnCheckout = new JButton("💳 THANH TOÁN");
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 16));
        btnCheckout.setBackground(new Color(39, 174, 96));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rightPanel.add(btnCheckout);

        // Ghép 2 Panel chính
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.75);
        add(splitPane, BorderLayout.CENTER);

        // --- Gắn sự kiện ---
        btnAddToCart.addActionListener(e -> addToCart());
        btnRemoveItem.addActionListener(e -> removeFromCart());
        btnApplyDiscount.addActionListener(e -> applyDiscount());
        txtDiscount.addActionListener(e -> applyDiscount());
        btnCheckout.addActionListener(e -> checkout());
    }

    /** Load danh sách sản phẩm vào ComboBox */
    private void loadProducts() {
        cboProduct.removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            cboProduct.addItem(p);
        }
        // Hiển thị dạng "Tên SP - Giá - Còn: X"
        cboProduct.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    Product p = (Product) value;
                    setText(String.format("%s — %,.0f VNĐ (Tồn: %d)", p.getProductName(), p.getPrice(), p.getStockQuantity()));
                }
                return this;
            }
        });
    }

    /** Thêm sản phẩm vào giỏ hàng */
    private void addToCart() {
        Product selected = (Product) cboProduct.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!"); return; }

        int qty = (int) spnQuantity.getValue();
        if (qty > selected.getStockQuantity()) {
            JOptionPane.showMessageDialog(this,
                "Số lượng vượt quá tồn kho! Còn lại: " + selected.getStockQuantity(),
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra nếu đã có trong giỏ → cộng thêm số lượng
        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == selected.getProductId()) {
                int newQty = item.getQuantity() + qty;
                if (newQty > selected.getStockQuantity()) {
                    JOptionPane.showMessageDialog(this, "Vượt tồn kho! Tối đa: " + selected.getStockQuantity());
                    return;
                }
                item.setQuantity(newQty);
                refreshCartTable();
                updatePaymentSummary();
                return;
            }
        }

        cart.add(new CartItem(selected, qty));
        refreshCartTable();
        updatePaymentSummary();
        spnQuantity.setValue(1);
    }

    /** Xóa dòng đã chọn khỏi giỏ */
    private void removeFromCart() {
        int row = tblCart.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần xóa khỏi giỏ!"); return; }
        cart.remove(row);
        refreshCartTable();
        updatePaymentSummary();
    }

    /** Áp dụng mã giảm giá */
    private void applyDiscount() {
        String code = txtDiscount.getText().trim();
        if (code.isEmpty()) {
            appliedDiscountCode = "";
            lblDiscountStatus.setText(" ");
            lblDiscountStatus.setForeground(Color.GRAY);
            updatePaymentSummary();
            return;
        }

        var dc = paymentService.timMaGiamGia(code);
        if (dc != null) {
            appliedDiscountCode = code;
            lblDiscountStatus.setText("✅ Áp dụng: " + dc.getDescription() + " (-" + dc.getDiscountPercent() + "%)");
            lblDiscountStatus.setForeground(new Color(39, 174, 96));
        } else {
            appliedDiscountCode = "";
            lblDiscountStatus.setText("❌ Mã không hợp lệ hoặc đã hết hạn!");
            lblDiscountStatus.setForeground(Color.RED);
        }
        updatePaymentSummary();
    }

    /** Cập nhật lại bảng hiển thị giỏ hàng */
    private void refreshCartTable() {
        cartModel.setRowCount(0);
        for (CartItem item : cart) {
            cartModel.addRow(new Object[]{
                item.getProduct().getProductName(),
                String.format("%,.0f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("%,.0f", item.getSubTotal())
            });
        }
    }

    /** Cập nhật bảng tóm tắt tiền bên phải */
    private void updatePaymentSummary() {
        PaymentResult result = paymentService.tinhThanhToan(cart, appliedDiscountCode);
        lblTongGoc.setText(String.format("%,.0f VNĐ", result.getTongTienGoc()));
        lblGiamGia.setText(String.format("-%,.0f VNĐ", result.getSoTienGiam()));
        lblGiamGia.setForeground(result.hasDiscount() ? new Color(192, 57, 43) : Color.BLACK);
        lblThue.setText(String.format("%,.0f VNĐ", result.getTienThue()));
        lblThanhTien.setText(String.format("%,.0f VNĐ", result.getThanhTienCuoi()));
    }

    /** Xử lý thanh toán */
    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentResult result = paymentService.tinhThanhToan(cart, appliedDiscountCode);

        // Hiện hộp thoại xác nhận với thông tin hóa đơn
        String message = "XÁC NHẬN THANH TOÁN\n\n" + result.toString() + "\n\nXác nhận thanh toán?";
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác Nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Tạo đối tượng Order
        Order order = new Order(userId, result.getThanhTienCuoi());

        // Chuyển CartItem → OrderDetail
        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cart) {
            details.add(new OrderDetail(
                item.getProduct().getProductId(),
                item.getQuantity(),
                item.getProduct().getPrice()
            ));
        }

        // Gọi DAO tạo đơn (tự động giảm tồn kho trong transaction)
        boolean success = orderDAO.createOrder(order, details);
        if (success) {
            JOptionPane.showMessageDialog(this,
                "✅ THANH TOÁN THÀNH CÔNG!\nCảm ơn bạn đã mua hàng!",
                "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            appliedDiscountCode = "";
            txtDiscount.setText("");
            lblDiscountStatus.setText(" ");
            refreshCartTable();
            updatePaymentSummary();
            loadProducts(); // Cập nhật lại tồn kho trên ComboBox
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Thanh toán thất bại!\nCó thể do sản phẩm không đủ hàng.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
