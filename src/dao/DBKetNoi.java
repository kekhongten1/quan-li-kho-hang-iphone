package dao;

import util.MatKhauUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public final class DBKetNoi {
    private static final Map<String, String> ENV = System.getenv();
    private static final String HOST = ENV.getOrDefault("QLKH_DB_HOST", "localhost");
    private static final String PORT = ENV.getOrDefault("QLKH_DB_PORT", "3306");
    private static final String DATABASE = ENV.getOrDefault("QLKH_DB_NAME", "quan_li_kho_iphone");
    private static final String USER = ENV.getOrDefault("QLKH_DB_USER", "root");
    private static final String PASSWORD = ENV.getOrDefault("QLKH_DB_PASSWORD", "123456");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok&useUnicode=true&characterEncoding=UTF-8";
    private static volatile boolean daDamBaoCauTrucDuLieu = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Khong tim thay thu vien MySQL Connector/J trong classpath.", e);
        }
    }

    private DBKetNoi() {
    }

    public static Connection layKetNoi() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        damBaoCauTrucDuLieu(conn);
        return conn;
    }

    private static void damBaoCauTrucDuLieu(Connection conn) throws SQLException {
        if (daDamBaoCauTrucDuLieu) {
            return;
        }

        synchronized (DBKetNoi.class) {
            if (daDamBaoCauTrucDuLieu) {
                return;
            }

            boolean phieuNhapDaCoTrangThai = coCot(conn, "phieu_nhap", "trang_thai");
            boolean phieuXuatDaCoTrangThai = coCot(conn, "phieu_xuat", "trang_thai");

            try (Statement stmt = conn.createStatement()) {
                themCotNeuChuaCo(conn, stmt, "phieu_nhap", "trang_thai",
                        "ALTER TABLE phieu_nhap ADD COLUMN trang_thai VARCHAR(30) NOT NULL DEFAULT 'Cho duyet'");
                themCotNeuChuaCo(conn, stmt, "phieu_nhap", "nguoi_duyet_id",
                        "ALTER TABLE phieu_nhap ADD COLUMN nguoi_duyet_id INT NULL");
                themCotNeuChuaCo(conn, stmt, "phieu_nhap", "ngay_duyet",
                        "ALTER TABLE phieu_nhap ADD COLUMN ngay_duyet DATETIME NULL");
                themCotNeuChuaCo(conn, stmt, "phieu_nhap", "ghi_chu_duyet",
                        "ALTER TABLE phieu_nhap ADD COLUMN ghi_chu_duyet VARCHAR(255) NULL");

                themCotNeuChuaCo(conn, stmt, "phieu_xuat", "trang_thai",
                        "ALTER TABLE phieu_xuat ADD COLUMN trang_thai VARCHAR(30) NOT NULL DEFAULT 'Cho duyet'");
                themCotNeuChuaCo(conn, stmt, "phieu_xuat", "nguoi_duyet_id",
                        "ALTER TABLE phieu_xuat ADD COLUMN nguoi_duyet_id INT NULL");
                themCotNeuChuaCo(conn, stmt, "phieu_xuat", "ngay_duyet",
                        "ALTER TABLE phieu_xuat ADD COLUMN ngay_duyet DATETIME NULL");
                themCotNeuChuaCo(conn, stmt, "phieu_xuat", "ghi_chu_duyet",
                        "ALTER TABLE phieu_xuat ADD COLUMN ghi_chu_duyet VARCHAR(255) NULL");

                if (!phieuNhapDaCoTrangThai) {
                    stmt.executeUpdate("UPDATE phieu_nhap SET trang_thai = 'Da duyet'");
                } else {
                    stmt.executeUpdate("UPDATE phieu_nhap SET trang_thai = 'Da duyet' "
                            + "WHERE trang_thai IS NULL OR trang_thai = ''");
                }

                if (!phieuXuatDaCoTrangThai) {
                    stmt.executeUpdate("UPDATE phieu_xuat SET trang_thai = 'Da duyet'");
                } else {
                    stmt.executeUpdate("UPDATE phieu_xuat SET trang_thai = 'Da duyet' "
                            + "WHERE trang_thai IS NULL OR trang_thai = ''");
                }

                stmt.executeUpdate("UPDATE san_pham SET trang_thai = "
                        + "CASE "
                        + "WHEN trang_thai = 'Tam ngung' THEN 'Tam ngung' "
                        + "WHEN so_luong_ton = 0 THEN 'Het hang' "
                        + "WHEN so_luong_ton < 5 THEN 'Sap het hang' "
                        + "ELSE 'Dang kinh doanh' END");
            }

            damBaoTaiKhoanMacDinh(conn);

            daDamBaoCauTrucDuLieu = true;
        }
    }

    private static void damBaoTaiKhoanMacDinh(Connection conn) throws SQLException {
        String sql = "INSERT INTO nguoi_dung(ten_dang_nhap, mat_khau, vai_tro, ho_ten) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "mat_khau = VALUES(mat_khau), vai_tro = VALUES(vai_tro), ho_ten = VALUES(ho_ten)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, MatKhauUtil.maHoa("123"));
            ps.setString(3, "Admin");
            ps.setString(4, "Quản trị viên");
            ps.executeUpdate();

            ps.setString(1, "quanly");
            ps.setString(2, MatKhauUtil.maHoa("123"));
            ps.setString(3, "Quan ly");
            ps.setString(4, "Quản lý kho");
            ps.executeUpdate();

            ps.setString(1, "nhanvien");
            ps.setString(2, MatKhauUtil.maHoa("123"));
            ps.setString(3, "Nhan vien");
            ps.setString(4, "Nhân viên kho");
            ps.executeUpdate();
        }
    }

    private static boolean coCot(Connection conn, String tenBang, String tenCot) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tenBang, tenCot)) {
            return rs.next();
        }
    }

    private static void themCotNeuChuaCo(Connection conn, Statement stmt, String tenBang, String tenCot, String sql)
            throws SQLException {
        if (!coCot(conn, tenBang, tenCot)) {
            stmt.executeUpdate(sql);
        }
    }
}
