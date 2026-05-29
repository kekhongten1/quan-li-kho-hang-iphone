package giao_dien.panel;

import dao.ThongKeDAO;
import model.SanPham;
import model.ThongKeKho;
import util.GiaoDienUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TrangChuPanel extends JPanel implements LamMoiDuLieu {
    private static final int NGUONG_CANH_BAO = 5;

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final DefaultTableModel modelBang = new DefaultTableModel();

    private JLabel lblTongSanPham;
    private JLabel lblTongTonKho;
    private JLabel lblSapHetHang;
    private JLabel lblGiaTriTonKho;

    public TrangChuPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(GiaoDienUtil.MAU_NEN_CHAN);

        add(taoHeader(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
    }

    private JPanel taoHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblTieuDe = new JLabel("Trang chủ");
        lblTieuDe.setFont(GiaoDienUtil.FONT_TIEU_DE);
        lblTieuDe.setForeground(new Color(15, 23, 42));
        panel.add(lblTieuDe, BorderLayout.WEST);

        JButton btnTaiLai = GiaoDienUtil.taoNut("↻  Cập nhật", GiaoDienUtil.MAU_XAM);
        btnTaiLai.addActionListener(e -> lamMoiDuLieu());
        panel.add(btnTaiLai, BorderLayout.EAST);

        return panel;
    }

    private JPanel taoNoiDung() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        JPanel panelThe = new JPanel(new GridLayout(1, 4, 14, 0));
        panelThe.setOpaque(false);
        lblTongSanPham  = taoThe(panelThe, "Tổng sản phẩm",    "📋", new Color(37, 99, 235));
        lblTongTonKho   = taoThe(panelThe, "Tổng tồn kho",     "📦", new Color(5, 150, 105));
        lblSapHetHang   = taoThe(panelThe, "Sắp hết hàng",     "⚠️",  new Color(217, 119, 6));
        lblGiaTriTonKho = taoThe(panelThe, "Giá trị tồn kho",  "💰", new Color(185, 28, 28));

        panel.add(panelThe, BorderLayout.NORTH);

        modelBang.setColumnIdentifiers(new String[]{"Mã SP", "Tên sản phẩm", "Dòng máy", "Tồn kho", "Trạng thái"});
        JTable table = new JTable(modelBang);
        GiaoDienUtil.caiThienBang(table);

        JPanel panelCanhBao = new JPanel(new BorderLayout(10, 10));
        panelCanhBao.setOpaque(false);
        panelCanhBao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(GiaoDienUtil.MAU_VIEN),
                        "Danh sách sản phẩm cần cảnh báo tồn kho"),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        panelCanhBao.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(panelCanhBao, BorderLayout.CENTER);
        return panel;
    }

    private JLabel taoThe(JPanel panelCha, String tieuDe, String icon, Color mauNen) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(mauNen);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel lblIcon = new JLabel(icon + "  " + tieuDe, SwingConstants.LEFT);
        lblIcon.setForeground(new Color(255, 255, 255, 200));
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        JLabel lblGiaTri = new JLabel("0", SwingConstants.LEFT);
        lblGiaTri.setForeground(Color.WHITE);
        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 30));

        panel.add(lblIcon,   BorderLayout.NORTH);
        panel.add(lblGiaTri, BorderLayout.CENTER);
        panelCha.add(panel);
        return lblGiaTri;
    }

    @Override
    public void lamMoiDuLieu() {
        try {
            ThongKeKho thongKe = thongKeDAO.layThongKeTongQuan(NGUONG_CANH_BAO);
            lblTongSanPham.setText(String.valueOf(thongKe.getTongSanPham()));
            lblTongTonKho.setText(String.valueOf(thongKe.getTongTonKho()));
            lblSapHetHang.setText(String.valueOf(thongKe.getSanPhamSapHet()));
            lblGiaTriTonKho.setText(String.format("%,.0f ₫", thongKe.getGiaTriTonKho()));

            modelBang.setRowCount(0);
            ArrayList<SanPham> danhSach = thongKeDAO.laySanPhamSapHetVaHetHang();
            for (SanPham sp : danhSach) {
                modelBang.addRow(new Object[]{
                        sp.getMaSanPham(), sp.getTenSanPham(),
                        sp.getDongMay(), sp.getSoLuongTon(), sp.getTrangThai()
                });
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
