# 📦 Hệ Thống Quản Lý Kho Hàng iPhone

Ứng dụng desktop quản lý kho hàng iPhone được xây dựng bằng **Java Swing** kết hợp **MySQL**, áp dụng mô hình kiến trúc **MVC** (Model - View - Controller). Dự án mô phỏng quy trình quản lý kho thực tế với hệ thống phân quyền người dùng, luồng duyệt phiếu nhập/xuất và báo cáo thống kê tổng quan.

---

## 🛠️ Công nghệ sử dụng

| Thành phần | Chi tiết |
|---|---|
| Ngôn ngữ | Java (JDK 8+) |
| Giao diện | Java Swing |
| Cơ sở dữ liệu | MySQL 5.7+ / 8.0+ |
| Kết nối DB | JDBC — MySQL Connector/J 9.6.0 |
| Bảo mật | Mã hóa mật khẩu SHA-256 |
| Kiến trúc | MVC (Model - View - DAO) |

---

## ✨ Tính năng chính

### 🔐 Xác thực & Phân quyền
- Đăng nhập với tên đăng nhập và mật khẩu (mã hóa SHA-256)
- 3 vai trò người dùng:
  - **Admin** — toàn quyền
  - **Quản lý** — duyệt/hủy phiếu, quản lý sản phẩm, xem báo cáo
  - **Nhân viên** — tạo phiếu nhập/xuất, xem sản phẩm

### 📋 Quản lý sản phẩm
- Thêm, sửa, xóa sản phẩm iPhone (mã, tên, dòng máy, màu sắc, dung lượng, giá)
- Trạng thái tồn kho **tự động cập nhật** theo số lượng:
  - `Đang kinh doanh` — tồn kho > 5
  - `Sắp hết hàng` — tồn kho 1–4
  - `Hết hàng` — tồn kho = 0
  - `Tạm ngừng` — quản lý cập nhật thủ công
- Tìm kiếm sản phẩm theo từ khóa
- Nhân viên chỉ được xem, không được chỉnh sửa

### 📥 Nhập kho
- Tạo phiếu nhập với sản phẩm, số lượng, giá nhập tham chiếu
- Phiếu nhân viên tạo → **chờ quản lý duyệt**
- Phiếu quản lý tạo → **duyệt và cập nhật tồn kho ngay**
- Quản lý có thể duyệt hoặc hủy kèm ghi chú lý do
- Lịch sử phiếu có thể lọc theo trạng thái: Chờ duyệt / Đã duyệt / Đã hủy

### 📤 Xuất kho
- Tạo phiếu xuất với giá bán tham chiếu từ danh mục sản phẩm
- Validation nghiệp vụ:
  - Không cho xuất sản phẩm **tạm ngừng** hoặc **hết hàng**
  - Không cho xuất số lượng **vượt tồn kho** (không xuất âm tồn)
- Luồng duyệt tương tự nhập kho

### 📊 Thống kê & Báo cáo
- Tổng quan: tổng sản phẩm, tổng tồn kho, tổng đã nhập/xuất, giá trị tồn kho
- Danh sách sản phẩm sắp hết hàng / hết hàng
- Danh sách sản phẩm tạm ngừng kinh doanh
- Phiếu nhập/xuất đã duyệt gần đây
- Phiếu nhập/xuất đã hủy gần đây

---

## 🗂️ Cấu trúc dự án

