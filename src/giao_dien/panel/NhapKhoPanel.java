package giao_dien.panel;

import dao.NhapKhoDAO;
import dao.SanPhamDAO;
import model.BanGhiNhapKho;
import model.NguoiDung;
import model.SanPham;
import util.GiaoDienUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NhapKhoPanel extends JPanel implements LamMoiDuLieu {
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String LOC_TAT_CA = "Tất cả";

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final NhapKhoDAO nhapKhoDAO = new NhapKhoDAO();
    private final NguoiDung nguoiDungDangNhap;
    private final boolean coQuyenDuyet;
    private final DefaultTableModel modelBang = new DefaultTableModel();

    private ArrayList<SanPham> danhSachSanPham = new ArrayList<>();
    private ArrayList<BanGhiNhapKho> danhSachPhieuNhap = new ArrayList<>();
    private ArrayList<BanGhiNhapKho> danhSachHienThi = new ArrayList<>();

    private JTextField txtMaPhieu, txtNgayNhap, txtTonKhoHienTai, txtGiaNhap, txtTrangThaiSanPham;
    private JComboBox<String> cboSanPham, cboLocTrangThai;
    private JTextField txtSoLuongNhap;
    private JTextArea txtGhiChu;
    private JLabel lblThongTinHangCho;
    private JTable table;

    public NhapKhoPanel(NguoiDung nguoiDungDangNhap) {
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

        JLabel lblTieuDe = new JLabel("Nhập kho");
        lblTieuDe.setFont(GiaoDienUtil.FONT_TIEU_DE);
        lblTieuDe.setForeground(new Color(15, 23, 42));
        panel.add(lblTieuDe, BorderLayout.WEST);

        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelLoc.setOpaque(false);
        cboLocTrangThai = new JComboBox<>(new String[]{
                LOC_TAT_CA,
                NhapKhoDAO.TRANG_THAI_CHO_DUYET,
                NhapKhoDAO.TRANG_THAI_DA_DUYET,
                NhapKhoDAO.TRANG_THAI_DA_HUY
        });
        cboLocTrangThai.setSelectedItem(coQuyenDuyet ? NhapKhoDAO.TRANG_THAI_CHO_DUYET : LOC_TAT_CA);
        cboLocTrangThai.addActionListener(e -> taiBangLichSu());

        panelLoc.add(new JLabel("Lọc theo:"));
        panelLoc.add(cboLocTrangThai);
        panel.add(panelLoc, BorderLayout.EAST);

        lblThongTinHangCho = new JLabel(" ");
        lblThongTinHangCho.setForeground(new Color(90, 90, 90));
        lblThongTinHangCho.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JLabel lblMoTa = new JLabel(coQuyenDuyet
                ? "ℹ  Quản lý tạo phiếu sẽ được duyệt ngay. Có thể duyệt/hủy phiếu đang chờ."
                : "ℹ  Phiếu nhập do nhân viên tạo sẽ nằm trong hàng chờ quản lý duyệt.");
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
        panel.add(taoFormNhapKho(), BorderLayout.NORTH);

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

    private JPanel taoFormNhapKho() {
        JPanel panel = new JPanel(new GridLayout(5, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GiaoDienUtil.MAU_VIEN),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(4, 8, 4, 8),
                        "Thông tin phiếu nhập")));

        txtMaPhieu          = new JTextField(); txtMaPhieu.setEditable(false);
        txtNgayNhap         = new JTextField(); txtNgayNhap.setEditable(false);
        txtTonKhoHienTai    = new JTextField(); txtTonKhoHienTai.setEditable(false);
        txtGiaNhap          = new JTextField(); txtGiaNhap.setEditable(false);
        txtTrangThaiSanPham = new JTextField(); txtTrangThaiSanPham.setEditable(false);
        cboSanPham          = new JComboBox<>();
        txtSoLuongNhap      = new JTextField();
        txtGhiChu           = new JTextArea(3, 20);
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);

        cboSanPham.addActionListener(e -> capNhatThongTinSanPham());

        panel.add(new JLabel("Mã phiếu nhập"));    panel.add(txtMaPhieu);
        panel.add(new JLabel("Ngày lập"));          panel.add(txtNgayNhap);
        panel.add(new JLabel("Sản phẩm"));          panel.add(cboSanPham);
        panel.add(new JLabel("Tồn kho hiện tại"));  panel.add(txtTonKhoHienTai);
        panel.add(new JLabel("Trạng thái SP"));     panel.add(txtTrangThaiSanPham);
        panel.add(new JLabel("Giá nhập"));          panel.add(txtGiaNhap);
        panel.add(new JLabel("Số lượng nhập"));     panel.add(txtSoLuongNhap);
        panel.add(new JLabel("Người lập"));         panel.add(new JLabel(nguoiDungDangNhap.getHoTen()));
        panel.add(new JLabel("Ghi chú"));           panel.add(new JScrollPane(txtGhiChu));
        panel.add(new JLabel());                    panel.add(new JLabel());

        return panel;
    }

    private JPanel taoPanelNut() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panel.setOpaque(false);

        JButton btnLuu   = GiaoDienUtil.taoNut("💾  Lưu phiếu",  GiaoDienUtil.MAU_CHINH);
        JButton btnDuyet = GiaoDienUtil.taoNut("✅  Duyệt phiếu", GiaoDienUtil.MAU_XANH_LA);
        JButton btnHuy   = GiaoDienUtil.taoNut("❌  Hủy phiếu",   GiaoDienUtil.MAU_DO);
        JButton btnLamMoi = GiaoDienUtil.taoNut("↻  Làm mới",    GiaoDienUtil.MAU_XAM);

        btnLuu.addActionListener(e -> luuPhieuNhap());
        btnDuyet.addActionListener(e -> duyetPhieuNhapDangChon());
        btnHuy.addActionListener(e -> huyPhieuNhapDangChon());
        btnLamMoi.addActionListener(e -> lamMoiDuLieu());

        btnDuyet.setEnabled(coQuyenDuyet);
        btnHuy.setEnabled(coQuyenDuyet);

        panel.add(btnLuu);
        panel.add(btnDuyet);
        panel.add(btnHuy);
        panel.add(btnLamMoi);
        return panel;
    }

    @Override
    public void lamMoiDuLieu() {
        try {
            txtMaPhieu.setText(nhapKhoDAO.taoMaPhieuMoi());
            txtNgayNhap.setText(DINH_DANG_NGAY.format(LocalDateTime.now()));
            txtSoLuongNhap.setText("");
            txtGhiChu.setText("");
            taiDanhSachSanPham();
            taiBangLichSu();
            capNhatThongTinSanPham();
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
        danhSachPhieuNhap = nhapKhoDAO.layDanhSachPhieuNhap();
        danhSachHienThi.clear();

        String loc = String.valueOf(cboLocTrangThai.getSelectedItem());
        for (BanGhiNhapKho b : danhSachPhieuNhap) {
            if (!LOC_TAT_CA.equalsIgnoreCase(loc) && !loc.equalsIgnoreCase(b.getTrangThai())) continue;
            danhSachHienThi.add(b);
            modelBang.addRow(new Object[]{
                    b.getMaPhieu(), DINH_DANG_NGAY.format(b.getNgayNhap()),
                    b.getMaSanPham(), b.getTenSanPham(), b.getSoLuong(),
                    dinhDangTien(b.getDonGia()), dinhDangTien(b.getThanhTien()),
                    b.getNguoiTao(), b.getTrangThai(),
                    hoac(b.getNguoiDuyet()), dinhDangNgay(b.getNgayDuyet()), hoac(b.getGhiChuDuyet())
            });
        }

        int soCho = demTheoTrangThai(danhSachPhieuNhap, NhapKhoDAO.TRANG_THAI_CHO_DUYET);
        lblThongTinHangCho.setText(coQuyenDuyet
                ? "Hàng chờ duyệt: " + soCho + " phiếu"
                : "Tổng phiếu nhập: " + danhSachPhieuNhap.size());
    }

    private void capNhatThongTinSanPham() {
        int i = cboSanPham.getSelectedIndex();
        if (i < 0 || i >= danhSachSanPham.size()) {
            txtTonKhoHienTai.setText(""); txtGiaNhap.setText(""); txtTrangThaiSanPham.setText(""); return;
        }
        SanPham sp = danhSachSanPham.get(i);
        txtTonKhoHienTai.setText(String.valueOf(sp.getSoLuongTon()));
        txtGiaNhap.setText(dinhDangTien(sp.getGiaNhap()));
        txtTrangThaiSanPham.setText(sp.getTrangThai());
    }

    private void luuPhieuNhap() {
        int i = cboSanPham.getSelectedIndex();
        if (i < 0 || i >= danhSachSanPham.size()) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào để lập phiếu nhập."); return;
        }
        try {
            int sl = Integer.parseInt(txtSoLuongNhap.getText().trim());
            if (sl <= 0) { JOptionPane.showMessageDialog(this, "Số lượng nhập phải > 0."); return; }

            SanPham sp = danhSachSanPham.get(i);
            boolean ok = nhapKhoDAO.taoPhieuNhap(txtMaPhieu.getText().trim(),
                    nguoiDungDangNhap.getId(), txtGhiChu.getText().trim(), sp, sl, coQuyenDuyet);
            if (ok) {
                lamMoiDuLieu();
                JOptionPane.showMessageDialog(this, coQuyenDuyet
                        ? "Phiếu nhập đã được duyệt và cập nhật tồn kho."
                        : "Đã tạo phiếu nhập. Phiếu đang chờ quản lý duyệt.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể lưu phiếu nhập.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng nhập phải là số nguyên hợp lệ.");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void duyetPhieuNhapDangChon() {
        if (!coQuyenDuyet) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được duyệt phiếu."); return; }
        BanGhiNhapKho b = layPhieuDangChon();
        if (b == null) return;
        if (!NhapKhoDAO.TRANG_THAI_CHO_DUYET.equalsIgnoreCase(b.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ duyệt được phiếu đang ở trạng thái chờ duyệt."); return;
        }
        try {
            if (nhapKhoDAO.duyetPhieuNhap(b.getMaPhieu(), nguoiDungDangNhap.getId())) {
                lamMoiDuLieu(); JOptionPane.showMessageDialog(this, "Duyệt phiếu nhập thành công.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể duyệt phiếu nhập đã chọn.");
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void huyPhieuNhapDangChon() {
        if (!coQuyenDuyet) { JOptionPane.showMessageDialog(this, "Chỉ quản lý mới được hủy phiếu."); return; }
        BanGhiNhapKho b = layPhieuDangChon();
        if (b == null) return;
        if (!NhapKhoDAO.TRANG_THAI_CHO_DUYET.equalsIgnoreCase(b.getTrangThai())) {
            JOptionPane.showMessageDialog(this, "Chỉ hủy được phiếu đang ở trạng thái chờ duyệt."); return;
        }
        String ghiChu = JOptionPane.showInputDialog(this, "Nhập lý do hủy phiếu:");
        if (ghiChu == null) return;
        if (ghiChu.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Phiếu hủy bắt buộc có ghi chú."); return; }
        try {
            if (nhapKhoDAO.huyPhieuNhap(b.getMaPhieu(), nguoiDungDangNhap.getId(), ghiChu.trim())) {
                lamMoiDuLieu(); JOptionPane.showMessageDialog(this, "Đã hủy phiếu nhập.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể hủy phiếu nhập đã chọn.");
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BanGhiNhapKho layPhieuDangChon() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= danhSachHienThi.size()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một phiếu trong danh sách."); return null;
        }
        return danhSachHienThi.get(row);
    }

    private String dinhDangTien(double v) { return String.format("%,.0f", v); }
    private String dinhDangNgay(LocalDateTime t) { return t == null ? "" : DINH_DANG_NGAY.format(t); }
    private String hoac(String s) { return s == null ? "" : s; }

    private int demTheoTrangThai(ArrayList<BanGhiNhapKho> ds, String tt) {
        int n = 0;
        for (BanGhiNhapKho b : ds) if (tt.equalsIgnoreCase(b.getTrangThai())) n++;
        return n;
    }
}
