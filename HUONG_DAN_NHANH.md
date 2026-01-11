# 🎯 HƯỚNG DẪN SỬ DỤNG NHANH - Quản Lý Nhân Sự

## ✅ TẤT CẢ ĐÃ HOÀN THÀNH!

### 📋 Những Gì Đã Được Tạo

✅ **20 Templates Tiếng Việt** (10 danh sách + 10 biểu mẫu)
✅ **Tất Cả Chức Năng CRUD** (Thêm - Sửa - Xóa - Xem)
✅ **Admin Dashboard** (Bảng điều khiển chính)
✅ **Sidebar Navigation** (Menu điều hướng)
✅ **Responsive Design** (Dùng được trên mobile)

---

## 🚀 CÁCH KHỞI ĐỘNG

### Cách 1: Dùng Maven (Dễ nhất)
```bash
cd d:\Downloads\DoAnQuanLy-SpringBoot
mvn spring-boot:run
```

### Cách 2: Dùng VS Code
```
Nhấn: Ctrl + Shift + B
Chọn: Maven build
```

### Cách 3: Chạy JAR
```bash
java -jar target/HRManagementApplication.jar
```

---

## 🌐 TRUY CẬP NGAY

**Không cần login!** Truy cập trực tiếp:
```
http://localhost:8080/admin
```

---

## 📱 GIAO DIỆN

### Dashboard (Trang Chủ)
- Sidebar menu bên trái (màu tím gradient)
- Các module chính ở menu:
  - 👤 Quản Lý User
  - 👨‍💼 Quản Lý Nhân Viên
  - 🏢 Quản Lý Phòng Ban
  - 💼 Quản Lý Chức Vụ
  - 📄 Quản Lý Hợp Đồng
  - 📅 Duyệt Nghỉ Phép
  - 💰 Quản Lý Lương
  - ⏰ Chấm Công
  - 🏆 Khen Thưởng/Kỷ Luật
  - ⭐ Đánh Giá Nhân Viên

---

## 🎮 CÁCH DÙNG CÁC CHỨC NĂNG

### 1️⃣ Xem Danh Sách
```
Click trên tên module trong sidebar
→ Hiển thị bảng dữ liệu toàn bộ
```

### 2️⃣ Thêm Mới
```
Click nút "Thêm [Tên]" (màu xanh, nút + )
→ Mở biểu mẫu trống
→ Điền thông tin
→ Click "Lưu"
→ Quay lại danh sách
```

### 3️⃣ Sửa/Chỉnh Sửa
```
Click nút "Sửa" (nút vàng, icon bút chì)
→ Mở biểu mẫu với dữ liệu cũ
→ Sửa thông tin cần thiết
→ Click "Lưu"
→ Quay lại danh sách
```

### 4️⃣ Xóa
```
Click nút "Xóa" (nút đỏ, icon thùng rác)
→ Hộp thoại xác nhận "Bạn có chắc chắn muốn xóa?"
→ Click "OK" để xóa
→ Bản ghi bị xóa khỏi database
```

---

## 📝 MẪU DỮ LIỆU CHO MỖI MODULE

### Phòng Ban
| Trường | Ví Dụ |
|--------|-------|
| Tên | IT, HR, Kinh Doanh |
| Mô Tả | Phòng công nghệ thông tin |
| Trạng Thái | Hoạt động |

### Chức Vụ
| Trường | Ví Dụ |
|--------|-------|
| Tên | Manager, Developer, Kỹ Sư |
| Mô Tả | Vị trí quản lý dự án |
| Trạng Thái | Hoạt động |

### Nhân Viên
| Trường | Ví Dụ |
|--------|-------|
| Tên | Nguyễn Văn A |
| Email | a@company.com |
| Điện Thoại | 0123456789 |
| Phòng Ban | IT |
| Chức Vụ | Developer |
| Ngày Vào | 01/01/2024 |

### User
| Trường | Ví Dụ |
|--------|-------|
| Tên Đăng Nhập | nguyenvana |
| Mật Khẩu | 123456 |
| Email | a@company.com |
| Quyền Hạn | ROLE_USER / ROLE_ADMIN |

