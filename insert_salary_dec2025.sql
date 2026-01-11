-- =====================================================
-- INSERT DỮ LIỆU LƯƠNG MẪU THÁNG 12/2025
-- Dành cho tất cả nhân viên (employeeId: 1009-1020)
-- =====================================================

USE ANHHIEU;
GO

-- Xóa dữ liệu lương cũ tháng 12/2025 nếu có
DELETE FROM Salaries WHERE year = 2025 AND month = 12;
GO

-- =====================================================
-- INSERT LƯƠNG CHO TỪNG NHÂN VIÊN
-- Mỗi người có tình huống khác nhau:
-- - Đủ ngày công (26 ngày)
-- - Thiếu ngày công
-- - OT nhiều
-- - Có thưởng
-- - Có khấu trừ
-- =====================================================

-- 1. Nguyễn Văn A (ID: 1009) - ĐỦ NGÀY CÔNG, OT vừa phải
-- Lương cơ bản: 10,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1009, 2025, 12, 
    10000000,           -- basicSalary (từ Employee)
    10000000,           -- baseSalary
    26,                 -- workDays - ĐỦ 26 ngày
    8,                  -- overtimeHours - 8 giờ OT
    576923,             -- overtimePay = (10tr/26/8) * 1.5 * 8 = 576,923
    500000,             -- bonus - Thưởng năng suất
    500000,             -- allowance - Phụ cấp xăng xe
    0,                  -- deduction
    11576923,           -- totalSalary = 10tr + 576,923 + 500k + 500k
    N'Đủ công, hoàn thành tốt nhiệm vụ', 
    GETDATE());

-- 2. Trần Minh Quân (ID: 1011) - ĐỦ NGÀY CÔNG, OT NHIỀU
-- Lương cơ bản: 14,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1011, 2025, 12, 
    14000000,           -- basicSalary
    14000000,           -- baseSalary
    26,                 -- workDays - ĐỦ 26 ngày
    24,                 -- overtimeHours - 24 giờ OT (OT NHIỀU)
    2423077,            -- overtimePay = (14tr/26/8) * 1.5 * 24 = 2,423,077
    1000000,            -- bonus - Thưởng dự án
    800000,             -- allowance - Phụ cấp
    200000,             -- deduction - Trừ bảo hiểm
    18023077,           -- totalSalary
    N'OT nhiều do dự án cuối năm, hoàn thành xuất sắc', 
    GETDATE());

-- 3. Nguyễn Thị Mai (ID: 1012) - THIẾU 3 NGÀY CÔNG
-- Lương cơ bản: 12,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1012, 2025, 12, 
    12000000,           -- basicSalary
    10615385,           -- baseSalary = 12tr * (23/26) = 10,615,385 (thiếu 3 ngày)
    23,                 -- workDays - THIẾU 3 ngày
    0,                  -- overtimeHours
    0,                  -- overtimePay
    0,                  -- bonus
    400000,             -- allowance
    300000,             -- deduction - Trừ BHXH
    10715385,           -- totalSalary = 10,615,385 + 400k - 300k
    N'Nghỉ phép 3 ngày (đã hết phép năm)', 
    GETDATE());

-- 4. Lê Hoàng Phúc (ID: 1013) - ĐỦ CÔNG, CÓ THƯỞNG LỚN
-- Lương cơ bản: 15,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1013, 2025, 12, 
    15000000,           -- basicSalary
    15000000,           -- baseSalary
    26,                 -- workDays - ĐỦ
    12,                 -- overtimeHours
    1298077,            -- overtimePay = (15tr/26/8) * 1.5 * 12
    3000000,            -- bonus - THƯỞNG TẾT + THƯỞNG DOANH SỐ
    600000,             -- allowance
    500000,             -- deduction
    19398077,           -- totalSalary
    N'Thưởng doanh số Q4, thưởng Tết sớm', 
    GETDATE());

-- 5. Phạm Ngọc Anh (ID: 1014) - THIẾU 5 NGÀY, KHÔNG OT
-- Lương cơ bản: 11,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1014, 2025, 12, 
    11000000,           -- basicSalary
    8884615,            -- baseSalary = 11tr * (21/26) = 8,884,615
    21,                 -- workDays - THIẾU 5 ngày
    0,                  -- overtimeHours
    0,                  -- overtimePay
    0,                  -- bonus
    300000,             -- allowance
    200000,             -- deduction
    8984615,            -- totalSalary
    N'Nghỉ ốm 5 ngày (có giấy BS)', 
    GETDATE());

-- 6. Võ Thanh Tùng (ID: 1015) - ĐỦ CÔNG, OT RẤT NHIỀU
-- Lương cơ bản: 16,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1015, 2025, 12, 
    16000000,           -- basicSalary
    16000000,           -- baseSalary
    26,                 -- workDays - ĐỦ
    40,                 -- overtimeHours - OT RẤT NHIỀU (40 giờ)
    4615385,            -- overtimePay = (16tr/26/8) * 1.5 * 40 = 4,615,385
    2000000,            -- bonus - Thưởng dự án khẩn cấp
    700000,             -- allowance
    400000,             -- deduction
    22915385,           -- totalSalary
    N'OT nhiều xử lý sự cố cuối năm, thưởng đặc biệt', 
    GETDATE());

-- 7. Đặng Thị Hồng (ID: 1016) - ĐỦ CÔNG, OT ÍT
-- Lương cơ bản: 12,500,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1016, 2025, 12, 
    12500000,           -- basicSalary
    12500000,           -- baseSalary
    26,                 -- workDays - ĐỦ
    4,                  -- overtimeHours - OT ít
    360577,             -- overtimePay = (12.5tr/26/8) * 1.5 * 4
    300000,             -- bonus
    450000,             -- allowance
    250000,             -- deduction
    13360577,           -- totalSalary
    N'Hoàn thành công việc đúng tiến độ', 
    GETDATE());

