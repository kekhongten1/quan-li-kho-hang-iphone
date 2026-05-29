package dao;

import model.BanGhiNhapKho;
import model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NhapKhoDAO {
    public static final String TRANG_THAI_CHO_DUYET = "Cho duyet";
    public static final String TRANG_THAI_DA_DUYET = "Da duyet";
    public static final String TRANG_THAI_DA_HUY = "Da huy";

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public String taoMaPhieuMoi() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS so_thu_tu FROM phieu_nhap";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format("PN%05d", rs.getInt("so_thu_tu"));
            }
            return "PN00001";
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tao ma phieu nhap moi.", e);
        }
    }

    public boolean taoPhieuNhap(String maPhieu, int nguoiDungId, String ghiChu, SanPham sanPham,
                                int soLuongNhap, boolean tuDongDuyet) {
        String sqlPhieuNhap = "INSERT INTO phieu_nhap(ma_phieu, ngay_nhap, nguoi_dung_id, ghi_chu, trang_thai, "
                + "nguoi_duyet_id, ngay_duyet, ghi_chu_duyet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chi_tiet_phieu_nhap(phieu_nhap_id, san_pham_id, so_luong, don_gia) VALUES (?, ?, ?, ?)";
        String sqlCapNhatTon = "UPDATE san_pham SET so_luong_ton = so_luong_ton + ?, gia_nhap = ? WHERE id = ?";

        try (Connection conn = DBKetNoi.layKetNoi()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psPhieu = conn.prepareStatement(sqlPhieuNhap, Statement.RETURN_GENERATED_KEYS)) {
                LocalDateTime thoiDiemXuLy = LocalDateTime.now();
                String trangThai = tuDongDuyet ? TRANG_THAI_DA_DUYET : TRANG_THAI_CHO_DUYET;

                psPhieu.setString(1, maPhieu);
                psPhieu.setTimestamp(2, Timestamp.valueOf(thoiDiemXuLy));
                psPhieu.setInt(3, nguoiDungId);
                psPhieu.setString(4, ghiChu);
                psPhieu.setString(5, trangThai);
                if (tuDongDuyet) {
                    psPhieu.setInt(6, nguoiDungId);
                    psPhieu.setTimestamp(7, Timestamp.valueOf(thoiDiemXuLy));
                } else {
                    psPhieu.setNull(6, java.sql.Types.INTEGER);
                    psPhieu.setNull(7, java.sql.Types.TIMESTAMP);
                }
                psPhieu.setString(8, null);
                psPhieu.executeUpdate();

                int phieuNhapId;
                try (ResultSet keys = psPhieu.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return false;
                    }
                    phieuNhapId = keys.getInt(1);
                }

                try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet)) {
                    psChiTiet.setInt(1, phieuNhapId);
                    psChiTiet.setInt(2, sanPham.getId());
                    psChiTiet.setInt(3, soLuongNhap);
                    psChiTiet.setDouble(4, sanPham.getGiaNhap());
                    psChiTiet.executeUpdate();
                }

                if (tuDongDuyet) {
                    try (PreparedStatement psCapNhat = conn.prepareStatement(sqlCapNhatTon)) {
                        psCapNhat.setInt(1, soLuongNhap);
                        psCapNhat.setDouble(2, sanPham.getGiaNhap());
                        psCapNhat.setInt(3, sanPham.getId());
                        psCapNhat.executeUpdate();
                    }
                    sanPhamDAO.capNhatTrangThaiSauBienDongTonKho(conn, sanPham.getId());
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the luu phieu nhap kho.", e);
        }
    }

    public boolean duyetPhieuNhap(String maPhieu, int nguoiDuyetId) {
        String sqlLayPhieu = "SELECT pn.id, ct.san_pham_id, ct.so_luong, ct.don_gia "
                + "FROM phieu_nhap pn "
                + "JOIN chi_tiet_phieu_nhap ct ON ct.phieu_nhap_id = pn.id "
                + "WHERE pn.ma_phieu = ? AND pn.trang_thai = ? FOR UPDATE";
        String sqlCapNhatTon = "UPDATE san_pham SET so_luong_ton = so_luong_ton + ?, gia_nhap = ? WHERE id = ?";
        String sqlCapNhatPhieu = "UPDATE phieu_nhap SET trang_thai = ?, nguoi_duyet_id = ?, ngay_duyet = ?, "
                + "ghi_chu_duyet = ? WHERE id = ?";

        try (Connection conn = DBKetNoi.layKetNoi()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psLay = conn.prepareStatement(sqlLayPhieu)) {
                psLay.setString(1, maPhieu);
                psLay.setString(2, TRANG_THAI_CHO_DUYET);

                int phieuId;
                int sanPhamId;
                int soLuong;
                double donGia;

                try (ResultSet rs = psLay.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    phieuId = rs.getInt("id");
                    sanPhamId = rs.getInt("san_pham_id");
                    soLuong = rs.getInt("so_luong");
                    donGia = rs.getDouble("don_gia");
                }

                try (PreparedStatement psCapNhatTon = conn.prepareStatement(sqlCapNhatTon);
                     PreparedStatement psCapNhatPhieu = conn.prepareStatement(sqlCapNhatPhieu)) {

                    psCapNhatTon.setInt(1, soLuong);
                    psCapNhatTon.setDouble(2, donGia);
                    psCapNhatTon.setInt(3, sanPhamId);
                    psCapNhatTon.executeUpdate();
                    sanPhamDAO.capNhatTrangThaiSauBienDongTonKho(conn, sanPhamId);

                    psCapNhatPhieu.setString(1, TRANG_THAI_DA_DUYET);
                    psCapNhatPhieu.setInt(2, nguoiDuyetId);
                    psCapNhatPhieu.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    psCapNhatPhieu.setString(4, null);
                    psCapNhatPhieu.setInt(5, phieuId);
                    psCapNhatPhieu.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the duyet phieu nhap.", e);
        }
    }

    public boolean huyPhieuNhap(String maPhieu, int nguoiDuyetId, String ghiChuDuyet) {
        String sql = "UPDATE phieu_nhap SET trang_thai = ?, nguoi_duyet_id = ?, ngay_duyet = ?, ghi_chu_duyet = ? "
                + "WHERE ma_phieu = ? AND trang_thai = ?";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TRANG_THAI_DA_HUY);
            ps.setInt(2, nguoiDuyetId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, ghiChuDuyet);
            ps.setString(5, maPhieu);
            ps.setString(6, TRANG_THAI_CHO_DUYET);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the huy phieu nhap.", e);
        }
    }

    public ArrayList<BanGhiNhapKho> layDanhSachPhieuNhap() {
        String sql = "SELECT pn.ma_phieu, pn.ngay_nhap, sp.ma_san_pham, sp.ten_san_pham, "
                + "ct.so_luong, ct.don_gia, nd.ho_ten AS nguoi_tao, pn.ghi_chu, pn.trang_thai, "
                + "nd_duyet.ho_ten AS nguoi_duyet, pn.ngay_duyet, pn.ghi_chu_duyet "
                + "FROM chi_tiet_phieu_nhap ct "
                + "JOIN phieu_nhap pn ON pn.id = ct.phieu_nhap_id "
                + "JOIN san_pham sp ON sp.id = ct.san_pham_id "
                + "JOIN nguoi_dung nd ON nd.id = pn.nguoi_dung_id "
                + "LEFT JOIN nguoi_dung nd_duyet ON nd_duyet.id = pn.nguoi_duyet_id "
                + "ORDER BY CASE WHEN pn.trang_thai = ? THEN 0 ELSE 1 END, pn.ngay_nhap DESC, pn.id DESC";

        ArrayList<BanGhiNhapKho> danhSach = new ArrayList<>();

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TRANG_THAI_CHO_DUYET);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ngayDuyet = rs.getTimestamp("ngay_duyet");
                    danhSach.add(new BanGhiNhapKho(
                            rs.getString("ma_phieu"),
                            rs.getTimestamp("ngay_nhap").toLocalDateTime(),
                            rs.getString("ma_san_pham"),
                            rs.getString("ten_san_pham"),
                            rs.getInt("so_luong"),
                            rs.getDouble("don_gia"),
                            rs.getString("nguoi_tao"),
                            rs.getString("ghi_chu"),
                            rs.getString("trang_thai"),
                            rs.getString("nguoi_duyet"),
                            ngayDuyet == null ? null : ngayDuyet.toLocalDateTime(),
                            rs.getString("ghi_chu_duyet")
                    ));
                }
            }

            return danhSach;
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tai lich su nhap kho.", e);
        }
    }
}
