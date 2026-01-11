-- =====================================================
-- ANHHIEU Database Schema - HR Management System v2.0
-- =====================================================

-- Drop Database if exists
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'ANHHIEU')
    DROP DATABASE ANHHIEU;

CREATE DATABASE ANHHIEU;
GO

USE ANHHIEU;
GO

-- =====================================================
-- Roles Table (Phân quyền)
-- =====================================================
CREATE TABLE Roles (
    roleId INT PRIMARY KEY IDENTITY(1,1),
    roleName NVARCHAR(50) UNIQUE NOT NULL,
    description NVARCHAR(MAX),
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Users Table
-- =====================================================
CREATE TABLE Users (
    userId INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(MAX) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    fullName NVARCHAR(100) NOT NULL,
    roleId INT FOREIGN KEY REFERENCES Roles(roleId),
    role NVARCHAR(50) DEFAULT 'NHAN_VIEN',
    isAdmin BIT NOT NULL DEFAULT 0,
    isActive BIT NOT NULL DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Departments Table
-- =====================================================
CREATE TABLE Departments (
    departmentId INT PRIMARY KEY IDENTITY(1,1),
    departmentName NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(MAX),
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Positions Table
-- =====================================================
CREATE TABLE Positions (
    positionId INT PRIMARY KEY IDENTITY(1,1),
    positionName NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(MAX),
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Employees Table
-- =====================================================
CREATE TABLE Employees (
    employeeId INT PRIMARY KEY IDENTITY(1,1),
    employeeName NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20) NOT NULL,
    address NVARCHAR(200),
    dateOfBirth DATE,
    gender NVARCHAR(10),
    CCCD NVARCHAR(20),
    MNV NVARCHAR(20),
    DepartmentId INT FOREIGN KEY REFERENCES Departments(departmentId),
    PositionId INT FOREIGN KEY REFERENCES Positions(positionId),
    joinDate DATE,
    basicSalary DECIMAL(18,2),
    IsLeader BIT DEFAULT 0,
    Imgs NVARCHAR(255) DEFAULT 'noimage.png',
    EmployeeStatus INT DEFAULT 1,
    IsActive BIT DEFAULT 1,
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Contracts Table
-- =====================================================
CREATE TABLE Contracts (
    contractId INT PRIMARY KEY IDENTITY(1,1),
    EmployeeId INT FOREIGN KEY REFERENCES Employees(employeeId),
    contractType NVARCHAR(50),
    startDate DATE,
    endDate DATE,
    salary DECIMAL(18,2),
    contractStatus NVARCHAR(50),
    description NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- LeaveRequests Table
-- =====================================================
CREATE TABLE LeaveRequests (
    leaveId INT PRIMARY KEY IDENTITY(1,1),
    EmployeeId INT FOREIGN KEY REFERENCES Employees(employeeId),
    leaveType NVARCHAR(50),
    startDate DATE,
    endDate DATE,
    numberOfDays INT,
    reason NVARCHAR(MAX),
    status NVARCHAR(20) DEFAULT 'Pending',
    ApprovedBy NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Attendance Table
-- =====================================================
CREATE TABLE Attendance (
    attendanceId INT PRIMARY KEY IDENTITY(1,1),
    EmployeeId INT FOREIGN KEY REFERENCES Employees(employeeId),
    attendanceDate DATE NOT NULL,
    status NVARCHAR(50),
    notes NVARCHAR(MAX),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Salaries Table
-- =====================================================
CREATE TABLE Salaries (
    salaryId INT PRIMARY KEY IDENTITY(1,1),
    EmployeeId INT FOREIGN KEY REFERENCES Employees(employeeId),
    year INT,
    month INT,
    basicSalary DECIMAL(18,2),
    allowance DECIMAL(18,2),
    deduction DECIMAL(18,2),
    totalSalary DECIMAL(18,2),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- RewardDiscipline Table (Khen Thưởng/Kỷ Luật)
-- =====================================================
CREATE TABLE RewardDiscipline (
    rewardDisciplineId INT PRIMARY KEY IDENTITY(1,1),
    EmployeeId INT FOREIGN KEY REFERENCES Employees(employeeId),
    type NVARCHAR(50),
    reason NVARCHAR(MAX),
    amount DECIMAL(18,2),
    DepartmentId INT FOREIGN KEY REFERENCES Departments(departmentId),
    createdDate DATETIME DEFAULT GETDATE(),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- EmployeeRating Table (Đánh Giá Nhân Viên)
-- =====================================================
CREATE TABLE EmployeeRating (
    ratingId INT PRIMARY KEY IDENTITY(1,1),
    EmployeeId INT FOREIGN KEY REFERENCES Employees(employeeId),
    ratingYear INT,
    ratingMonth INT,
    performanceScore DECIMAL(5,2),
    attendanceScore DECIMAL(5,2),
    conductScore DECIMAL(5,2),
    totalScore DECIMAL(5,2),
    comments NVARCHAR(MAX),
    ratedBy NVARCHAR(100),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME
);

-- =====================================================
-- Insert Sample Data
-- =====================================================

-- Insert Roles (Phân quyền)
INSERT INTO Roles (roleName, description, IsActive) VALUES
('ADMIN', N'Quản trị viên - Toàn quyền hệ thống', 1),
('NHAN_VIEN', N'Nhân viên - Quyền hạn chế', 1);

-- Insert Users với phân quyền
INSERT INTO Users (username, password, email, fullName, roleId, role, isAdmin, isActive) VALUES
('admin', '123456', 'admin@company.com', N'Quản Trị Viên', 1, 'ADMIN', 1, 1),
('user1', '123456', 'user1@company.com', N'Nguyễn Văn A', 2, 'NHAN_VIEN', 0, 1),
('user2', '123456', 'user2@company.com', N'Trần Thị B', 2, 'NHAN_VIEN', 0, 1);

-- Insert Departments
INSERT INTO Departments (departmentName, description, IsActive) VALUES
(N'Phòng Nhân Sự', N'Bộ phận quản lý nhân sự', 1),
(N'Phòng Kinh Doanh', N'Bộ phận phát triển kinh doanh', 1),
(N'Phòng Công Nghệ', N'Bộ phận công nghệ thông tin', 1),
(N'Phòng Tài Chính', N'Bộ phận tài chính kế toán', 1);

-- Insert Positions
INSERT INTO Positions (positionName, description, IsActive) VALUES
(N'Giám Đốc', N'Vị trí lãnh đạo cấp cao', 1),
(N'Trưởng Phòng', N'Trưởng phòng ban', 1),
(N'Nhân Viên', N'Nhân viên bình thường', 1),
(N'Thực Tập Sinh', N'Vị trí thực tập', 1);

-- Insert Employees
INSERT INTO Employees (employeeName, email, phone, address, dateOfBirth, gender, CCCD, MNV, DepartmentId, PositionId, joinDate, basicSalary, IsLeader, IsActive) VALUES
(N'Nguyễn Văn A', 'nguyenvana@company.com', '0123456789', N'Hà Nội', '1990-01-15', N'Nam', '123456789012', 'NV001', 1, 1, '2020-01-01', 15000000.00, 1, 1),
(N'Trần Thị B', 'tranthib@company.com', '0987654321', N'TP Hồ Chí Minh', '1992-05-20', N'Nữ', '987654321098', 'NV002', 2, 2, '2020-03-01', 12000000.00, 1, 1),
(N'Lê Văn C', 'levanc@company.com', '0912345678', N'Đà Nẵng', '1995-07-10', N'Nam', '456789123456', 'NV003', 3, 3, '2021-01-01', 10000000.00, 0, 1),
(N'Phạm Thị D', 'phamthid@company.com', '0834567890', N'Hà Nội', '1998-03-25', N'Nữ', '789123456789', 'NV004', 4, 3, '2022-06-01', 8000000.00, 0, 1);

-- Insert Contracts
INSERT INTO Contracts (EmployeeId, contractType, startDate, endDate, salary, contractStatus, description) VALUES
(1, N'Vô Thời Hạn', '2020-01-01', NULL, 15000000.00, 'Active', N'Hợp đồng lao động vô thời hạn'),
(2, N'Thời Hạn', '2020-03-01', '2025-03-01', 12000000.00, 'Active', N'Hợp đồng lao động có thời hạn'),
(3, N'Thời Hạn', '2021-01-01', '2024-12-31', 10000000.00, 'Active', N'Hợp đồng lao động có thời hạn'),
(4, N'Thời Vụ', '2022-06-01', '2025-06-01', 8000000.00, 'Active', N'Hợp đồng lao động thời vụ');

-- Insert Leave Requests
INSERT INTO LeaveRequests (EmployeeId, leaveType, startDate, endDate, numberOfDays, reason, status) VALUES
(1, N'Phép Năm', '2024-12-10', '2024-12-15', 5, N'Nghỉ lễ', 'Approved'),
(2, N'Phép Bệnh', '2024-12-18', '2024-12-20', 3, N'Cảm lạnh', 'Pending'),
(3, N'Phép Năm', '2025-01-01', '2025-01-07', 7, N'Tết dương lịch', 'Approved');

-- Insert Attendance
INSERT INTO Attendance (EmployeeId, attendanceDate, status, notes) VALUES
(1, '2024-12-06', 'Present', N'Đúng giờ'),
(2, '2024-12-06', 'Late', N'Trễ 15 phút'),
(3, '2024-12-06', 'Absent', N'Vắng không phép'),
(4, '2024-12-06', 'Present', N'Đúng giờ');

-- Insert Salaries
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, allowance, deduction, totalSalary) VALUES
(1, 2024, 11, 15000000.00, 2000000.00, 1500000.00, 15500000.00),
(1, 2024, 12, 15000000.00, 2000000.00, 1500000.00, 15500000.00),
(2, 2024, 11, 12000000.00, 1500000.00, 1200000.00, 12300000.00),
(2, 2024, 12, 12000000.00, 1500000.00, 1200000.00, 12300000.00),
(3, 2024, 11, 10000000.00, 1000000.00, 1000000.00, 10000000.00),
(3, 2024, 12, 10000000.00, 1000000.00, 1000000.00, 10000000.00),
(4, 2024, 11, 8000000.00, 800000.00, 800000.00, 8000000.00),
(4, 2024, 12, 8000000.00, 800000.00, 800000.00, 8000000.00);

-- Insert Reward/Discipline
INSERT INTO RewardDiscipline (EmployeeId, type, reason, amount, DepartmentId) VALUES
(1, N'Khen Thưởng', N'Hoàn thành dự án xuất sắc', 2000000.00, 1),
(2, N'Kỷ Luật', N'Vi phạm quy định công ty', -500000.00, 2),
(3, N'Khen Thưởng', N'Chăm chỉ, có trách nhiệm', 1500000.00, 3);

-- Insert Employee Ratings
INSERT INTO EmployeeRating (EmployeeId, ratingYear, ratingMonth, performanceScore, attendanceScore, conductScore, totalScore, comments, ratedBy) VALUES
(1, 2024, 12, 9.0, 9.5, 9.0, 9.2, N'Nhân viên xuất sắc', 'admin'),
(2, 2024, 12, 8.5, 8.0, 8.5, 8.3, N'Nhân viên tốt', 'admin'),
(3, 2024, 12, 8.0, 8.5, 8.0, 8.2, N'Nhân viên bình thường', 'admin'),
(4, 2024, 12, 7.5, 7.5, 7.5, 7.5, N'Cần cải thiện', 'admin');

-- =====================================================
-- Create Indexes
-- =====================================================
CREATE INDEX idx_user_username ON Users(username);
CREATE INDEX idx_user_email ON Users(email);
CREATE INDEX idx_user_role ON Users(roleId);
CREATE INDEX idx_employee_department ON Employees(DepartmentId);
CREATE INDEX idx_employee_position ON Employees(PositionId);
CREATE INDEX idx_leave_employee ON LeaveRequests(EmployeeId);
CREATE INDEX idx_leave_status ON LeaveRequests(status);
CREATE INDEX idx_salary_employee ON Salaries(EmployeeId);
CREATE INDEX idx_salary_year_month ON Salaries(year, month);
CREATE INDEX idx_attendance_employee ON Attendance(EmployeeId);
CREATE INDEX idx_attendance_date ON Attendance(attendanceDate);
CREATE INDEX idx_contract_employee ON Contracts(EmployeeId);
CREATE INDEX idx_rating_employee ON EmployeeRating(EmployeeId);
CREATE INDEX idx_reward_employee ON RewardDiscipline(EmployeeId);

-- =====================================================
-- Verify Data
-- =====================================================
SELECT COUNT(*) as TotalRoles FROM Roles;
SELECT COUNT(*) as TotalUsers FROM Users;
SELECT COUNT(*) as TotalEmployees FROM Employees;
SELECT COUNT(*) as TotalDepartments FROM Departments;
SELECT COUNT(*) as TotalPositions FROM Positions;
SELECT COUNT(*) as TotalContracts FROM Contracts;
SELECT COUNT(*) as TotalLeaveRequests FROM LeaveRequests;
SELECT COUNT(*) as TotalSalaries FROM Salaries;
SELECT COUNT(*) as TotalAttendance FROM Attendance;
