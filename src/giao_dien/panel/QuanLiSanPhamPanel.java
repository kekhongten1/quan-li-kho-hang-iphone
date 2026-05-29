package giao_dien.panel;

import dao.SanPhamDAO;
import model.NguoiDung;
import model.SanPham;
import util.GiaoDienUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class QuanLiSanPhamPanel extends JPanel implements LamMoiDuLieu {
    private static final String CHE_DO_THEO_TON_KHO = "Theo tồn kho";

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final DefaultTableModel modelBang = new DefaultTableModel();
    private final boolean coQuyenQuanLy;

    private ArrayList<SanPham> danhSachSanPham = new ArrayList<>();

    private JTextField txtTimKiem;
    private JTextField txtMa, txtTen, txtDongMay, txtMauSac, txtDungLuong;
    private JTextField txtGiaNhap, txtGiaBan, txtSoLuongTon;
    private JComboBox<String> cboTrangThai;
    private JLabel lblTrangThaiTuDong;
    private JTable table;

    public QuanLiSanPhamPanel(NguoiDung nguoiDungDangNhap) {
        this.coQuyenQuanLy = nguoiDungDangNhap.laQuanLy();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(GiaoDienUtil.MAU_NEN_CHAN);

        add(taoTieuDe(), BorderLayout.NORTH);
        add(taoNoiDungTrungTam(), BorderLayout.CENTER);
        add(taoPanelNut(), BorderLayout.SOUTH);
    }

    private JPanel taoTieuDe() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Quản lý sản phẩm");
        lblTieuDe.setFont(GiaoDienUtil.FONT_TIEU_DE);
        lblTieuDe.setForeground(new Color(15, 23, 42));
        panel.add(lblTieuDe, BorderLayout.WEST);

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTimKiem.setOpaque(false);
        txtTimKiem = new JTextField(22);
        JButton btnTimKiem = GiaoDienUtil.taoNut("🔍  Tìm kiếm", GiaoDienUtil.MAU_CHINH);
        JButton btnTaiLai  = GiaoDienUtil.taoNut("↻  Tải lại",   GiaoDienUtil.MAU_XAM);

        btnTimKiem.addActionListener(e -> timKiemSanPham());
        btnTaiLai.addActionListener(e -> lamMoiDuLieu());

        panelTimKiem.add(new JLabel("Từ khóa:"));
        panelTimKiem.add(txtTimKiem);
        panelTimKiem.add(btnTimKiem);
        panelTimKiem.add(btnTaiLai);
        panel.add(panelTimKiem, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoNoiDungTrungTam() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.add(taoFormThongTin(), BorderLayout.NORTH);

        modelBang.setColumnIdentifiers(new String[]{
                "Mã SP", "Tên sản phẩm", "Dòng máy", "Màu sắc", "Dung lượng",
                "Giá nhập", "Giá bán", "Tồn kho", "Trạng thái"
        });

        table = new JTable(modelBang);
        GiaoDienUtil.caiThienBang(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> hienThiLenForm());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoFormThongTin() {
        JPanel panelWrapper = new JPanel(new BorderLayout(0, 8));
        panelWrapper.setOpaque(false);

        JPanel panel = new JPanel(new GridLayout(5, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GiaoDienUtil.MAU_VIEN),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(4, 8, 4, 8),
                        "Thông tin sản phẩm iPhone")));

        txtMa         = new JTextField();
        txtTen        = new JTextField();
        txtDongMay    = new JTextField();
        txtMauSac     = new JTextField();
        txtDungLuong  = new JTextField();
        txtGiaNhap    = new JTextField();
        txtGiaBan     = new JTextField();
        txtSoLuongTon = new JTextField();
        cboTrangThai  = new JComboBox<>(new String[]{CHE_DO_THEO_TON_KHO, SanPhamDAO.TRANG_THAI_TAM_NGUNG});
        lblTrangThaiTuDong = new JLabel(SanPhamDAO.TRANG_THAI_DANG_KINH_DOANH);
        lblTrangThaiTuDong.setFont(GiaoDienUtil.FONT_NHAN);

        txtSoLuongTon.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { capNhatTrangThaiTuDongTrenForm(); }
            public void removeUpdate(DocumentEvent e)  { capNhatTrangThaiTuDongTrenForm(); }
            public void changedUpdate(DocumentEvent e) { capNhatTrangThaiTuDongTrenForm(); }
        });

        panel.add(new JLabel("Mã sản phẩm"));    panel.add(txtMa);
        panel.add(new JLabel("Tên sản phẩm"));   panel.add(txtTen);
        panel.add(new JLabel("Dòng máy"));        panel.add(txtDongMay);
        panel.add(new JLabel("Màu sắc"));         panel.add(txtMauSac);
        panel.add(new JLabel("Dung lượng"));      panel.add(txtDungLuong);
        panel.add(new JLabel("Giá nhập"));        panel.add(txtGiaNhap);
        panel.add(new JLabel("Giá bán"));         panel.add(txtGiaBan);
        panel.add(new JLabel("Số lượng tồn"));    panel.add(txtSoLuongTon);
        panel.add(new JLabel("Chế độ trạng thái")); panel.add(cboTrangThai);
        panel.add(new JLabel("Trạng thái tự động")); panel.add(lblTrangThaiTuDong);

        panelWrapper.add(panel, BorderLayout.CENTER);

        JLabel lblGhiChu = new JLabel(coQuyenQuanLy
                ? "ℹ  Trạng thái tồn kho được tự động cập nhật. Chỉ \"Tạm ngừng\" là thao tác thủ công."
                : "⚠  Tài khoản nhân viên chỉ được xem danh sách sản phẩm.");
        lblGhiChu.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblGhiChu.setForeground(coQuyenQuanLy ? new Color(30, 64, 175) : new Color(161, 84, 0));
        panelWrapper.add(lblGhiChu, BorderLayout.SOUTH);

        apDungPhanQuyen();
        return panelWrapper;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panel.setOpaque(false);

        JButton btnThem   = GiaoDienUtil.taoNut("➕  Thêm",       GiaoDienUtil.MAU_CHINH);
        JButton btnSua    = GiaoDienUtil.taoNut("✏  Sửa",         GiaoDienUtil.MAU_XAM);
        JButton btnXoa    = GiaoDienUtil.taoNut("🗑  Xóa",         GiaoDienUtil.MAU_DO);
        JButton btnLamMoi = GiaoDienUtil.taoNut("↺  Làm mới form", new Color(71, 85, 105));

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> clearForm());

        btnThem.setEnabled(coQuyenQuanLy);
        btnSua.setEnabled(coQuyenQuanLy);
        btnXoa.setEnabled(coQuyenQuanLy);

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        return panel;
    }

    private void apDungPhanQuyen() {
        txtMa.setEditable(coQuyenQuanLy);
        txtTen.setEditable(coQuyenQuanLy);
        txtDongMay.setEditable(coQuyenQuanLy);
        txtMauSac.setEditable(coQuyenQuanLy);
        txtDungLuong.setEditable(coQuyenQuanLy);
        txtGiaNhap.setEditable(coQuyenQuanLy);
        txtGiaBan.setEditable(coQuyenQuanLy);
        txtSoLuongTon.setEditable(coQuyenQuanLy);
        cboTrangThai.setEnabled(coQuyenQuanLy);
    }

    @Override
    public void lamMoiDuLieu() {
        try {
            txtTimKiem.setText("");
            danhSachSanPham = sanPhamDAO.layDanhSachSanPham();
            taiBang();
            clearForm();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemSanPham() {
        try {
            danhSachSanPham = sanPhamDAO.timKiemSanPham(txtTimKiem.getText());
            taiBang();
            clearForm();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themSanPham() {
        if (!coQuyenQuanLy) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được thêm sản phẩm."); return; }
        try {
            SanPham sp = docSanPhamTuForm();
            if (sanPhamDAO.themSanPham(sp)) {
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm. Kiểm tra mã sản phẩm có bị trùng không.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void suaSanPham() {
        if (!coQuyenQuanLy) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được cập nhật sản phẩm."); return; }
        if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Hãy chọn sản phẩm cần sửa."); return; }
        try {
            SanPham sp = docSanPhamTuForm();
            if (sanPhamDAO.suaSanPham(sp)) {
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void xoaSanPham() {
        if (!coQuyenQuanLy) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được xóa sản phẩm."); return; }
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Hãy chọn sản phẩm cần xóa."); return; }

        int luaChon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm đã chọn không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (luaChon != JOptionPane.YES_OPTION) return;

        String ma = danhSachSanPham.get(row).getMaSanPham();
        if (sanPhamDAO.xoaSanPham(ma)) {
            lamMoiDuLieu();
            JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Không thể xóa. Sản phẩm đã phát sinh giao dịch.");
        }
    }

    private SanPham docSanPhamTuForm() {
        String ma       = txtMa.getText().trim();
        String ten      = txtTen.getText().trim();
        String dongMay  = txtDongMay.getText().trim();
        String mauSac   = txtMauSac.getText().trim();
        String dungLuong = txtDungLuong.getText().trim();
        String trangThai = String.valueOf(cboTrangThai.getSelectedItem());

        if (ma.isEmpty() || ten.isEmpty() || dongMay.isEmpty())
            throw new IllegalArgumentException("Mã sản phẩm, tên sản phẩm và dòng máy không được để trống.");

        double giaNhap   = docSoThuc(txtGiaNhap.getText(), "Giá nhập");
        double giaBan    = docSoThuc(txtGiaBan.getText(), "Giá bán");
        int soLuongTon   = docSoNguyen(txtSoLuongTon.getText(), "Số lượng tồn");

        if (giaNhap < 0 || giaBan < 0 || soLuongTon < 0)
            throw new IllegalArgumentException("Giá nhập, giá bán và số lượng tồn phải ≥ 0.");

        return new SanPham(ma, ten, dongMay, mauSac, dungLuong, giaNhap, giaBan, soLuongTon, trangThai);
    }

    private double docSoThuc(String v, String ten) {
        try { return Double.parseDouble(v.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(ten + " phải là số hợp lệ."); }
    }

    private int docSoNguyen(String v, String ten) {
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(ten + " phải là số nguyên hợp lệ."); }
    }

    private void taiBang() {
        modelBang.setRowCount(0);
        for (SanPham sp : danhSachSanPham) {
            modelBang.addRow(new Object[]{
                    sp.getMaSanPham(), sp.getTenSanPham(), sp.getDongMay(),
                    sp.getMauSac(), sp.getDungLuong(),
                    dinhDangTien(sp.getGiaNhap()), dinhDangTien(sp.getGiaBan()),
                    sp.getSoLuongTon(), sp.getTrangThai()
            });
        }
    }

    private void clearForm() {
        txtMa.setText(""); txtTen.setText(""); txtDongMay.setText("");
        txtMauSac.setText(""); txtDungLuong.setText("");
        txtGiaNhap.setText(""); txtGiaBan.setText(""); txtSoLuongTon.setText("0");
        cboTrangThai.setSelectedItem(CHE_DO_THEO_TON_KHO);
        capNhatTrangThaiTuDongTrenForm();
        table.clearSelection();
        txtMa.requestFocus();
    }

    private void hienThiLenForm() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= danhSachSanPham.size()) return;
        SanPham sp = danhSachSanPham.get(row);
        txtMa.setText(sp.getMaSanPham());
        txtTen.setText(sp.getTenSanPham());
        txtDongMay.setText(sp.getDongMay());
        txtMauSac.setText(sp.getMauSac());
        txtDungLuong.setText(sp.getDungLuong());
        txtGiaNhap.setText(String.valueOf(sp.getGiaNhap()));
        txtGiaBan.setText(String.valueOf(sp.getGiaBan()));
        txtSoLuongTon.setText(String.valueOf(sp.getSoLuongTon()));
        cboTrangThai.setSelectedItem(SanPhamDAO.TRANG_THAI_TAM_NGUNG.equalsIgnoreCase(sp.getTrangThai())
                ? SanPhamDAO.TRANG_THAI_TAM_NGUNG : CHE_DO_THEO_TON_KHO);
        capNhatTrangThaiTuDongTrenForm();
    }

    private String dinhDangTien(double v) { return String.format("%,.0f", v); }

    private void capNhatTrangThaiTuDongTrenForm() {
        String s = txtSoLuongTon.getText().trim();
        if (s.isEmpty()) { lblTrangThaiTuDong.setText("Cần nhập số lượng tồn"); return; }
        try {
            int q = Integer.parseInt(s);
            lblTrangThaiTuDong.setText(q < 0 ? "Số lượng không hợp lệ" : SanPhamDAO.xacDinhTrangThaiTuDong(q));
        } catch (NumberFormatException e) {
            lblTrangThaiTuDong.setText("Số lượng không hợp lệ");
        }
    }
}
