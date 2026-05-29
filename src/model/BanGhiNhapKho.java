package model;

import java.time.LocalDateTime;

public class BanGhiNhapKho {
    private String maPhieu;
    private LocalDateTime ngayNhap;
    private String maSanPham;
    private String tenSanPham;
    private int soLuong;
    private double donGia;
    private String nguoiTao;
    private String ghiChu;
    private String trangThai;
    private String nguoiDuyet;
    private LocalDateTime ngayDuyet;
    private String ghiChuDuyet;

    public BanGhiNhapKho(String maPhieu, LocalDateTime ngayNhap, String maSanPham, String tenSanPham,
                         int soLuong, double donGia, String nguoiTao, String ghiChu,
                         String trangThai, String nguoiDuyet, LocalDateTime ngayDuyet, String ghiChuDuyet) {
        this.maPhieu = maPhieu;
        this.ngayNhap = ngayNhap;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.nguoiTao = nguoiTao;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
        this.nguoiDuyet = nguoiDuyet;
        this.ngayDuyet = ngayDuyet;
        this.ghiChuDuyet = ghiChuDuyet;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public LocalDateTime getNgayNhap() {
        return ngayNhap;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public double getThanhTien() {
        return donGia * soLuong;
    }

    public String getNguoiTao() {
        return nguoiTao;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getNguoiDuyet() {
        return nguoiDuyet;
    }

    public LocalDateTime getNgayDuyet() {
        return ngayDuyet;
    }

    public String getGhiChuDuyet() {
        return ghiChuDuyet;
    }
}
