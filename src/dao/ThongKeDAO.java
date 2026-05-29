package dao;

import model.SanPham;
import model.ThongKeKho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ThongKeDAO {
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public ThongKeKho layThongKeTongQuan(int nguongSapHet) {
        String sqlSanPham = "SELECT COUNT(*) AS tong_san_pham, COALESCE(SUM(so_luong_ton), 0) AS tong_ton_kho, "
                + "COALESCE(SUM(CASE WHEN trang_thai = ? THEN 1 ELSE 0 END), 0) AS san_pham_sap_het, "
                + "COALESCE(SUM(so_luong_ton * gia_nhap), 0) AS gia_tri_ton_kho FROM san_pham";
        String sqlNhap = "SELECT COALESCE(SUM(ct.so_luong), 0) AS tong_nhap "
                + "FROM chi_tiet_phieu_nhap ct "
                + "JOIN phieu_nhap pn ON pn.id = ct.phieu_nhap_id "
                + "WHERE pn.trang_thai = ?";
        String sqlXuat = "SELECT COALESCE(SUM(ct.so_luong), 0) AS tong_xuat "
                + "FROM chi_tiet_phieu_xuat ct "
                + "JOIN phieu_xuat px ON px.id = ct.phieu_xuat_id "
                + "WHERE px.trang_thai = ?";

        try (Connection conn = DBKetNoi.layKetNoi()) {
            sanPhamDAO.dongBoTrangThaiTuDong(conn);

            ThongKeKho thongKe = new ThongKeKho();

            try (PreparedStatement ps = conn.prepareStatement(sqlSanPham)) {
                ps.setString(1, SanPhamDAO.TRANG_THAI_SAP_HET_HANG);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        thongKe.setTongSanPham(rs.getInt("tong_san_pham"));
                        thongKe.setTongTonKho(rs.getInt("tong_ton_kho"));
                        thongKe.setSanPhamSapHet(rs.getInt("san_pham_sap_het"));
                        thongKe.setGiaTriTonKho(rs.getDouble("gia_tri_ton_kho"));
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlNhap)) {
                ps.setString(1, NhapKhoDAO.TRANG_THAI_DA_DUYET);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        thongKe.setTongDaNhap(rs.getInt("tong_nhap"));
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlXuat)) {
                ps.setString(1, XuatKhoDAO.TRANG_THAI_DA_DUYET);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        thongKe.setTongDaXuat(rs.getInt("tong_xuat"));
                    }
                }
            }

            return thongKe;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai thong ke tong quan.", e);
        }
    }

    public ArrayList<SanPham> laySanPhamConTrongKho() {
        return layDanhSachSanPhamTheoDieuKien("SELECT * FROM san_pham WHERE so_luong_ton > 0 ORDER BY so_luong_ton ASC, id DESC");
    }

    public ArrayList<SanPham> laySanPhamSapHetVaHetHang() {
        String sql = "SELECT * FROM san_pham WHERE trang_thai IN (?, ?) ORDER BY so_luong_ton ASC, id DESC";
        ArrayList<SanPham> danhSach = new ArrayList<>();

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            sanPhamDAO.dongBoTrangThaiTuDong(conn);
            ps.setString(1, SanPhamDAO.TRANG_THAI_SAP_HET_HANG);
            ps.setString(2, SanPhamDAO.TRANG_THAI_HET_HANG);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docSanPham(rs));
                }
            }

            return danhSach;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai danh sach san pham sap het va het hang.", e);
        }
    }

    public ArrayList<SanPham> laySanPhamTamNgung() {
        String sql = "SELECT * FROM san_pham WHERE trang_thai = ? ORDER BY id DESC";
        ArrayList<SanPham> danhSach = new ArrayList<>();

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            sanPhamDAO.dongBoTrangThaiTuDong(conn);
            ps.setString(1, SanPhamDAO.TRANG_THAI_TAM_NGUNG);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docSanPham(rs));
                }
            }

            return danhSach;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai danh sach san pham tam ngung.", e);
        }
    }

    private ArrayList<SanPham> layDanhSachSanPhamTheoDieuKien(String sql) {
        ArrayList<SanPham> danhSach = new ArrayList<>();

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            sanPhamDAO.dongBoTrangThaiTuDong(conn);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(docSanPham(rs));
                }
            }

            return danhSach;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai danh sach san pham ton kho.", e);
        }
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
