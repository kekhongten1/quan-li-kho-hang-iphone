CREATE DATABASE IF NOT EXISTS quan_li_kho_iphone
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE quan_li_kho_iphone;

CREATE TABLE IF NOT EXISTS nguoi_dung (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ten_dang_nhap VARCHAR(50) NOT NULL UNIQUE,
    mat_khau VARCHAR(100) NOT NULL,
    vai_tro VARCHAR(30) NOT NULL DEFAULT 'nhan_vien',
    ho_ten VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS san_pham (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ma_san_pham VARCHAR(30) NOT NULL UNIQUE,
    ten_san_pham VARCHAR(150) NOT NULL,
    dong_may VARCHAR(50) NOT NULL,
    mau_sac VARCHAR(50) NOT NULL,
    dung_luong VARCHAR(30) NOT NULL,
    gia_nhap DECIMAL(15,2) NOT NULL DEFAULT 0,
    gia_ban DECIMAL(15,2) NOT NULL DEFAULT 0,
    so_luong_ton INT NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'Dang kinh doanh'
);

CREATE TABLE IF NOT EXISTS phieu_nhap (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ma_phieu VARCHAR(30) NOT NULL UNIQUE,
    ngay_nhap DATETIME NOT NULL,
    nguoi_dung_id INT NOT NULL,
    ghi_chu VARCHAR(255),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'Cho duyet',
    nguoi_duyet_id INT NULL,
    ngay_duyet DATETIME NULL,
    ghi_chu_duyet VARCHAR(255) NULL,
    CONSTRAINT fk_phieu_nhap_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE IF NOT EXISTS chi_tiet_phieu_nhap (
    id INT PRIMARY KEY AUTO_INCREMENT,
    phieu_nhap_id INT NOT NULL,
    san_pham_id INT NOT NULL,
    so_luong INT NOT NULL,
    don_gia DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_ctpn_phieu_nhap
        FOREIGN KEY (phieu_nhap_id) REFERENCES phieu_nhap(id),
    CONSTRAINT fk_ctpn_san_pham
        FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE IF NOT EXISTS phieu_xuat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ma_phieu VARCHAR(30) NOT NULL UNIQUE,
    ngay_xuat DATETIME NOT NULL,
    nguoi_dung_id INT NOT NULL,
    ghi_chu VARCHAR(255),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'Cho duyet',
    nguoi_duyet_id INT NULL,
    ngay_duyet DATETIME NULL,
    ghi_chu_duyet VARCHAR(255) NULL,
    CONSTRAINT fk_phieu_xuat_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE IF NOT EXISTS chi_tiet_phieu_xuat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    phieu_xuat_id INT NOT NULL,
    san_pham_id INT NOT NULL,
    so_luong INT NOT NULL,
    don_gia DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_ctpx_phieu_xuat
        FOREIGN KEY (phieu_xuat_id) REFERENCES phieu_xuat(id),
    CONSTRAINT fk_ctpx_san_pham
        FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

-- Mật khẩu mặc định: 123 (SHA-256 hash)
INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau, vai_tro, ho_ten)
VALUES
    ('admin',     'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'Admin',     'Quản trị viên'),
    ('quanly',    'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'Quan ly',   'Quản lý kho'),
    ('nhanvien',  'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'Nhan vien', 'Nhân viên kho')
ON DUPLICATE KEY UPDATE
    mat_khau = VALUES(mat_khau),
    vai_tro = VALUES(vai_tro),
    ho_ten = VALUES(ho_ten);

INSERT INTO san_pham (ma_san_pham, ten_san_pham, dong_may, mau_sac, dung_luong, gia_nhap, gia_ban, so_luong_ton, trang_thai)
VALUES
    ('IP13-128-BL', 'iPhone 13 128GB Blue', 'iPhone 13', 'Blue', '128GB', 14000000, 15990000, 12, 'Dang kinh doanh'),
    ('IP14-128-BK', 'iPhone 14 128GB Black', 'iPhone 14', 'Black', '128GB', 17000000, 18990000, 8, 'Dang kinh doanh'),
    ('IP15-256-PK', 'iPhone 15 256GB Pink', 'iPhone 15', 'Pink', '256GB', 21500000, 23990000, 4, 'Sap het hang'),
    ('IP15PM-256-NT', 'iPhone 15 Pro Max 256GB Natural', 'iPhone 15 Pro Max', 'Natural Titanium', '256GB', 28500000, 31990000, 2, 'Sap het hang'),
    ('IP11-64-WH', 'iPhone 11 64GB White', 'iPhone 11', 'White', '64GB', 8500000, 9990000, 0, 'Het hang')
ON DUPLICATE KEY UPDATE
    ten_san_pham = VALUES(ten_san_pham),
    dong_may = VALUES(dong_may),
    mau_sac = VALUES(mau_sac),
    dung_luong = VALUES(dung_luong),
    gia_nhap = VALUES(gia_nhap),
    gia_ban = VALUES(gia_ban),
    so_luong_ton = VALUES(so_luong_ton),
    trang_thai = VALUES(trang_thai);