```
quan_li_kho_hang_iphone/
├── src/
│   ├── main/
│   │   └── Main.java                   # Điểm khởi động ứng dụng
│   ├── dao/                            # Data Access Object (tầng truy cập DB)
│   │   ├── DBKetNoi.java               # Quản lý kết nối MySQL, tự khởi tạo schema
│   │   ├── NguoiDungDAO.java           # Xác thực người dùng
│   │   ├── SanPhamDAO.java             # Quản lý sản phẩm
│   │   ├── NhapKhoDAO.java             # Quản lý phiếu nhập
│   │   ├── XuatKhoDAO.java             # Quản lý phiếu xuất
│   │   └── ThongKeDAO.java             # Truy vấn thống kê
│   ├── model/                          # Entity classes
│   │   ├── NguoiDung.java
│   │   ├── SanPham.java
│   │   ├── BanGhiNhapKho.java
│   │   ├── BanGhiXuatKho.java
│   │   └── ThongKeKho.java
│   ├── giao_dien/                      # Tầng giao diện (View)
│   │   ├── DangNhapForm.java           # Màn hình đăng nhập
│   │   ├── TrangChuForm.java           # Cửa sổ chính (sidebar + CardLayout)
│   │   └── panel/
│   │       ├── TrangChuPanel.java      # Dashboard tổng quan
│   │       ├── QuanLiSanPhamPanel.java # Quản lý sản phẩm
│   │       ├── NhapKhoPanel.java       # Nhập kho
│   │       ├── XuatKhoPanel.java       # Xuất kho
│   │       ├── ThongKePanel.java       # Thống kê
│   │       └── LamMoiDuLieu.java       # Interface refresh dữ liệu
│   └── util/
│       ├── MatKhauUtil.java            # Mã hóa mật khẩu SHA-256
│       └── GiaoDienUtil.java           # Hằng số màu, font, helper UI
├── sql/
│   └── tao_co_so_du_lieu.sql           # Script tạo database và dữ liệu mẫu
├── lib/
│   └── mysql-connector-j-9.6.0.jar    # MySQL JDBC Driver
├── chay_windows.bat                    # Script biên dịch và chạy trên Windows
└── README.md
```

---

## ⚙️ Hướng dẫn cài đặt và chạy

### Yêu cầu
- **JDK 8** trở lên (đã cài `java` và `javac`)
- **MySQL 5.7+** hoặc **MySQL 8.0+**

### Bước 1 — Tạo cơ sở dữ liệu

Mở MySQL Workbench hoặc terminal MySQL, chạy file:

```sql
source sql/tao_co_so_du_lieu.sql
```

> Hoặc copy nội dung file SQL và chạy trực tiếp trong MySQL Workbench.

### Bước 2 — Cấu hình kết nối

Mặc định ứng dụng kết nối với:

| Tham số | Giá trị mặc định |
|---|---|
| Host | `localhost` |
| Port | `3306` |
| Database | `quan_li_kho_iphone` |
| User | `root` |
| Password | `123456` |

Nếu cần thay đổi, đặt biến môi trường trước khi chạy:

```
QLKH_DB_HOST=localhost
QLKH_DB_PORT=3306
QLKH_DB_NAME=quan_li_kho_iphone
QLKH_DB_USER=root
QLKH_DB_PASSWORD=matkhaucuaban
```

### Bước 3 — Chạy ứng dụng

**Windows** — double-click hoặc chạy từ terminal:

```bat
chay_windows.bat
```

Script tự động biên dịch toàn bộ source code rồi khởi động ứng dụng.

---

## 👤 Tài khoản mặc định

| Tên đăng nhập | Mật khẩu | Vai trò |
|---|---|---|
| `admin` | `123` | Admin (toàn quyền) |
| `quanly` | `123` | Quản lý |
| `nhanvien` | `123` | Nhân viên |

> **Lưu ý bảo mật:** Mật khẩu được mã hóa bằng SHA-256 trước khi lưu vào cơ sở dữ liệu.

---

## 🗄️ Cơ sở dữ liệu

Gồm 5 bảng chính:

```
nguoi_dung          — Người dùng và vai trò
san_pham            — Danh mục sản phẩm iPhone
phieu_nhap          — Phiếu nhập kho (header)
chi_tiet_phieu_nhap — Chi tiết phiếu nhập (line items)
phieu_xuat          — Phiếu xuất kho (header)
chi_tiet_phieu_xuat — Chi tiết phiếu xuất (line items)
```

Ứng dụng **tự động tạo và cập nhật cấu trúc bảng** khi khởi động lần đầu — không cần chạy migration thủ công.

---

## 📌 Ghi chú phát triển

- Dự án sử dụng **`PreparedStatement`** cho tất cả câu truy vấn SQL, tránh SQL Injection
- Các thao tác nhập/xuất kho sử dụng **transaction** để đảm bảo tính toàn vẹn dữ liệu
- Giao diện áp dụng **CardLayout** để chuyển đổi giữa các màn hình mà không cần tạo cửa sổ mới
- Phân quyền được kiểm tra ở cả tầng giao diện (ẩn/disable nút) và tầng nghiệp vụ (DAO)

---

*Dự án được thực hiện độc lập nhằm mục đích học tập và rèn luyện kỹ năng lập trình Java, thiết kế cơ sở dữ liệu và xây dựng ứng dụng desktop.*
