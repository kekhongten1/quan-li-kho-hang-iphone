package main;

import giao_dien.DangNhapForm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            DangNhapForm dangNhapForm = new DangNhapForm();
            dangNhapForm.setVisible(true);
        });
    }
}
