-- Script liên kết User với Employee
-- Chạy script này trong SQL Server Management Studio

-- Kiểm tra danh sách Users và Employees
SELECT u.userId, u.username, u.email, u.fullName, u.employeeId, 
       e.employeeId as emp_id, e.employeeCode, e.fullName as emp_name, e.email as emp_email
FROM Users u
LEFT JOIN Employees e ON u.employeeId = e.employeeId OR u.email = e.email;

-- Liên kết User với Employee dựa trên email
UPDATE Users 
SET employeeId = (SELECT employeeId FROM Employees WHERE Employees.email = Users.email)
WHERE employeeId IS NULL 
  AND EXISTS (SELECT 1 FROM Employees WHERE Employees.email = Users.email);

-- Hoặc liên kết cụ thể cho NV001 (Nguyễn Danh Hiếu)
-- Thay đổi userId và employeeId theo database của bạn
-- UPDATE Users SET employeeId = 1 WHERE username = 'nv001';

-- Kiểm tra lại sau khi update
SELECT u.userId, u.username, u.email, u.employeeId, e.employeeCode, e.fullName
FROM Users u
LEFT JOIN Employees e ON u.employeeId = e.employeeId;
