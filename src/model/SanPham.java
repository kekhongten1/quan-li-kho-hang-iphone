package model;

public class SanPham {
    private int id;
    private String maSanPham;
    private String tenSanPham;
    private String dongMay;
    private String mauSac;
    private String dungLuong;
    private double giaNhap;
    private double giaBan;
    private int soLuongTon;
    private String trangThai;

    public SanPham() {
    }

    public SanPham(int id, String maSanPham, String tenSanPham, String dongMay, String mauSac,
                   String dungLuong, double giaNhap, double giaBan, int soLuongTon, String trangThai) {
        this.id = id;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.dongMay = dongMay;
        this.mauSac = mauSac;
        this.dungLuong = dungLuong;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.trangThai = trangThai;
    }

    public SanPham(String maSanPham, String tenSanPham, String dongMay, String mauSac,
                   String dungLuong, double giaNhap, double giaBan, int soLuongTon, String trangThai) {
        this(0, maSanPham, tenSanPham, dongMay, mauSac, dungLuong, giaNhap, giaBan, soLuongTon, trangThai);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getDongMay() {
        return dongMay;
    }

    public void setDongMay(String dongMay) {
        this.dongMay = dongMay;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public String getDungLuong() {
        return dungLuong;
    }

    public void setDungLuong(String dungLuong) {
        this.dungLuong = dungLuong;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