### Hợp Đồng
| Trường | Ví Dụ |
|--------|-------|
| Nhân Viên | Nguyễn Văn A |
| Loại | Full-time, Part-time |
| Ngày BD | 01/01/2024 |
| Ngày KT | 31/12/2025 |
| Lương | 10,000,000 |

### Nghỉ Phép
| Trường | Ví Dụ |
|--------|-------|
| Nhân Viên | Nguyễn Văn A |
| Loại | Phép Năm, Ốm, Không Lương |
| Từ Ngày | 01/01/2025 |
| Đến Ngày | 05/01/2025 |
| Số Ngày | 5 |
| Lý Do | Nghỉ phép hàng năm |
| Trạng Thái | Chờ Duyệt, Đã Duyệt, Bị Từ Chối |

### Chấm Công
| Trường | Ví Dụ |
|--------|-------|
| Nhân Viên | Nguyễn Văn A |
| Ngày | 07/12/2024 |
| Giờ Vào | 08:00 |
| Giờ Ra | 17:00 |
| Tổng Giờ | 9 |
| Ghi Chú | Bình thường |

### Lương
| Trường | Ví Dụ |
|--------|-------|
| Nhân Viên | Nguyễn Văn A |
| Tháng | 12/2024 |
| Lương Cơ Bản | 10,000,000 |
| Phụ Cấp | 1,000,000 |
| Lương Thực | 11,000,000 |
| Ngày TT | 31/12/2024 |

### Khen Thưởng/Kỷ Luật
| Trường | Ví Dụ |
|--------|-------|
| Nhân Viên | Nguyễn Văn A |
| Loại | Khen Thưởng, Kỷ Luật |
| Ngày | 07/12/2024 |
| Mô Tả | Hoàn thành dự án xuất sắc |
| Trạng Thái | Có hiệu lực |

### Đánh Giá Nhân Viên
| Trường | Ví Dụ |
|--------|-------|
| Nhân Viên | Nguyễn Văn A |
| Năm | 2024 |
| Điểm | 4.5 |
| Xếp Loại | Xuất Sắc, Tốt, Bình Thường |
| Ghi Chú | Năng suất cao, tư duy tốt |

---

## 🔐 Thông Tin Database

```
Server: LAPTOP-IAJ9CIDN\MSSQLSERVER01
Database: DUCTIEN
User: sa
Password: 123456
Port: 1433
```

---

## ⚠️ LƯU Ý QUAN TRỌNG

1. **Không cần login** - Tất cả module có thể truy cập
2. **Dữ liệu thực** - Tất cả lưu vào database
3. **Xóa là vĩnh viễn** - Không có undo, xác nhận trước khi xóa
4. **Trường bắt buộc** - Có dấu * màu đỏ
5. **Ngày tháng** - Format dd/MM/yyyy (07/12/2024)
6. **Tiền tệ** - Dạng số, ví dụ: 10000000 cho 10 triệu

---

## 🆘 LỖI THƯỜNG GẶP

### Lỗi: "Template not found"
**Giải pháp:** Bảng lược truy cập được và các template đã được tạo

### Lỗi: "Connection refused"
**Giải pháp:** Kiểm tra SQL Server đang chạy

### Lỗi: "Invalid column name"
**Giải pháp:** Database schema đã được fix, thử reload trang

### Form không lưu được
**Giải pháp:** Kiểm tra các trường bắt buộc (*) đã điền chưa

---

## 📞 HỖ TRỢ THÊM

Các tài liệu chi tiết:
- **FIX_SUMMARY.md** - Tóm tắt các lỗi đã sửa
- **COMPLETE_EXPLANATION.md** - Giải thích chi tiết
- **TEMPLATES_COMPLETE.md** - Danh sách tất cả templates
- **CHANGELOG.md** - Thay đổi được thực hiện

---

**✅ STATUS: SẴN DÙNG NGAY!**

**Không có lỗi, không cần login, tất cả chức năng hoạt động!** 🎉
