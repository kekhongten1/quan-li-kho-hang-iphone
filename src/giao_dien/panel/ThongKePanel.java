package giao_dien.panel;

import dao.NhapKhoDAO;
import dao.SanPhamDAO;
import dao.ThongKeDAO;
import dao.XuatKhoDAO;
import model.BanGhiNhapKho;
import model.BanGhiXuatKho;
import model.SanPham;
import model.ThongKeKho;
import util.GiaoDienUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class ThongKePanel extends JPanel implements LamMoiDuLieu {
    private static final int NGUONG_CANH_BAO = 5;
    private static final int SO_BAN_GHI_GAN_DAY = 8;
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final NhapKhoDAO nhapKhoDAO = new NhapKhoDAO();
    private final XuatKhoDAO xuatKhoDAO = new XuatKhoDAO();

    private final DefaultTableModel modelTonKho   = new DefaultTableModel();
    private final DefaultTableModel modelCanhBao  = new DefaultTableModel();
    private final DefaultTableModel modelTamNgung = new DefaultTableModel();
    private final DefaultTableModel modelDaDuyet  = new DefaultTableModel();
    private final DefaultTableModel modelDaHuy    = new DefaultTableModel();

    private JLabel lblTongSanPham, lblTongTonKho, lblTongDaNhap, lblTongDaXuat, lblGiaTriTonKho;

    public ThongKePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(GiaoDienUtil.MAU_NEN_CHAN);

        add(taoHeader(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
    }

    private JLabel taoHeader() {
        JLabel lbl = new JLabel("Thống kê kho hàng");
        lbl.setFont(GiaoDienUtil.FONT_TIEU_DE);
        lbl.setForeground(new Color(15, 23, 42));
        return lbl;
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        JPanel panelThe = new JPanel(new GridLayout(1, 5, 10, 0));
        panelThe.setOpaque(false);
        lblTongSanPham  = taoThe(panelThe, "Tổng SP",         "📋", new Color(37, 99, 235));
        lblTongTonKho   = taoThe(panelThe, "Tổng tồn kho",    "📦", new Color(5, 150, 105));
        lblTongDaNhap   = taoThe(panelThe, "Tổng đã nhập",    "📥", new Color(109, 40, 217));
        lblTongDaXuat   = taoThe(panelThe, "Tổng đã xuất",    "📤", new Color(217, 119, 6));
        lblGiaTriTonKho = taoThe(panelThe, "Giá trị tồn",     "💰", new Color(185, 28, 28));
        panel.add(panelThe, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("Tồn kho",                taoPanelBang(modelTonKho,   new String[]{"Mã SP", "Tên sản phẩm", "Dòng máy", "Tồn kho", "Trạng thái"}, "Danh sách sản phẩm trong kho"));
        tabs.addTab("Sắp hết / Hết hàng",    taoPanelBang(modelCanhBao,  new String[]{"Mã SP", "Tên sản phẩm", "Dòng máy", "Tồn kho", "Trạng thái"}, "Danh sách sản phẩm sắp hết và hết hàng"));
        tabs.addTab("Tạm ngừng",              taoPanelBang(modelTamNgung, new String[]{"Mã SP", "Tên sản phẩm", "Dòng máy", "Tồn kho", "Trạng thái"}, "Danh sách sản phẩm tạm ngừng"));
        tabs.addTab("Phiếu đã duyệt",         taoPanelBang(modelDaDuyet,  new String[]{"Loại phiếu", "Mã phiếu", "Ngày lập", "Sản phẩm", "Số lượng", "Người lập", "Người duyệt", "Ngày duyệt"}, "Phiếu nhập xuất đã duyệt gần đây"));
        tabs.addTab("Phiếu đã hủy",           taoPanelBang(modelDaHuy,   new String[]{"Loại phiếu", "Mã phiếu", "Ngày lập", "Sản phẩm", "Số lượng", "Người lập", "Người hủy", "Ngày hủy", "Ghi chú hủy"}, "Phiếu nhập xuất đã hủy gần đây"));
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    private JLabel taoThe(JPanel cha, String tieuDe, String icon, Color mau) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(mau);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel lblTop = new JLabel(icon + "  " + tieuDe);
        lblTop.setForeground(new Color(255, 255, 255, 200));
        lblTop.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JLabel lblVal = new JLabel("0", SwingConstants.LEFT);
        lblVal.setForeground(Color.WHITE);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 26));

        p.add(lblTop, BorderLayout.NORTH);
        p.add(lblVal, BorderLayout.CENTER);
        cha.add(p);
        return lblVal;
    }

    private JPanel taoPanelBang(DefaultTableModel model, String[] cols, String tieuDe) {
        model.setColumnIdentifiers(cols);
        JTable table = new JTable(model);
        GiaoDienUtil.caiThienBang(table);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GiaoDienUtil.MAU_VIEN),
                BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6), tieuDe)));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void lamMoiDuLieu() {
        try {
            ThongKeKho tk = thongKeDAO.layThongKeTongQuan(NGUONG_CANH_BAO);
            lblTongSanPham.setText(String.valueOf(tk.getTongSanPham()));
            lblTongTonKho.setText(String.valueOf(tk.getTongTonKho()));
            lblTongDaNhap.setText(String.valueOf(tk.getTongDaNhap()));
            lblTongDaXuat.setText(String.valueOf(tk.getTongDaXuat()));
            lblGiaTriTonKho.setText(String.format("%,.0f ₫", tk.getGiaTriTonKho()));

            taiBangSanPham(modelTonKho,   thongKeDAO.laySanPhamConTrongKho());
            taiBangSanPham(modelCanhBao,  thongKeDAO.laySanPhamSapHetVaHetHang());
            taiBangSanPham(modelTamNgung, thongKeDAO.laySanPhamTamNgung());
            taiBangPhieu(modelDaDuyet, NhapKhoDAO.TRANG_THAI_DA_DUYET, false);
            taiBangPhieu(modelDaHuy,   NhapKhoDAO.TRANG_THAI_DA_HUY,  true);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void taiBangSanPham(DefaultTableModel model, ArrayList<SanPham> ds) {
        model.setRowCount(0);
        for (SanPham sp : ds)
            model.addRow(new Object[]{sp.getMaSanPham(), sp.getTenSanPham(), sp.getDongMay(), sp.getSoLuongTon(), sp.getTrangThai()});
    }

    private void taiBangPhieu(DefaultTableModel model, String trangThai, boolean includeGhiChu) {
        model.setRowCount(0);
        ArrayList<DongPhieu> ds = tongHopDuLieuPhieu(trangThai);
        for (int i = 0; i < Math.min(SO_BAN_GHI_GAN_DAY, ds.size()); i++) {
            DongPhieu d = ds.get(i);
            if (includeGhiChu) {
                model.addRow(new Object[]{d.loaiPhieu, d.maPhieu, DINH_DANG_NGAY.format(d.ngayLap),
                        d.tenSanPham, d.soLuong, d.nguoiLap, d.nguoiXuLy, dinhDangNgay(d.ngayXuLy), d.ghiChuXuLy});
            } else {
                model.addRow(new Object[]{d.loaiPhieu, d.maPhieu, DINH_DANG_NGAY.format(d.ngayLap),
                        d.tenSanPham, d.soLuong, d.nguoiLap, d.nguoiXuLy, dinhDangNgay(d.ngayXuLy)});
            }
        }
    }

    private ArrayList<DongPhieu> tongHopDuLieuPhieu(String trangThai) {
        ArrayList<DongPhieu> ds = new ArrayList<>();
        for (BanGhiNhapKho b : nhapKhoDAO.layDanhSachPhieuNhap()) {
            if (trangThai.equalsIgnoreCase(b.getTrangThai()))
                ds.add(new DongPhieu("Nhập", b.getMaPhieu(), b.getNgayNhap(), b.getTenSanPham(),
                        b.getSoLuong(), b.getNguoiTao(), b.getNguoiDuyet(), b.getNgayDuyet(), b.getGhiChuDuyet()));
        }
        for (BanGhiXuatKho b : xuatKhoDAO.layDanhSachPhieuXuat()) {
            if (trangThai.equalsIgnoreCase(b.getTrangThai()))
                ds.add(new DongPhieu("Xuất", b.getMaPhieu(), b.getNgayXuat(), b.getTenSanPham(),
                        b.getSoLuong(), b.getNguoiTao(), b.getNguoiDuyet(), b.getNgayDuyet(), b.getGhiChuDuyet()));
        }
        ds.sort(Comparator.comparing(DongPhieu::thoiGianSapXep).reversed());
        return ds;
    }

    private String dinhDangNgay(LocalDateTime t) { return t == null ? "" : DINH_DANG_NGAY.format(t); }

    private static class DongPhieu {
        final String loaiPhieu, maPhieu, tenSanPham, nguoiLap, nguoiXuLy, ghiChuXuLy;
        final LocalDateTime ngayLap, ngayXuLy;
        final int soLuong;

        DongPhieu(String loai, String ma, LocalDateTime ngayLap, String ten, int sl,
                  String nguoiLap, String nguoiXuLy, LocalDateTime ngayXuLy, String ghiChu) {
            this.loaiPhieu = loai; this.maPhieu = ma; this.ngayLap = ngayLap;
            this.tenSanPham = ten; this.soLuong = sl; this.nguoiLap = nguoiLap;
            this.nguoiXuLy = nguoiXuLy == null ? "" : nguoiXuLy;
            this.ngayXuLy = ngayXuLy;
            this.ghiChuXuLy = ghiChu == null ? "" : ghiChu;
        }

        LocalDateTime thoiGianSapXep() { return ngayXuLy == null ? ngayLap : ngayXuLy; }
    }
}
