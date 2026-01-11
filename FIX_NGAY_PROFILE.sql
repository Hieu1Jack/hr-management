-- =====================================================
-- SCRIPT FIX NHANH: Liên kết User với Employee
-- Chạy script này trong SQL Server Management Studio
-- Database: ANHHIEU
-- =====================================================

USE ANHHIEU;
GO

-- Bước 1: Xem dữ liệu hiện tại
SELECT 'TRƯỚC KHI FIX - USERS' as info, userId, username, email, fullName, employeeId FROM Users;
SELECT 'TRƯỚC KHI FIX - EMPLOYEES' as info, employeeId, employeeName, email, MNV FROM Employees;

-- Bước 2: Cập nhật employeeId cho Users dựa trên tên (fullName = employeeName)
UPDATE Users 
SET employeeId = (SELECT TOP 1 employeeId FROM Employees WHERE Employees.employeeName = Users.fullName)
WHERE employeeId IS NULL 
  AND isAdmin = 0
  AND EXISTS (SELECT 1 FROM Employees WHERE Employees.employeeName = Users.fullName);

-- Bước 3: Nếu bước 2 không thành công, cập nhật thủ công
-- Giả sử user1 = employeeId 1, user2 = employeeId 2
UPDATE Users SET employeeId = 1 WHERE username = 'user1' AND employeeId IS NULL;
UPDATE Users SET employeeId = 2 WHERE username = 'user2' AND employeeId IS NULL;

-- Bước 4: Hoặc cập nhật email của User khớp với Employee để tìm kiếm qua email hoạt động
UPDATE Users SET email = 'nguyenvana@company.com' WHERE username = 'user1';
UPDATE Users SET email = 'tranthib@company.com' WHERE username = 'user2';

-- Bước 5: Kiểm tra kết quả
SELECT 'SAU KHI FIX' as info, 
       u.userId, u.username, u.email, u.fullName, u.employeeId,
       e.employeeId as emp_id, e.employeeName, e.email as emp_email, e.MNV
FROM Users u
LEFT JOIN Employees e ON u.employeeId = e.employeeId;

GO
