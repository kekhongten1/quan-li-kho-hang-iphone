package dao;

import model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SanPhamDAO {
    public static final String TRANG_THAI_DANG_KINH_DOANH = "Dang kinh doanh";
    public static final String TRANG_THAI_SAP_HET_HANG = "Sap het hang";
    public static final String TRANG_THAI_HET_HANG = "Het hang";
    public static final String TRANG_THAI_TAM_NGUNG = "Tam ngung";

    public ArrayList<SanPham> layDanhSachSanPham() {
        String sql = "SELECT * FROM san_pham ORDER BY id DESC";
        ArrayList<SanPham> danhSach = new ArrayList<>();

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            dongBoTrangThaiTuDong(conn);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docSanPham(rs));
                }
            }

            return danhSach;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai danh sach san pham.", e);
        }
    }

    public ArrayList<SanPham> timKiemSanPham(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return layDanhSachSanPham();
        }

        String sql = "SELECT * FROM san_pham "
                + "WHERE ma_san_pham LIKE ? OR ten_san_pham LIKE ? OR dong_may LIKE ? "
                + "ORDER BY id DESC";
        ArrayList<SanPham> danhSach = new ArrayList<>();
        String giaTriTim = "%" + tuKhoa.trim() + "%";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            dongBoTrangThaiTuDong(conn);
            ps.setString(1, giaTriTim);
            ps.setString(2, giaTriTim);
            ps.setString(3, giaTriTim);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docSanPham(rs));
                }
            }

            return danhSach;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tim kiem san pham.", e);
        }
    }

    public boolean themSanPham(SanPham sanPham) {
        String sql = "INSERT INTO san_pham(ma_san_pham, ten_san_pham, dong_may, mau_sac, dung_luong, "
                + "gia_nhap, gia_ban, so_luong_ton, trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ganGiaTriSanPham(ps, sanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean suaSanPham(SanPham sanPham) {
        String sql = "UPDATE san_pham SET ten_san_pham=?, dong_may=?, mau_sac=?, dung_luong=?, "
                + "gia_nhap=?, gia_ban=?, so_luong_ton=?, trang_thai=? WHERE ma_san_pham=?";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sanPham.getTenSanPham());
            ps.setString(2, sanPham.getDongMay());
            ps.setString(3, sanPham.getMauSac());
            ps.setString(4, sanPham.getDungLuong());
            ps.setDouble(5, sanPham.getGiaNhap());
            ps.setDouble(6, sanPham.getGiaBan());
            ps.setInt(7, sanPham.getSoLuongTon());
            ps.setString(8, xacDinhTrangThaiSanPham(sanPham.getSoLuongTon(), sanPham.getTrangThai()));
            ps.setString(9, sanPham.getMaSanPham());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean xoaSanPham(String maSanPham) {
        String sql = "DELETE FROM san_pham WHERE ma_san_pham=?";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSanPham);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public void dongBoTrangThaiTuDong(Connection conn) throws SQLException {
        String sql = "UPDATE san_pham SET trang_thai = CASE "
                + "WHEN so_luong_ton = 0 THEN ? "
                + "WHEN so_luong_ton < 5 THEN ? "
                + "ELSE ? END "
                + "WHERE trang_thai <> ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TRANG_THAI_HET_HANG);
            ps.setString(2, TRANG_THAI_SAP_HET_HANG);
            ps.setString(3, TRANG_THAI_DANG_KINH_DOANH);
            ps.setString(4, TRANG_THAI_TAM_NGUNG);
            ps.executeUpdate();
        }
    }

    public void capNhatTrangThaiSauBienDongTonKho(Connection conn, int sanPhamId) throws SQLException {
        String sql = "UPDATE san_pham SET trang_thai = CASE "
                + "WHEN trang_thai = ? THEN ? "
                + "WHEN so_luong_ton = 0 THEN ? "
                + "WHEN so_luong_ton < 5 THEN ? "
                + "ELSE ? END "
                + "WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, TRANG_THAI_TAM_NGUNG);
            ps.setString(2, TRANG_THAI_TAM_NGUNG);
            ps.setString(3, TRANG_THAI_HET_HANG);
            ps.setString(4, TRANG_THAI_SAP_HET_HANG);
            ps.setString(5, TRANG_THAI_DANG_KINH_DOANH);
            ps.setInt(6, sanPhamId);
            ps.executeUpdate();
        }
    }

    public String xacDinhTrangThaiSanPham(int soLuongTon, String trangThaiDuocChon) {
        if (TRANG_THAI_TAM_NGUNG.equalsIgnoreCase(trangThaiDuocChon)) {
            return TRANG_THAI_TAM_NGUNG;
        }
        return xacDinhTrangThaiTuDong(soLuongTon);
    }

    public static String xacDinhTrangThaiTuDong(int soLuongTon) {
        if (soLuongTon <= 0) {
            return TRANG_THAI_HET_HANG;
        }
        if (soLuongTon < 5) {
            return TRANG_THAI_SAP_HET_HANG;
        }
        return TRANG_THAI_DANG_KINH_DOANH;
    }

    private void ganGiaTriSanPham(PreparedStatement ps, SanPham sanPham) throws SQLException {
        ps.setString(1, sanPham.getMaSanPham());
        ps.setString(2, sanPham.getTenSanPham());
        ps.setString(3, sanPham.getDongMay());
        ps.setString(4, sanPham.getMauSac());
        ps.setString(5, sanPham.getDungLuong());
        ps.setDouble(6, sanPham.getGiaNhap());
        ps.setDouble(7, sanPham.getGiaBan());
        ps.setInt(8, sanPham.getSoLuongTon());
        ps.setString(9, xacDinhTrangThaiSanPham(sanPham.getSoLuongTon(), sanPham.getTrangThai()));
    }

    private SanPham docSanPham(ResultSet rs) throws SQLException {
        return new SanPham(
                rs.getInt("id"),
                rs.getString("ma_san_pham"),
                rs.getString("ten_san_pham"),
                rs.getString("dong_may"),
                rs.getString("mau_sac"),
                rs.getString("dung_luong"),
                rs.getDouble("gia_nhap"),
                rs.getDouble("gia_ban"),
                rs.getInt("so_luong_ton"),
                rs.getString("trang_thai")
        );
    }
}
