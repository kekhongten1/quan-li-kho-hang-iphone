package giao_dien.panel;

import dao.SanPhamDAO;
import dao.XuatKhoDAO;
import model.BanGhiXuatKho;
import model.NguoiDung;
import model.SanPham;
import util.GiaoDienUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class XuatKhoPanel extends JPanel implements LamMoiDuLieu {
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String LOC_TAT_CA = "Tất cả";

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final XuatKhoDAO xuatKhoDAO = new XuatKhoDAO();
    private final NguoiDung nguoiDungDangNhap;
    private final boolean coQuyenDuyet;
    private final DefaultTableModel modelBang = new DefaultTableModel();

    private ArrayList<SanPham> danhSachSanPham = new ArrayList<>();
    private ArrayList<BanGhiXuatKho> danhSachPhieuXuat = new ArrayList<>();
    private ArrayList<BanGhiXuatKho> danhSachHienThi = new ArrayList<>();

    private JTextField txtMaPhieu, txtNgayXuat, txtTonKhoHienTai, txtGiaXuat, txtTrangThaiSanPham;
    private JComboBox<String> cboSanPham, cboLocTrangThai;
    private JTextField txtSoLuongXuat;
    private JTextArea txtGhiChu;
    private JLabel lblCanhBao, lblThongTinHangCho;
    private JTable table;

    public XuatKhoPanel(NguoiDung nguoiDungDangNhap) {
        this.nguoiDungDangNhap = nguoiDungDangNhap;
        this.coQuyenDuyet = nguoiDungDangNhap.laQuanLy();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(GiaoDienUtil.MAU_NEN_CHAN);

        add(taoTieuDe(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoPanelNut(), BorderLayout.SOUTH);
    }

    private JPanel taoTieuDe() {
        JPanel panel = new JPanel(new BorderLayout(10, 6));
        panel.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Xuất kho");
        lblTieuDe.setFont(GiaoDienUtil.FONT_TIEU_DE);
        lblTieuDe.setForeground(new Color(15, 23, 42));
        panel.add(lblTieuDe, BorderLayout.WEST);

        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelLoc.setOpaque(false);
        cboLocTrangThai = new JComboBox<>(new String[]{
                LOC_TAT_CA,
                XuatKhoDAO.TRANG_THAI_CHO_DUYET,
                XuatKhoDAO.TRANG_THAI_DA_DUYET,
                XuatKhoDAO.TRANG_THAI_DA_HUY
        });
        cboLocTrangThai.setSelectedItem(coQuyenDuyet ? XuatKhoDAO.TRANG_THAI_CHO_DUYET : LOC_TAT_CA);
        cboLocTrangThai.addActionListener(e -> taiBangLichSu());

        panelLoc.add(new JLabel("Lọc theo:"));
        panelLoc.add(cboLocTrangThai);
        panel.add(panelLoc, BorderLayout.EAST);

        lblThongTinHangCho = new JLabel(" ");
        lblThongTinHangCho.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblThongTinHangCho.setForeground(new Color(90, 90, 90));

        JLabel lblMoTa = new JLabel(coQuyenDuyet
                ? "ℹ  Quản lý tạo phiếu xuất sẽ được duyệt ngay. Có thể duyệt/hủy phiếu chờ nhân viên."
                : "ℹ  Phiếu xuất do nhân viên tạo cần quản lý duyệt trước khi trừ tồn.");
        lblMoTa.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMoTa.setForeground(new Color(71, 85, 105));

        JPanel panelThongTin = new JPanel(new GridLayout(2, 1, 0, 2));
        panelThongTin.setOpaque(false);
        panelThongTin.add(lblMoTa);
        panelThongTin.add(lblThongTinHangCho);
        panel.add(panelThongTin, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.add(taoFormXuatKho(), BorderLayout.NORTH);

        modelBang.setColumnIdentifiers(new String[]{
                "Mã phiếu", "Ngày lập", "Mã SP", "Tên sản phẩm",
                "Số lượng", "Đơn giá", "Thành tiền",
                "Người lập", "Trạng thái", "Người duyệt", "Ngày duyệt", "Ghi chú hủy"
        });

        table = new JTable(modelBang);
        GiaoDienUtil.caiThienBang(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoFormXuatKho() {
        JPanel panelWrapper = new JPanel(new BorderLayout(0, 8));
        panelWrapper.setOpaque(false);

        JPanel panel = new JPanel(new GridLayout(5, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GiaoDienUtil.MAU_VIEN),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(4, 8, 4, 8),
                        "Thông tin phiếu xuất")));

        txtMaPhieu          = new JTextField(); txtMaPhieu.setEditable(false);
        txtNgayXuat         = new JTextField(); txtNgayXuat.setEditable(false);
        txtTonKhoHienTai    = new JTextField(); txtTonKhoHienTai.setEditable(false);
        txtGiaXuat          = new JTextField(); txtGiaXuat.setEditable(false);
        txtTrangThaiSanPham = new JTextField(); txtTrangThaiSanPham.setEditable(false);
        cboSanPham          = new JComboBox<>();
        txtSoLuongXuat      = new JTextField();
        txtGhiChu           = new JTextArea(3, 20);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        lblCanhBao = new JLabel(" ");
        lblCanhBao.setForeground(GiaoDienUtil.MAU_DO);
        lblCanhBao.setFont(new Font("Segoe UI", Font.BOLD, 12));

        cboSanPham.addActionListener(e -> capNhatThongTinSanPham());
        txtSoLuongXuat.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { capNhatCanhBao(); }
            public void removeUpdate(DocumentEvent e)  { capNhatCanhBao(); }
            public void changedUpdate(DocumentEvent e) { capNhatCanhBao(); }
        });

        panel.add(new JLabel("Mã phiếu xuất"));    panel.add(txtMaPhieu);
        panel.add(new JLabel("Ngày lập"));          panel.add(txtNgayXuat);
        panel.add(new JLabel("Sản phẩm"));          panel.add(cboSanPham);
        panel.add(new JLabel("Tồn kho hiện tại"));  panel.add(txtTonKhoHienTai);
        panel.add(new JLabel("Trạng thái SP"));     panel.add(txtTrangThaiSanPham);
        panel.add(new JLabel("Giá xuất"));          panel.add(txtGiaXuat);
        panel.add(new JLabel("Số lượng xuất"));     panel.add(txtSoLuongXuat);
        panel.add(new JLabel("Người lập"));         panel.add(new JLabel(nguoiDungDangNhap.getHoTen()));
        panel.add(new JLabel("Ghi chú"));           panel.add(new JScrollPane(txtGhiChu));
        panel.add(new JLabel());                    panel.add(new JLabel());

        panelWrapper.add(panel, BorderLayout.CENTER);
        panelWrapper.add(lblCanhBao, BorderLayout.SOUTH);
        return panelWrapper;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panel.setOpaque(false);

        JButton btnLuu    = GiaoDienUtil.taoNut("💾  Lưu phiếu",   GiaoDienUtil.MAU_CHINH);
        JButton btnDuyet  = GiaoDienUtil.taoNut("✅  Duyệt phiếu", GiaoDienUtil.MAU_XANH_LA);
        JButton btnHuy    = GiaoDienUtil.taoNut("❌  Hủy phiếu",   GiaoDienUtil.MAU_DO);
        JButton btnLamMoi = GiaoDienUtil.taoNut("↻  Làm mới",     GiaoDienUtil.MAU_XAM);

        btnLuu.addActionListener(e -> luuPhieuXuat());
        btnDuyet.addActionListener(e -> duyetPhieuXuatDangChon());
        btnHuy.addActionListener(e -> huyPhieuXuatDangChon());
        btnLamMoi.addActionListener(e -> lamMoiDuLieu());

        btnDuyet.setEnabled(coQuyenDuyet);
        btnHuy.setEnabled(coQuyenDuyet);

        panel.add(btnLuu); panel.add(btnDuyet); panel.add(btnHuy); panel.add(btnLamMoi);
        return panel;
    }

    @Override
    public void lamMoiDuLieu() {
        try {
            txtMaPhieu.setText(xuatKhoDAO.taoMaPhieuMoi());
            txtNgayXuat.setText(DINH_DANG_NGAY.format(LocalDateTime.now()));
            txtSoLuongXuat.setText(""); txtGhiChu.setText("");
            taiDanhSachSanPham(); taiBangLichSu(); capNhatThongTinSanPham();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taiDanhSachSanPham() {
        danhSachSanPham = sanPhamDAO.layDanhSachSanPham();
        cboSanPham.removeAllItems();
        for (SanPham sp : danhSachSanPham)
            cboSanPham.addItem(sp.getMaSanPham() + " - " + sp.getTenSanPham());
    }

    private void taiBangLichSu() {
        modelBang.setRowCount(0);
        danhSachPhieuXuat = xuatKhoDAO.layDanhSachPhieuXuat();
        danhSachHienThi.clear();

        String loc = String.valueOf(cboLocTrangThai.getSelectedItem());
        for (BanGhiXuatKho b : danhSachPhieuXuat) {
            if (!LOC_TAT_CA.equalsIgnoreCase(loc) && !loc.equalsIgnoreCase(b.getTrangThai())) continue;
            danhSachHienThi.add(b);
            modelBang.addRow(new Object[]{
                    b.getMaPhieu(), DINH_DANG_NGAY.format(b.getNgayXuat()),
                    b.getMaSanPham(), b.getTenSanPham(), b.getSoLuong(),
                    dinhDangTien(b.getDonGia()), dinhDangTien(b.getThanhTien()),
                    b.getNguoiTao(), b.getTrangThai(),
                    hoac(b.getNguoiDuyet()), dinhDangNgay(b.getNgayDuyet()), hoac(b.getGhiChuDuyet())
            });
        }

        int soCho = demTheoTrangThai(danhSachPhieuXuat, XuatKhoDAO.TRANG_THAI_CHO_DUYET);
        lblThongTinHangCho.setText(coQuyenDuyet
                ? "Hàng chờ duyệt: " + soCho + " phiếu"
                : "Tổng phiếu xuất: " + danhSachPhieuXuat.size());
    }

    private void capNhatThongTinSanPham() {
        int i = cboSanPham.getSelectedIndex();
        if (i < 0 || i >= danhSachSanPham.size()) {
            txtTonKhoHienTai.setText(""); txtGiaXuat.setText(""); txtTrangThaiSanPham.setText(""); lblCanhBao.setText(" "); return;
        }
        SanPham sp = danhSachSanPham.get(i);
        txtTonKhoHienTai.setText(String.valueOf(sp.getSoLuongTon()));
        txtGiaXuat.setText(dinhDangTien(sp.getGiaBan()));
        txtTrangThaiSanPham.setText(sp.getTrangThai());
        capNhatCanhBao();
    }

    private void luuPhieuXuat() {
        int i = cboSanPham.getSelectedIndex();
        if (i < 0 || i >= danhSachSanPham.size()) { JOptionPane.showMessageDialog(this, "Chưa có sản phẩm để lập phiếu xuất."); return; }
        try {
            int sl = Integer.parseInt(txtSoLuongXuat.getText().trim());
            if (sl <= 0) { JOptionPane.showMessageDialog(this, "Số lượng xuất phải > 0."); return; }

            SanPham sp = danhSachSanPham.get(i);
            String canh = xayDungCanhBao(sp, sl);
            if (!canh.trim().isEmpty()) { JOptionPane.showMessageDialog(this, canh); return; }

            boolean ok = xuatKhoDAO.taoPhieuXuat(txtMaPhieu.getText().trim(),
                    nguoiDungDangNhap.getId(), txtGhiChu.getText().trim(), sp, sl, coQuyenDuyet);
            if (ok) {
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this, coQuyenDuyet
                        ? "Phiếu xuất đã được duyệt và cập nhật tồn kho."
                        : "Đã tạo phiếu xuất. Phiếu đang chờ quản lý duyệt.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu phiếu xuất. Sản phẩm có thể tạm ngừng, hết hàng hoặc không đủ tồn.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng xuất phải là số nguyên hợp lệ.");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void duyetPhieuXuatDangChon() {
        if (!coQuyenDuyet) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được duyệt phiếu."); return; }
        BanGhiXuatKho b = layPhieuDangChon();
        if (b == null) return;
        if (!XuatKhoDAO.TRANG_THAI_CHO_DUYET.equalsIgnoreCase(b.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ duyệt được phiếu đang ở trạng thái chờ duyệt."); return;
        }
        try {
            if (xuatKhoDAO.duyetPhieuXuat(b.getMaPhieu(), nguoiDungDangNhap.getId())) {
                lamMoiDuLieu(); JOptionPane.showMessageDialog(this, "Duyệt phiếu xuất thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể duyệt. Sản phẩm có thể tạm ngừng, hết hàng hoặc không đủ tồn.");
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void huyPhieuXuatDangChon() {
        if (!coQuyenDuyet) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được hủy phiếu."); return; }
        BanGhiXuatKho b = layPhieuDangChon();
        if (b == null) return;
        if (!XuatKhoDAO.TRANG_THAI_CHO_DUYET.equalsIgnoreCase(b.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ hủy được phiếu đang ở trạng thái chờ duyệt."); return;
        }
        String ghiChu = JOptionPane.showInputDialog(this, "Nhập lý do hủy phiếu:");
        if (ghiChu == null) return;
        if (ghiChu.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Phiếu hủy bắt buộc có ghi chú."); return; }
        try {
            if (xuatKhoDAO.huyPhieuXuat(b.getMaPhieu(), nguoiDungDangNhap.getId(), ghiChu.trim())) {
                lamMoiDuLieu(); JOptionPane.showMessageDialog(this, "Đã hủy phiếu xuất.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể hủy phiếu xuất đã chọn.");
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BanGhiXuatKho layPhieuDangChon() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= danhSachHienThi.size()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một phiếu trong danh sách."); return null;
        }
        return danhSachHienThi.get(row);
    }

    private String xayDungCanhBao(SanPham sp, Integer sl) {
        if (SanPhamDAO.TRANG_THAI_TAM_NGUNG.equalsIgnoreCase(sp.getTrangThai()))
            return "Cảnh báo: sản phẩm đang tạm ngừng, không được lập phiếu xuất.";
        if (SanPhamDAO.TRANG_THAI_HET_HANG.equalsIgnoreCase(sp.getTrangThai()) || sp.getSoLuongTon() <= 0)
            return "Cảnh báo: sản phẩm đã hết hàng, không được lập phiếu xuất.";
        if (sl != null && sl > sp.getSoLuongTon())
            return "Cảnh báo: số lượng xuất vượt tồn kho hiện tại, không được xuất âm tồn.";
        return "";
    }

    private void capNhatCanhBao() {
        int i = cboSanPham.getSelectedIndex();
        if (i < 0 || i >= danhSachSanPham.size()) { lblCanhBao.setText(" "); return; }
        Integer sl = null;
        String s = txtSoLuongXuat.getText().trim();
        if (!s.isEmpty()) {
            try {
                sl = Integer.parseInt(s);
                if (sl <= 0) { lblCanhBao.setText("⚠  Số lượng xuất phải lớn hơn 0."); return; }
            } catch (NumberFormatException e) {
                lblCanhBao.setText("⚠  Số lượng xuất phải là số nguyên hợp lệ."); return;
            }
        }
        String c = xayDungCanhBao(danhSachSanPham.get(i), sl);
        lblCanhBao.setText(c.isEmpty() ? " " : "⚠  " + c);
    }

    private String dinhDangTien(double v) { return String.format("%,.0f", v); }
    private String dinhDangNgay(LocalDateTime t) { return t == null ? "" : DINH_DANG_NGAY.format(t); }
    private String hoac(String s) { return s == null ? "" : s; }

    private int demTheoTrangThai(ArrayList<BanGhiXuatKho> ds, String tt) {
        int n = 0;
        for (BanGhiXuatKho b : ds) if (tt.equalsIgnoreCase(b.getTrangThai())) n++;
        return n;
    }
}
