package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Map;
import dao.OrderDAO;

/**
 * Panel thống kê doanh thu theo tháng — chỉ dành cho Admin.
 *
 * Tính năng:
 *  - Chọn năm → tải dữ liệu doanh thu theo tháng
 *  - Hiển thị bảng: Tháng | Doanh Thu | Biểu Đồ cột (ASCII-style)
 *  - Hiển thị tổng doanh thu toàn hệ thống
 */
public class StatPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final OrderDAO orderDAO = new OrderDAO();

    private JSpinner       spnYear;
    private JButton        btnLoad;
    private JTable         tblRevenue;
    private DefaultTableModel revenueModel;
    private JLabel         lblTotalRevenue;
    private JLabel         lblRecordCount;

    public StatPanel() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("THỐNG KÊ DOANH THU", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(142, 68, 173));
        add(lblTitle, BorderLayout.NORTH);

        // Panel trên: chọn năm + nút tải
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        spnYear = new JSpinner(new SpinnerNumberModel(currentYear, 2000, currentYear + 5, 1));
        spnYear.setPreferredSize(new Dimension(80, 28));
        btnLoad = new JButton("📊 Tải Dữ Liệu");
        topPanel.add(new JLabel("Năm:"));
        topPanel.add(spnYear);
        topPanel.add(btnLoad);
        add(topPanel, BorderLayout.NORTH);

        // Bảng thống kê tháng
        String[] cols = {"Tháng", "Doanh Thu (VNĐ)", "Biểu Đồ"};
        revenueModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRevenue = new JTable(revenueModel);
        tblRevenue.setRowHeight(28);
        tblRevenue.setFont(new Font("Monospaced", Font.PLAIN, 13));
        tblRevenue.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tblRevenue.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRevenue.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblRevenue.getColumnModel().getColumn(2).setPreferredWidth(400);

        JScrollPane scrollPane = new JScrollPane(tblRevenue);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Doanh Thu Theo Tháng"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel dưới: tổng doanh thu
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Tổng Kết"));
        lblRecordCount  = new JLabel("Số tháng có doanh thu: —", JLabel.CENTER);
        lblTotalRevenue = new JLabel("TỔNG DOANH THU TOÀN HỆ THỐNG: —", JLabel.CENTER);
        lblTotalRevenue.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalRevenue.setForeground(new Color(192, 57, 43));
        lblRecordCount.setFont(new Font("Arial", Font.PLAIN, 13));
        bottomPanel.add(lblRecordCount);
        bottomPanel.add(lblTotalRevenue);
        add(bottomPanel, BorderLayout.SOUTH);

        // Sự kiện nút tải
        btnLoad.addActionListener(e -> loadData());
    }

    private void loadData() {
        int year = (int) spnYear.getValue();
        revenueModel.setRowCount(0);

        Map<Integer, Double> monthlyData = orderDAO.getMonthlyRevenue(year);
        double maxRevenue = monthlyData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);

        for (int month = 1; month <= 12; month++) {
            double revenue = monthlyData.getOrDefault(month, 0.0);
            String revenueStr = revenue > 0 ? String.format("%,.0f", revenue) : "—";
            String bar = buildBar(revenue, maxRevenue);
            revenueModel.addRow(new Object[]{
                "Tháng " + String.format("%2d", month),
                revenueStr,
                bar
            });
        }

        // Tổng doanh thu toàn hệ thống
        double total = orderDAO.getTotalRevenue();
        lblTotalRevenue.setText("TỔNG DOANH THU TOÀN HỆ THỐNG: " + String.format("%,.0f VNĐ", total));
        lblRecordCount.setText("Năm " + year + " — Số tháng có doanh thu: " + monthlyData.size() + "/12");
    }

    /** Tạo biểu đồ cột đơn giản bằng ký tự ▓ */
    private String buildBar(double value, double max) {
        if (value <= 0 || max <= 0) return "";
        int barLength = (int) Math.round((value / max) * 30);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < barLength; i++) sb.append('▓');
        return sb.toString();
    }
}
