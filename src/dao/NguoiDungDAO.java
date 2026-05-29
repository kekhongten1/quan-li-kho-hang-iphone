package dao;

import model.NguoiDung;
import util.MatKhauUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NguoiDungDAO {

    public NguoiDung dangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM nguoi_dung WHERE ten_dang_nhap = ? AND mat_khau = ?";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ps.setString(2, MatKhauUtil.maHoa(matKhau));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NguoiDung(
                            rs.getInt("id"),
                            rs.getString("ten_dang_nhap"),
                            rs.getString("mat_khau"),
                            rs.getString("vai_tro"),
                            rs.getString("ho_ten")
                    );
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the ket noi CSDL de thuc hien dang nhap.", e);
        }
    }
}
