-- =====================================================
-- SCRIPT CẬP NHẬT DATABASE CHO HR MANAGEMENT SYSTEM
-- Chạy script này trong SQL Server Management Studio
-- =====================================================

-- 1. Thêm cột baseSalary vào bảng Positions
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'Positions') AND name = 'baseSalary')
BEGIN
    ALTER TABLE Positions ADD baseSalary DECIMAL(18,2) NULL;
    PRINT N'Đã thêm cột baseSalary vào bảng Positions';
END

-- 2. Thêm cột coefficient vào bảng Positions  
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'Positions') AND name = 'coefficient')
BEGIN
    ALTER TABLE Positions ADD coefficient DECIMAL(5,2) NULL;
    PRINT N'Đã thêm cột coefficient vào bảng Positions';
END

-- 3. Cập nhật giá trị mặc định cho các chức vụ hiện có
UPDATE Positions SET baseSalary = 10000000 WHERE baseSalary IS NULL;
UPDATE Positions SET coefficient = 1.0 WHERE coefficient IS NULL;

-- 4. Thêm cột checkInTime vào bảng Attendance (nếu chưa có)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'Attendance') AND name = 'checkInTime')
BEGIN
    ALTER TABLE Attendance ADD checkInTime TIME NULL;
    PRINT N'Đã thêm cột checkInTime vào bảng Attendance';
END

-- 5. Thêm cột checkOutTime vào bảng Attendance (nếu chưa có)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'Attendance') AND name = 'checkOutTime')
BEGIN
    ALTER TABLE Attendance ADD checkOutTime TIME NULL;
    PRINT N'Đã thêm cột checkOutTime vào bảng Attendance';
END

-- 6. Thêm cột overtimeHours vào bảng Attendance (nếu chưa có)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'Attendance') AND name = 'overtimeHours')
BEGIN
    ALTER TABLE Attendance ADD overtimeHours DECIMAL(5,2) NULL;
    PRINT N'Đã thêm cột overtimeHours vào bảng Attendance';
END

-- 7. Thêm cột notes vào bảng Salaries (nếu chưa có)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'Salaries') AND name = 'notes')
BEGIN
    ALTER TABLE Salaries ADD notes NVARCHAR(MAX) NULL;
    PRINT N'Đã thêm cột notes vào bảng Salaries';
END

PRINT N'======================================';
PRINT N'CẬP NHẬT DATABASE HOÀN TẤT!';
PRINT N'======================================';
