package giao_dien;

import giao_dien.panel.*;
import model.NguoiDung;
import util.GiaoDienUtil;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class TrangChuForm extends JFrame {
    private final NguoiDung nguoiDungDangNhap;
    private final Map<String, JPanel> danhSachPanel = new LinkedHashMap<>();
    private final Map<String, JButton> nutMenu = new LinkedHashMap<>();
    private JPanel panelNoiDung;
    private CardLayout cardLayout;
    private String cardHienTai = "";

    public TrangChuForm(NguoiDung nguoiDungDangNhap) {
        this.nguoiDungDangNhap = nguoiDungDangNhap;
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setTitle("Hệ thống quản lý kho hàng iPhone");
        setSize(1280, 740);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(taoPanelMenu(), BorderLayout.WEST);
        add(taoPanelNoiDung(), BorderLayout.CENTER);

        hienThiNoiDung("trang_chu");
    }

    private JPanel taoPanelMenu() {
        JPanel panelMenu = new JPanel();
        panelMenu.setPreferredSize(new Dimension(230, 0));
        panelMenu.setBackground(GiaoDienUtil.MAU_SIDEBAR);
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));

        // Logo + tên app
        JLabel lblLogo = new JLabel("📦  KHO IPHONE");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Đường kẻ phân cách
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(51, 65, 85));
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Thông tin người dùng
        JLabel lblHoTen = new JLabel(nguoiDungDangNhap.getHoTen());
        lblHoTen.setForeground(new Color(203, 213, 225));
        lblHoTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHoTen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVaiTro = new JLabel(nguoiDungDangNhap.getVaiTro());
        lblVaiTro.setForeground(new Color(148, 163, 184));
        lblVaiTro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVaiTro.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(51, 65, 85));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        panelMenu.add(lblLogo);
        panelMenu.add(Box.createVerticalStrut(14));
        panelMenu.add(sep1);
        panelMenu.add(Box.createVerticalStrut(12));
        panelMenu.add(lblHoTen);
        panelMenu.add(Box.createVerticalStrut(2));
        panelMenu.add(lblVaiTro);
        panelMenu.add(Box.createVerticalStrut(14));
        panelMenu.add(sep2);
        panelMenu.add(Box.createVerticalStrut(14));

        String[][] menuItems = {
            {"🏠  Trang chủ",          "trang_chu"},
            {"📋  Quản lý sản phẩm",   "quan_li_san_pham"},
            {"📥  Nhập kho",            "nhap_kho"},
            {"📤  Xuất kho",            "xuat_kho"},
            {"📊  Thống kê",            "thong_ke"},
        };

        for (String[] item : menuItems) {
            JButton btn = taoNutMenu(item[0], item[1]);
            nutMenu.put(item[1], btn);
            panelMenu.add(btn);
            panelMenu.add(Box.createVerticalStrut(4));
        }

        panelMenu.add(Box.createVerticalGlue());
        panelMenu.add(taoNutThoat());

        return panelMenu;
    }

    private JButton taoNutMenu(String ten, String cardName) {
        JButton btn = new JButton(ten);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(GiaoDienUtil.MAU_SIDEBAR_NUT);
        btn.setForeground(new Color(203, 213, 225));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> hienThiNoiDung(cardName));
        return btn;
    }

    private JButton taoNutThoat() {
        JButton btn = new JButton("🚪  Đăng xuất");
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(127, 29, 29));
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> dangXuat());
        return btn;
    }

    private JPanel taoPanelNoiDung() {
        cardLayout = new CardLayout();
        panelNoiDung = new JPanel(cardLayout);
        panelNoiDung.setBackground(GiaoDienUtil.MAU_NEN_CHAN);

        dangKyPanel("trang_chu",        new TrangChuPanel());
        dangKyPanel("quan_li_san_pham", new QuanLiSanPhamPanel(nguoiDungDangNhap));
        dangKyPanel("nhap_kho",         new NhapKhoPanel(nguoiDungDangNhap));
        dangKyPanel("xuat_kho",         new XuatKhoPanel(nguoiDungDangNhap));
        dangKyPanel("thong_ke",         new ThongKePanel());

        return panelNoiDung;
    }

    private void dangKyPanel(String tenCard, JPanel panel) {
        danhSachPanel.put(tenCard, panel);
        panelNoiDung.add(panel, tenCard);
    }

    private void hienThiNoiDung(String tenCard) {
        cardLayout.show(panelNoiDung, tenCard);
        cardHienTai = tenCard;

        // Highlight nút đang active
        for (Map.Entry<String, JButton> entry : nutMenu.entrySet()) {
            boolean active = entry.getKey().equals(tenCard);
            entry.getValue().setBackground(active
                    ? GiaoDienUtil.MAU_CHINH
                    : GiaoDienUtil.MAU_SIDEBAR_NUT);
            entry.getValue().setForeground(active ? Color.WHITE : new Color(203, 213, 225));
        }

        JPanel panel = danhSachPanel.get(tenCard);
        if (panel instanceof LamMoiDuLieu) {
            ((LamMoiDuLieu) panel).lamMoiDuLieu();
        }
    }

    private void dangXuat() {
        int luaChon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất không?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
        if (luaChon == JOptionPane.YES_OPTION) {
            new DangNhapForm().setVisible(true);
            dispose();
        }
    }
}
