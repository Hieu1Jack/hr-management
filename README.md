# Hệ Thống Quản Lý Nhân Sự - HR Management System

## Giới Thiệu

Hệ thống quản lý nhân sự (HR Management System) được phát triển bằng **Java Spring Boot** với **Thymeleaf** template engine, **Spring Security** cho xác thực, và **SQL Server** làm cơ sở dữ liệu.

## Tính Năng Chính

### Phần Admin (10-12 Chức Năng)
1. **Quản Lý Người Dùng** - Tạo, sửa, xóa tài khoản người dùng và cấp quyền
2. **Quản Lý Nhân Viên** - CRUD nhân viên, cập nhật thông tin cá nhân
3. **Quản Lý Phòng Ban** - Tạo, quản lý các phòng ban
4. **Quản Lý Chức Vụ** - Quản lý các vị trí công việc
5. **Duyệt Phép** - Duyệt, từ chối yêu cầu phép nhân viên
6. **Quản Lý Lương** - Tính toán, quản lý lương và phúc lợi
7. **Chấm Công** - Quản lý thời gian làm việc, chấm công
8. **Xuất/Nhập Excel** - Tính năng import/export dữ liệu nhân viên và lương

### Phần Người Dùng
1. **Xem Hồ Sơ** - Xem thông tin cá nhân
2. **Xin Phép** - Gửi yêu cầu phép
3. **Xem Lương** - Xem bảng lương
4. **Xem Chấm Công** - Xem lịch sử chấm công

## Yêu Cầu Hệ Thống

- **Java**: JDK 17 trở lên
- **Maven**: 3.6+
- **SQL Server**: 2016 trở lên
- **Spring Boot**: 3.2.0

## Cài Đặt

### 1. Clone Repository
```bash
git clone <your-repo-url>
cd DoAnQuanLy-SpringBoot
```

### 2. Cấu Hình Cơ Sở Dữ Liệu
Cập nhật file `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://YOUR_SERVER\\INSTANCE;databaseName=DUCTIEN;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
```

### 3. Chạy Database Script
Tạo database và bảng bằng cách chạy SQL script từ file `database.sql`

### 4. Build Project
```bash
mvn clean install
```

### 5. Chạy Ứng Dụng
```bash
mvn spring-boot:run
```

Truy cập: `http://localhost:8080`

## Thông Tin Đăng Nhập Demo

**Admin Account:**
- Username: `admin`
- Password: `123456`

## Cấu Trúc Dự Án

```
DoAnQuanLy-SpringBoot/
├── src/main/java/com/ductien/hrmanagement/
│   ├── entity/                 # JPA Entities
│   ├── repository/             # Spring Data Repositories
│   ├── service/                # Business Logic Services
│   ├── controller/             # MVC Controllers
│   │   ├── admin/             # Admin Controllers
│   │   └── user/              # User Controllers
│   └── config/                # Security & Spring Config
├── src/main/resources/
│   ├── templates/             # Thymeleaf HTML templates
│   │   ├── admin/            # Admin Views
│   │   └── user/             # User Views
│   ├── static/               # CSS, JS, Images
│   └── application.properties # Configuration
└── pom.xml                    # Maven Dependencies
```

## Dependencies Chính

- **Spring Boot Web** - Web MVC
- **Spring Data JPA** - Database ORM
- **Spring Security** - Authentication & Authorization
- **Thymeleaf** - HTML Template Engine
- **Microsoft SQL Server JDBC Driver** - Database Connection
- **Apache POI** - Excel Import/Export
- **Lombok** - Reduce Boilerplate Code

## API Endpoints

### Admin Endpoints
- `GET /admin/users` - Danh sách người dùng
- `POST /admin/users/create` - Tạo người dùng
- `GET /admin/employees` - Danh sách nhân viên
- `GET /admin/departments` - Danh sách phòng ban
- `GET /admin/positions` - Danh sách chức vụ
- `GET /admin/leaves` - Danh sách phép
- `GET /admin/payroll` - Danh sách lương
- `GET /admin/attendance` - Danh sách chấm công

### User Endpoints
- `GET /user/profile/{id}` - Xem hồ sơ
- `GET /user/leave-request` - Xin phép
- `GET /user/salary/{employeeId}` - Xem lương
- `GET /user/attendance/{employeeId}` - Xem chấm công

## Tính Năng Excel Import/Export

### Export
```bash
# Nhân viên
GET /admin/employees/export

# Lương
GET /admin/payroll/export
```

### Import
```bash
# Nhân viên
POST /admin/employees/import
```

## Tính Năng Bảo Mật

- **Spring Security** - Xác thực và phân quyền
- **BCrypt Password Encoding** - Mã hóa mật khẩu
- **Role-based Access Control** - Kiểm soát truy cập dựa trên vai trò
- **CSRF Protection** - Bảo vệ CSRF

## Cấu Hình Email (Tùy Chọn)

Để gửi email thông báo, thêm vào `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Hỗ Trợ

Nếu gặp lỗi hoặc có câu hỏi, vui lòng liên hệ:
- Email: support@hrmanagement.com
- GitHub Issues: [Project Issues](https://github.com/yourrepo/issues)

## License

MIT License - Xem file LICENSE để chi tiết.

## Tác Giả

Phát triển bởi: Đỗ Đức Tiến

## Lịch Sử Cập Nhật

**v1.0.0 (2024-12-07)**
- Phiên bản đầu tiên
- Tất cả tính năng chính
- Hỗ trợ Excel Import/Export
