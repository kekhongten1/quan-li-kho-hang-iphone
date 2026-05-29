package model;

public class NguoiDung {
    private int id;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private String hoTen;

    public NguoiDung(int id, String tenDangNhap, String matKhau, String vaiTro, String hoTen) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.hoTen = hoTen;
    }

    public int getId() {
        return id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public String getHoTen() {
        return hoTen;
    }

    public boolean laAdmin() {
        return vaiTro != null && vaiTro.trim().equalsIgnoreCase("Admin");
    }

    public boolean laQuanLy() {
        if (vaiTro == null) {
            return false;
        }

        String vaiTroChuanHoa = vaiTro.trim();
        return vaiTroChuanHoa.equalsIgnoreCase("Admin")
                || vaiTroChuanHoa.equalsIgnoreCase("Quan ly")
                || vaiTroChuanHoa.equalsIgnoreCase("Quan tri vien")
                || vaiTroChuanHoa.equalsIgnoreCase("Manager");
    }
}
