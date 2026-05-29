package util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public final class GiaoDienUtil {

    // Bảng màu chủ đạo
    public static final Color MAU_CHINH     = new Color(37, 99, 235);   // xanh dương
    public static final Color MAU_XANH_LA   = new Color(22, 163, 74);   // duyệt
    public static final Color MAU_DO        = new Color(185, 28, 28);   // hủy / xóa
    public static final Color MAU_XAM       = new Color(75, 85, 99);    // trung tính
    public static final Color MAU_CAM       = new Color(217, 119, 6);   // cảnh báo
    public static final Color MAU_SIDEBAR   = new Color(15, 23, 42);
    public static final Color MAU_SIDEBAR_NUT = new Color(30, 41, 59);
    public static final Color MAU_NEN_CHAN   = new Color(248, 250, 252);
    public static final Color MAU_VIEN      = new Color(226, 232, 240);
    public static final Color MAU_DONG_LE   = new Color(241, 245, 249);

    public static final Font FONT_TIEU_DE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_NHAN     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BANG     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_NUT      = new Font("Segoe UI", Font.BOLD, 13);

    private GiaoDienUtil() {}

    public static JButton taoNut(String ten, Color mauNen) {
        JButton btn = new JButton(ten);
        btn.setFont(FONT_NUT);
        btn.setBackground(mauNen);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }

    public static void caiThienBang(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BANG);
        table.setGridColor(MAU_VIEN);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(8, 2));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(new Color(51, 65, 85));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : MAU_DONG_LE);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }
}
