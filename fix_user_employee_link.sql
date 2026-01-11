-- =====================================================
-- Script FIX liên kết User với Employee
-- Vấn đề: Email trong bảng Users khác với email trong bảng Employees
-- =====================================================

-- 1. Xem dữ liệu hiện tại
SELECT 'USERS' as TableName, userId, username, email, fullName, employeeId FROM Users;
SELECT 'EMPLOYEES' as TableName, employeeId, employeeName, email, MNV FROM Employees;

-- =====================================================
-- CÁCH 1: Cập nhật email Users khớp với Employees
-- =====================================================
UPDATE Users SET email = 'nguyenvana@company.com' WHERE username = 'user1';
UPDATE Users SET email = 'tranthib@company.com' WHERE username = 'user2';

-- =====================================================
-- CÁCH 2: Liên kết trực tiếp qua employeeId
-- =====================================================
-- Giả sử: user1 = NV001 (employeeId = 1), user2 = NV002 (employeeId = 2)
UPDATE Users SET employeeId = 1 WHERE username = 'user1';
UPDATE Users SET employeeId = 2 WHERE username = 'user2';

-- =====================================================
-- CÁCH 3: Liên kết dựa trên fullName (nếu giống nhau)
-- =====================================================
UPDATE Users 
SET employeeId = (SELECT TOP 1 employeeId FROM Employees WHERE Employees.employeeName = Users.fullName)
WHERE employeeId IS NULL 
  AND EXISTS (SELECT 1 FROM Employees WHERE Employees.employeeName = Users.fullName);

-- =====================================================
-- Kiểm tra kết quả sau khi cập nhật
-- =====================================================
SELECT u.userId, u.username, u.email, u.fullName, u.employeeId,
       e.employeeId as emp_id, e.employeeName, e.email as emp_email, e.MNV
FROM Users u
LEFT JOIN Employees e ON u.employeeId = e.employeeId;

-- =====================================================
-- TẠO USER MỚI CHO TỪNG NHÂN VIÊN (Nếu cần)
-- =====================================================
-- Xóa users cũ (ngoại trừ admin)
-- DELETE FROM Users WHERE username NOT IN ('admin');

-- Tạo user mới cho từng nhân viên với email khớp
-- Password mặc định: 123456
/*
INSERT INTO Users (username, password, email, fullName, role, isAdmin, isActive, employeeId) 
SELECT 
    LOWER(MNV) as username,  -- username = mã nhân viên (viết thường)
    '123456' as password,
    email,
    employeeName as fullName,
    'NHAN_VIEN' as role,
    0 as isAdmin,
    1 as isActive,
    employeeId
FROM Employees
WHERE email NOT IN (SELECT email FROM Users);
*/
