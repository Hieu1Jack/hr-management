package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.Attendance;
import com.ductien.hrmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    // 1. Tìm chấm công theo ngày (Dùng cho Check-in/Check-out)
    Optional<Attendance> findByEmployeeEmployeeIdAndWorkDate(Integer employeeId, LocalDate workDate);

    // 2. Tìm chấm công trong khoảng thời gian (Dùng để hiển thị lịch sử & tính lương)
    List<Attendance> findByEmployeeEmployeeIdAndWorkDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);

    // --- CÁC QUERY TÍNH TOÁN TRỰC TIẾP TRONG DB ---

    @Query("SELECT a FROM Attendance a WHERE a.employee.employeeId = :employeeId AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month ORDER BY a.workDate DESC")// lấy danh sách chấm công theo nhân viên ngày , tháng năm , sắp xếp giảm dần theo ngày
    List<Attendance> findByEmployeeIdAndYearMonth(@Param("employeeId") Integer employeeId, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT a FROM Attendance a WHERE YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month ORDER BY a.workDate DESC")
    List<Attendance> findByYearMonth(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(a.regularHours) FROM Attendance a WHERE a.employee.employeeId = :employeeId AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month")// tổng giờ công chuẩn trong tháng
    Double sumRegularHoursByEmployeeAndMonth(@Param("employeeId") Integer employeeId, @Param("year") int year, @Param("month") int month);
    
    @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.employee.employeeId = :employeeId AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month")// tổng giờ tăng ca trong tháng
    Double sumOvertimeHoursByEmployeeAndMonth(@Param("employeeId") Integer employeeId, @Param("year") int year, @Param("month") int month);
    
    // [QUAN TRỌNG] Đếm ngày công: Tính cả 'Present' (Đúng giờ) và 'Late' (Đi muộn)
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.employeeId = :employeeId AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month AND (a.status = 'Present' OR a.status = 'Late')")// đếm số ngày làm việc (cả đúng giờ và đi muộn) trong tháng
    Long countFullDaysByEmployeeAndMonth(@Param("employeeId") Integer employeeId, @Param("year") int year, @Param("month") int month);// đếm số ngày làm việc đầy đủ (8h trở lên) trong tháng
}