package giao_dien;

import dao.NguoiDungDAO;
import model.NguoiDung;
import util.GiaoDienUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DangNhapForm extends JFrame {
    private final NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;

    public DangNhapForm() {
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setTitle("Đăng nhập - Hệ thống quản lý kho iPhone");
        setSize(480, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Panel nền chính
        JPanel panelNen = new JPanel(new GridBagLayout());
        panelNen.setBackground(new Color(248, 250, 252));
        setContentPane(panelNen);

        // Card đăng nhập
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(36, 40, 32, 40)
        ));
        card.setPreferredSize(new Dimension(380, 290));

        // Tiêu đề
        JLabel lblLogo = new JLabel("📦", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTieuDe = new JLabel("Quản Lý Kho Hàng iPhone");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTieuDe.setForeground(new Color(15, 23, 42));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMoTa = new JLabel("Đăng nhập để tiếp tục");
        lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMoTa.setForeground(new Color(100, 116, 139));
        lblMoTa.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form fields
        JPanel panelFields = new JPanel(new GridLayout(4, 1, 0, 6));
        panelFields.setOpaque(false);
        panelFields.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFields.setMaximumSize(new Dimension(300, 120));

        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(GiaoDienUtil.FONT_NHAN);
        lblUser.setForeground(new Color(51, 65, 85));

        txtTenDangNhap = new JTextField("admin");
        txtTenDangNhap.setFont(GiaoDienUtil.FONT_NHAN);
        txtTenDangNhap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(6, 10, 6, 10)));

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(GiaoDienUtil.FONT_NHAN);
        lblPass.setForeground(new Color(51, 65, 85));

        txtMatKhau = new JPasswordField("123");
        txtMatKhau.setFont(GiaoDienUtil.FONT_NHAN);
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(6, 10, 6, 10)));

        panelFields.add(lblUser);
        panelFields.add(txtTenDangNhap);
        panelFields.add(lblPass);
        panelFields.add(txtMatKhau);

        // Nút
        JPanel panelNut = new JPanel(new GridLayout(1, 2, 10, 0));
        panelNut.setOpaque(false);
        panelNut.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNut.setMaximumSize(new Dimension(300, 38));

        JButton btnDangNhap = GiaoDienUtil.taoNut("Đăng nhập", GiaoDienUtil.MAU_CHINH);
        JButton btnThoat    = GiaoDienUtil.taoNut("Thoát",     GiaoDienUtil.MAU_XAM);

        panelNut.add(btnDangNhap);
        panelNut.add(btnThoat);

        getRootPane().setDefaultButton(btnDangNhap);
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        btnThoat.addActionListener(e -> System.exit(0));

        card.add(lblLogo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblTieuDe);
        card.add(Box.createVerticalStrut(4));
        card.add(lblMoTa);
        card.add(Box.createVerticalStrut(20));
        card.add(panelFields);
        card.add(Box.createVerticalStrut(16));
        card.add(panelNut);

        panelNen.add(card);
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            NguoiDung nguoiDung = nguoiDungDAO.dangNhap(tenDangNhap, matKhau);
            if (nguoiDung == null) {
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu.",
                        "Đăng nhập thất bại", JOptionPane.WARNING_MESSAGE);
                txtMatKhau.setText("");
                txtMatKhau.requestFocus();
                return;
            }

            TrangChuForm trangChuForm = new TrangChuForm(nguoiDung);
            trangChuForm.setVisible(true);
            dispose();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage() + "\nHãy kiểm tra MySQL và cấu hình kết nối.",
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }
}
