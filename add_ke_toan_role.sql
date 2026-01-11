-- Script thêm role KE_TOAN (Kế toán) vào database
-- Role này chỉ có quyền nhập lương, không có các quyền admin khác

-- Thêm role KE_TOAN nếu chưa tồn tại
IF NOT EXISTS (SELECT 1 FROM Roles WHERE roleName = 'KE_TOAN')
BEGIN
    INSERT INTO Roles (roleName, description, isActive, CreatedAt)
    VALUES ('KE_TOAN', N'Kế toán - Chỉ được nhập lương', 1, GETDATE());
    PRINT N'Đã thêm role KE_TOAN';
END
ELSE
BEGIN
    PRINT N'Role KE_TOAN đã tồn tại';
END

-- Để gán role KE_TOAN cho một user, chạy lệnh sau:
-- UPDATE Users SET role = 'KE_TOAN' WHERE username = 'ten_user_ke_toan';

-- Ví dụ: Tạo user kế toán mới
-- INSERT INTO Users (username, password, email, role, isAdmin, isActive)
-- VALUES ('ketoan1', '123456', 'ketoan1@company.com', 'KE_TOAN', 0, 1);