-- 8. Nguyễn Quốc Bảo (ID: 1017) - THIẾU 2 NGÀY, CÓ OT BÙ
-- Lương cơ bản: 18,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1017, 2025, 12, 
    18000000,           -- basicSalary
    16615385,           -- baseSalary = 18tr * (24/26) = 16,615,385
    24,                 -- workDays - THIẾU 2 ngày
    16,                 -- overtimeHours - OT bù
    2076923,            -- overtimePay = (18tr/26/8) * 1.5 * 16
    500000,             -- bonus
    600000,             -- allowance
    350000,             -- deduction
    19442308,           -- totalSalary
    N'Nghỉ phép 2 ngày, OT bù tiến độ', 
    GETDATE());

-- 9. Trịnh Thu Hà (ID: 1018) - ĐỦ CÔNG, CÓ KHẤU TRỪ
-- Lương cơ bản: 10,500,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1018, 2025, 12, 
    10500000,           -- basicSalary
    10500000,           -- baseSalary
    26,                 -- workDays - ĐỦ
    6,                  -- overtimeHours
    455769,             -- overtimePay
    200000,             -- bonus
    400000,             -- allowance
    800000,             -- deduction - KHẤU TRỪ NHIỀU (ứng lương tháng trước)
    10755769,           -- totalSalary
    N'Trừ tạm ứng tháng 11', 
    GETDATE());

-- 10. Bùi Đức Long (ID: 1019) - THIẾU 7 NGÀY (NGHỈ NHIỀU)
-- Lương cơ bản: 20,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1019, 2025, 12, 
    20000000,           -- basicSalary
    14615385,           -- baseSalary = 20tr * (19/26) = 14,615,385
    19,                 -- workDays - THIẾU 7 ngày (nghỉ nhiều)
    0,                  -- overtimeHours
    0,                  -- overtimePay
    0,                  -- bonus
    500000,             -- allowance
    500000,             -- deduction
    14615385,           -- totalSalary
    N'Nghỉ không lương 7 ngày (việc gia đình)', 
    GETDATE());

-- 11. Nguyễn Thị Lan (ID: 1020) - ĐỦ CÔNG, NHÂN VIÊN MỚI
-- Lương cơ bản: 10,000,000
INSERT INTO Salaries (EmployeeId, year, month, basicSalary, baseSalary, workDays, 
                      overtimeHours, overtimePay, bonus, allowance, deduction, totalSalary, notes, CreatedAt)
VALUES (1020, 2025, 12, 
    10000000,           -- basicSalary
    10000000,           -- baseSalary
    26,                 -- workDays - ĐỦ
    10,                 -- overtimeHours
    721154,             -- overtimePay
    0,                  -- bonus - Nhân viên mới chưa có thưởng
    300000,             -- allowance
    200000,             -- deduction
    10821154,           -- totalSalary
    N'Nhân viên mới, hoàn thành tốt công việc', 
    GETDATE());

-- =====================================================
-- KIỂM TRA KẾT QUẢ
-- =====================================================
SELECT 
    s.salaryId,
    e.employeeName AS N'Họ tên',
    s.year AS N'Năm',
    s.month AS N'Tháng',
    FORMAT(s.basicSalary, 'N0') AS N'Lương cơ bản',
    s.workDays AS N'Ngày công',
    s.overtimeHours AS N'Giờ OT',
    FORMAT(s.overtimePay, 'N0') AS N'Tiền OT',
    FORMAT(s.bonus, 'N0') AS N'Thưởng',
    FORMAT(s.allowance, 'N0') AS N'Phụ cấp',
    FORMAT(s.deduction, 'N0') AS N'Khấu trừ',
    FORMAT(s.totalSalary, 'N0') AS N'Tổng lương',
    s.notes AS N'Ghi chú'
FROM Salaries s
JOIN Employees e ON s.EmployeeId = e.employeeId
WHERE s.year = 2025 AND s.month = 12
ORDER BY e.employeeId;

-- =====================================================
-- TỔNG KẾT
-- =====================================================
PRINT N'';
PRINT N'========== TỔNG KẾT LƯƠNG THÁNG 12/2025 ==========';
PRINT N'';

SELECT 
    N'Tổng số nhân viên: ' + CAST(COUNT(*) AS NVARCHAR) AS N'Thống kê'
FROM Salaries WHERE year = 2025 AND month = 12
UNION ALL
SELECT 
    N'Đủ công (26 ngày): ' + CAST(COUNT(*) AS NVARCHAR) + N' người'
FROM Salaries WHERE year = 2025 AND month = 12 AND workDays = 26
UNION ALL
SELECT 
    N'Thiếu công: ' + CAST(COUNT(*) AS NVARCHAR) + N' người'
FROM Salaries WHERE year = 2025 AND month = 12 AND workDays < 26
UNION ALL
SELECT 
    N'Có OT (>0 giờ): ' + CAST(COUNT(*) AS NVARCHAR) + N' người'
FROM Salaries WHERE year = 2025 AND month = 12 AND overtimeHours > 0
UNION ALL
SELECT 
    N'OT nhiều (>=20 giờ): ' + CAST(COUNT(*) AS NVARCHAR) + N' người'
FROM Salaries WHERE year = 2025 AND month = 12 AND overtimeHours >= 20
UNION ALL
SELECT 
    N'Tổng chi lương: ' + FORMAT(SUM(totalSalary), 'N0') + N' VNĐ'
FROM Salaries WHERE year = 2025 AND month = 12;

GO
