package dao;

import model.BanGhiXuatKho;
import model.SanPham;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class XuatKhoDAO {
    public static final String TRANG_THAI_CHO_DUYET = "Cho duyet";
    public static final String TRANG_THAI_DA_DUYET = "Da duyet";
    public static final String TRANG_THAI_DA_HUY = "Da huy";

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    public String taoMaPhieuMoi() {
        String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS so_thu_tu FROM phieu_xuat";

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format("PX%05d", rs.getInt("so_thu_tu"));
            }
            return "PX00001";
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tao ma phieu xuat moi.", e);
        }
    }

    public boolean taoPhieuXuat(String maPhieu, int nguoiDungId, String ghiChu, SanPham sanPham,
                                int soLuongXuat, boolean tuDongDuyet) {
        String sqlTonKho = "SELECT so_luong_ton, trang_thai FROM san_pham WHERE id = ? FOR UPDATE";
        String sqlPhieuXuat = "INSERT INTO phieu_xuat(ma_phieu, ngay_xuat, nguoi_dung_id, ghi_chu, trang_thai, "
                + "nguoi_duyet_id, ngay_duyet, ghi_chu_duyet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlChiTiet = "INSERT INTO chi_tiet_phieu_xuat(phieu_xuat_id, san_pham_id, so_luong, don_gia) VALUES (?, ?, ?, ?)";
        String sqlCapNhatTon = "UPDATE san_pham SET so_luong_ton = so_luong_ton - ? WHERE id = ?";

        try (Connection conn = DBKetNoi.layKetNoi()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psTon = conn.prepareStatement(sqlTonKho)) {
                psTon.setInt(1, sanPham.getId());

                int tonKhoHienTai;
                String trangThaiHienTai;
                try (ResultSet rs = psTon.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    tonKhoHienTai = rs.getInt("so_luong_ton");
                    trangThaiHienTai = rs.getString("trang_thai");
                }

                if (!coTheXuatKho(trangThaiHienTai, tonKhoHienTai, soLuongXuat)) {
                    conn.rollback();
                    return false;
                }

                LocalDateTime thoiDiemXuLy = LocalDateTime.now();
                String trangThaiPhieu = tuDongDuyet ? TRANG_THAI_DA_DUYET : TRANG_THAI_CHO_DUYET;
                int phieuXuatId;

                try (PreparedStatement psPhieu = conn.prepareStatement(sqlPhieuXuat, Statement.RETURN_GENERATED_KEYS)) {
                    psPhieu.setString(1, maPhieu);
                    psPhieu.setTimestamp(2, Timestamp.valueOf(thoiDiemXuLy));
                    psPhieu.setInt(3, nguoiDungId);
                    psPhieu.setString(4, ghiChu);
                    psPhieu.setString(5, trangThaiPhieu);
                    if (tuDongDuyet) {
                        psPhieu.setInt(6, nguoiDungId);
                        psPhieu.setTimestamp(7, Timestamp.valueOf(thoiDiemXuLy));
                    } else {
                        psPhieu.setNull(6, java.sql.Types.INTEGER);
                        psPhieu.setNull(7, java.sql.Types.TIMESTAMP);
                    }
                    psPhieu.setString(8, null);
                    psPhieu.executeUpdate();

                    try (ResultSet keys = psPhieu.getGeneratedKeys()) {
                        if (!keys.next()) {
                            conn.rollback();
                            return false;
                        }
                        phieuXuatId = keys.getInt(1);
                    }
                }

                try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet)) {
                    psChiTiet.setInt(1, phieuXuatId);
                    psChiTiet.setInt(2, sanPham.getId());
                    psChiTiet.setInt(3, soLuongXuat);
                    psChiTiet.setDouble(4, sanPham.getGiaBan());
                    psChiTiet.executeUpdate();
                }

                if (tuDongDuyet) {
                    try (PreparedStatement psCapNhat = conn.prepareStatement(sqlCapNhatTon)) {
                        psCapNhat.setInt(1, soLuongXuat);
                        psCapNhat.setInt(2, sanPham.getId());
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
            throw new RuntimeException("Khong the luu phieu xuat kho.", e);
        }
    }

    public boolean duyetPhieuXuat(String maPhieu, int nguoiDuyetId) {
        String sqlLayPhieu = "SELECT px.id, ct.san_pham_id, ct.so_luong, sp.so_luong_ton, sp.trang_thai "
                + "FROM phieu_xuat px "
                + "JOIN chi_tiet_phieu_xuat ct ON ct.phieu_xuat_id = px.id "
                + "JOIN san_pham sp ON sp.id = ct.san_pham_id "
                + "WHERE px.ma_phieu = ? AND px.trang_thai = ? FOR UPDATE";
        String sqlCapNhatTon = "UPDATE san_pham SET so_luong_ton = so_luong_ton - ? WHERE id = ?";
        String sqlCapNhatPhieu = "UPDATE phieu_xuat SET trang_thai = ?, nguoi_duyet_id = ?, ngay_duyet = ?, "
                + "ghi_chu_duyet = ? WHERE id = ?";

        try (Connection conn = DBKetNoi.layKetNoi()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psLay = conn.prepareStatement(sqlLayPhieu)) {
                psLay.setString(1, maPhieu);
                psLay.setString(2, TRANG_THAI_CHO_DUYET);

                int phieuId;
                int sanPhamId;
                int soLuong;
                int tonKhoHienTai;
                String trangThaiHienTai;

                try (ResultSet rs = psLay.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    phieuId = rs.getInt("id");
                    sanPhamId = rs.getInt("san_pham_id");
                    soLuong = rs.getInt("so_luong");
                    tonKhoHienTai = rs.getInt("so_luong_ton");
                    trangThaiHienTai = rs.getString("trang_thai");
                }

                if (!coTheXuatKho(trangThaiHienTai, tonKhoHienTai, soLuong)) {
                    conn.rollback();
                    return false;
                }

                try (PreparedStatement psCapNhatTon = conn.prepareStatement(sqlCapNhatTon);
                     PreparedStatement psCapNhatPhieu = conn.prepareStatement(sqlCapNhatPhieu)) {

                    psCapNhatTon.setInt(1, soLuong);
                    psCapNhatTon.setInt(2, sanPhamId);
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
            throw new RuntimeException("Khong the duyet phieu xuat.", e);
        }
    }

    public boolean huyPhieuXuat(String maPhieu, int nguoiDuyetId, String ghiChuDuyet) {
        String sql = "UPDATE phieu_xuat SET trang_thai = ?, nguoi_duyet_id = ?, ngay_duyet = ?, ghi_chu_duyet = ? "
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
            throw new RuntimeException("Khong the huy phieu xuat.", e);
        }
    }

    public ArrayList<BanGhiXuatKho> layDanhSachPhieuXuat() {
        String sql = "SELECT px.ma_phieu, px.ngay_xuat, sp.ma_san_pham, sp.ten_san_pham, "
                + "ct.so_luong, ct.don_gia, nd.ho_ten AS nguoi_tao, px.ghi_chu, px.trang_thai, "
                + "nd_duyet.ho_ten AS nguoi_duyet, px.ngay_duyet, px.ghi_chu_duyet "
                + "FROM chi_tiet_phieu_xuat ct "
                + "JOIN phieu_xuat px ON px.id = ct.phieu_xuat_id "
                + "JOIN san_pham sp ON sp.id = ct.san_pham_id "
                + "JOIN nguoi_dung nd ON nd.id = px.nguoi_dung_id "
                + "LEFT JOIN nguoi_dung nd_duyet ON nd_duyet.id = px.nguoi_duyet_id "
                + "ORDER BY CASE WHEN px.trang_thai = ? THEN 0 ELSE 1 END, px.ngay_xuat DESC, px.id DESC";

        ArrayList<BanGhiXuatKho> danhSach = new ArrayList<>();

        try (Connection conn = DBKetNoi.layKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, TRANG_THAI_CHO_DUYET);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ngayDuyet = rs.getTimestamp("ngay_duyet");
                    danhSach.add(new BanGhiXuatKho(
                            rs.getString("ma_phieu"),
                            rs.getTimestamp("ngay_xuat").toLocalDateTime(),
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
            throw new RuntimeException("Khong the tai lich su xuat kho.", e);
        }
    }

    private boolean coTheXuatKho(String trangThaiHienTai, int tonKhoHienTai, int soLuongXuat) {
        if (SanPhamDAO.TRANG_THAI_TAM_NGUNG.equalsIgnoreCase(trangThaiHienTai)
                || SanPhamDAO.TRANG_THAI_HET_HANG.equalsIgnoreCase(trangThaiHienTai)) {
            return false;
        }
        return tonKhoHienTai > 0 && soLuongXuat > 0 && tonKhoHienTai >= soLuongXuat;
    }
}
