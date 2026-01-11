-- =====================================================
-- SCRIPT TẠO DỮ LIỆU TEST CHẤM CÔNG + LƯƠNG THÁNG 12/2025
-- =====================================================

-- =====================================================
-- BƯỚC 1: CHẠY QUERY NÀY TRƯỚC ĐỂ XEM DANH SÁCH NHÂN VIÊN
-- =====================================================
SELECT 
    e.employeeId AS [ID],
    e.MNV AS [Mã NV],
    e.employeeName AS [Họ tên],
    d.departmentName AS [Phòng ban],
    p.positionName AS [Chức vụ],
    e.email AS [Email],
    e.basicSalary AS [Lương cơ bản],
    CASE WHEN e.isActive = 1 THEN N'Đang làm' ELSE N'Nghỉ việc' END AS [Trạng thái]
FROM Employees e
LEFT JOIN Departments d ON e.DepartmentId = d.departmentId
LEFT JOIN Positions p ON e.PositionId = p.positionId
WHERE e.isActive = 1
ORDER BY d.departmentName, e.employeeName;

-- =====================================================
-- BƯỚC 2: SAU KHI XEM XONG, CHỌN EMPLOYEE VÀ THAY VÀO ĐÂY
-- =====================================================
DECLARE @EmpId INT = 1;  -- << THAY employeeId (cột ID) của nhân viên vào đây
DECLARE @BasicSalary DECIMAL(18,2) = 15000000; -- << THAY lương cơ bản (hoặc để mặc định)

-- =====================================================
-- BƯỚC 2: XÓA DỮ LIỆU CŨ THÁNG 12/2025 (NẾU CÓ)
-- =====================================================
DELETE FROM Attendance 
WHERE employeeId = @EmpId 
AND MONTH(attendanceDate) = 12 
AND YEAR(attendanceDate) = 2025;

DELETE FROM Salaries 
WHERE EmployeeId = @EmpId 
AND month = 12 
AND year = 2025;

-- =====================================================
-- BƯỚC 3: INSERT 26 NGÀY CHẤM CÔNG THÁNG 12/2025
-- (Bỏ qua Chủ nhật: 7, 14, 21, 28 và ngày 31)
-- =====================================================
DECLARE @Date DATE = '2025-12-01';
DECLARE @DayCount INT = 0;

WHILE @Date <= '2025-12-31' AND @DayCount < 26
BEGIN
    -- Bỏ qua Chủ nhật (DATEPART = 1 là Sunday)
    IF DATEPART(WEEKDAY, @Date) != 1
    BEGIN
        INSERT INTO Attendance (employeeId, attendanceDate, checkInTime, checkOutTime, 
                                totalHours, regularHours, overtimeHours, status, notes, CreatedAt)
        VALUES (
            @EmpId,
            @Date,
            '08:00:00',     -- Check-in 8h sáng
            '17:30:00',     -- Check-out 5h30 chiều
            8.5,            -- Tổng giờ (trừ 1h nghỉ trưa)
            8.0,            -- Giờ chính thức
            0.5,            -- Giờ OT
            N'Đủ công',     -- Trạng thái
            N'Đi làm đầy đủ',
            GETDATE()
        );
        SET @DayCount = @DayCount + 1;
    END
    SET @Date = DATEADD(DAY, 1, @Date);
END

PRINT N'Đã insert ' + CAST(@DayCount AS VARCHAR) + N' ngày chấm công';

-- =====================================================
-- BƯỚC 4: TÍNH VÀ INSERT LƯƠNG THÁNG 12/2025
-- =====================================================
DECLARE @WorkDays INT = 26;
DECLARE @OvertimeHours DECIMAL(18,2) = 13.0;  -- 26 ngày x 0.5h OT
DECLARE @OvertimePay DECIMAL(18,2);
DECLARE @Bonus DECIMAL(18,2) = 500000;        -- Thưởng
DECLARE @Allowance DECIMAL(18,2) = 1000000;   -- Phụ cấp
DECLARE @Deduction DECIMAL(18,2) = 0;         -- Khấu trừ
DECLARE @TotalSalary DECIMAL(18,2);

-- Tính lương OT: (Lương cơ bản / 26 ngày / 8 giờ) x 1.5 x số giờ OT
SET @OvertimePay = (@BasicSalary / 26.0 / 8.0) * 1.5 * @OvertimeHours;

-- Tổng lương = Lương cơ bản + OT + Thưởng + Phụ cấp - Khấu trừ
SET @TotalSalary = @BasicSalary + @OvertimePay + @Bonus + @Allowance - @Deduction;

INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, deductions, allowance, 
                      deduction, totalSalary, notes, CreatedAt)
VALUES (
    @EmpId,
    2025,
    12,
    @BasicSalary,       -- basicSalary
    @BasicSalary,       -- baseSalary  
    @WorkDays,          -- workDays = 26
    @OvertimeHours,     -- overtimeHours = 13
    @OvertimePay,       -- overtimePay (tự tính)
    @Bonus,             -- bonus = 500,000
    @Deduction,         -- deductions
    @Allowance,         -- allowance = 1,000,000
    @Deduction,         -- deduction
    @TotalSalary,       -- totalSalary
    N'Lương tháng 12/2025 - Đủ công 26 ngày',
    GETDATE()
);

PRINT N'Đã tạo bảng lương tháng 12/2025';
PRINT N'Lương cơ bản: ' + CAST(@BasicSalary AS VARCHAR);
PRINT N'Lương OT: ' + CAST(@OvertimePay AS VARCHAR);
PRINT N'Tổng lương: ' + CAST(@TotalSalary AS VARCHAR);

-- =====================================================
-- BƯỚC 5: KIỂM TRA KẾT QUẢ
-- =====================================================
SELECT 'CHẤM CÔNG THÁNG 12/2025' AS Info;
SELECT * FROM Attendance 
WHERE employeeId = @EmpId 
AND MONTH(attendanceDate) = 12 
AND YEAR(attendanceDate) = 2025
ORDER BY attendanceDate;

SELECT 'LƯƠNG THÁNG 12/2025' AS Info;
SELECT s.*, e.employeeName 
FROM Salaries s
JOIN Employees e ON s.EmployeeId = e.employeeId
WHERE s.EmployeeId = @EmpId 
AND s.month = 12 
AND s.year = 2025;
