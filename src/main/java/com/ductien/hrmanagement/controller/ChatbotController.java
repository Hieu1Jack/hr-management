package com.ductien.hrmanagement.controller;

import com.ductien.hrmanagement.entity.*;
import com.ductien.hrmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private SalaryService salaryService;
    
    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private AttendanceService attendanceService;

    /**
     * API lấy context của user hiện tại để gửi cho AI
     */
    @GetMapping("/context")
    public ResponseEntity<Map<String, Object>> getUserContext(Authentication auth) {
        Map<String, Object> context = new HashMap<>();
        
        if (auth == null || !auth.isAuthenticated()) {
            context.put("error", "Chưa đăng nhập");
            return ResponseEntity.ok(context);
        }
        
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.getUserByUsername(username);
            
            if (userOpt.isEmpty()) {
                context.put("error", "Không tìm thấy user");
                return ResponseEntity.ok(context);
            }
            
            User user = userOpt.get();
            
            // Thông tin cơ bản
            context.put("fullName", user.getFullName());
            context.put("username", user.getUsername());
            context.put("email", user.getEmail());
            context.put("role", user.getRole());
            context.put("isAdmin", user.getIsAdmin());
            
            Employee employee = user.getEmployee();
            if (employee != null) {
                // Thông tin nhân viên
                Map<String, Object> empInfo = new HashMap<>();
                empInfo.put("employeeId", employee.getEmployeeId());
                empInfo.put("employeeName", employee.getEmployeeName());
                empInfo.put("MNV", employee.getMNV());
                empInfo.put("phone", employee.getPhone());
                empInfo.put("email", employee.getEmail());
                empInfo.put("address", employee.getAddress());
                empInfo.put("gender", employee.getGender());
                empInfo.put("CCCD", employee.getCCCD());
                
                if (employee.getDateOfBirth() != null) {
                    empInfo.put("dateOfBirth", employee.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                if (employee.getJoinDate() != null) {
                    empInfo.put("joinDate", employee.getJoinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                
                // Phòng ban & Chức vụ
                if (employee.getDepartment() != null) {
                    empInfo.put("department", employee.getDepartment().getDepartmentName());
                }
                if (employee.getPosition() != null) {
                    empInfo.put("position", employee.getPosition().getPositionName());
                }
                
                // Lương cơ bản
                if (employee.getBasicSalary() != null) {
                    empInfo.put("basicSalary", formatCurrency(employee.getBasicSalary().longValue()));
                }
                
                context.put("employee", empInfo);
                
                // Lấy thông tin lương tháng hiện tại và tháng trước
                LocalDate now = LocalDate.now();
                List<Map<String, Object>> salaryHistory = new ArrayList<>();
                
                // Lương 3 tháng gần nhất
                for (int i = 0; i < 3; i++) {
                    LocalDate monthDate = now.minusMonths(i);
                    List<Salary> salaries = salaryService.getSalariesByEmployeeAndMonth(
                        employee.getEmployeeId(), monthDate.getMonthValue(), monthDate.getYear());
                    
                    if (!salaries.isEmpty()) {
                        Salary sal = salaries.get(0);
                        Map<String, Object> salInfo = new HashMap<>();
                        salInfo.put("month", sal.getMonth());
                        salInfo.put("year", sal.getYear());
                        salInfo.put("basicSalary", sal.getBasicSalary() != null ? formatCurrency(sal.getBasicSalary().longValue()) : "0");
                        salInfo.put("allowance", sal.getAllowance() != null ? formatCurrency(sal.getAllowance().longValue()) : "0");
                        salInfo.put("bonus", sal.getBonus() != null ? formatCurrency(sal.getBonus().longValue()) : "0");
                        salInfo.put("deduction", sal.getDeduction() != null ? formatCurrency(sal.getDeduction().longValue()) : "0");
                        salInfo.put("totalSalary", sal.getTotalSalary() != null ? formatCurrency(sal.getTotalSalary().longValue()) : "0");
                        salInfo.put("workDays", sal.getWorkDays());
                        salInfo.put("overtimeHours", sal.getOvertimeHours());
                        salaryHistory.add(salInfo);
                    }
                }
                context.put("salaryHistory", salaryHistory);
                
                // Thống kê nghỉ phép
                List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employee.getEmployeeId());
                int totalLeaveDays = 0;
                int pendingLeaves = 0;
                int approvedLeaves = 0;
                
                for (LeaveRequest lr : leaveRequests) {
                    if ("Approved".equals(lr.getStatus())) {
                        totalLeaveDays += lr.getNumberOfDays() != null ? lr.getNumberOfDays() : 0;
                        approvedLeaves++;
                    } else if ("Pending".equals(lr.getStatus())) {
                        pendingLeaves++;
                    }
                }
                
                Map<String, Object> leaveInfo = new HashMap<>();
                leaveInfo.put("totalLeaveDaysUsed", totalLeaveDays);
                leaveInfo.put("remainingDays", 12 - totalLeaveDays); // Giả sử 12 ngày phép/năm
                leaveInfo.put("pendingRequests", pendingLeaves);
                leaveInfo.put("approvedRequests", approvedLeaves);
                context.put("leaveInfo", leaveInfo);
                
                // Thống kê chấm công tháng hiện tại
                Long workDaysThisMonth = attendanceService.getFullDaysCount(
                    employee.getEmployeeId(), now.getYear(), now.getMonthValue());
                Double overtimeThisMonth = attendanceService.getTotalOvertimeHours(
                    employee.getEmployeeId(), now.getYear(), now.getMonthValue());
                
                Map<String, Object> attendanceInfo = new HashMap<>();
                attendanceInfo.put("workDaysThisMonth", workDaysThisMonth);
                attendanceInfo.put("overtimeHoursThisMonth", Math.round(overtimeThisMonth * 10.0) / 10.0);
                attendanceInfo.put("currentMonth", now.getMonthValue());
                attendanceInfo.put("currentYear", now.getYear());
                
                // Kiểm tra đã check-in/check-out hôm nay chưa
                Optional<Attendance> todayAttendance = attendanceService.getTodayAttendance(employee.getEmployeeId());
                if (todayAttendance.isPresent()) {
                    Attendance att = todayAttendance.get();
                    attendanceInfo.put("checkedInToday", true);
                    attendanceInfo.put("checkInTime", att.getCheckInTime() != null ? att.getCheckInTime().toString() : null);
                    attendanceInfo.put("checkOutTime", att.getCheckOutTime() != null ? att.getCheckOutTime().toString() : null);
                } else {
                    attendanceInfo.put("checkedInToday", false);
                }
                
                context.put("attendanceInfo", attendanceInfo);
            }
            
            context.put("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
        } catch (Exception e) {
            context.put("error", "Lỗi khi lấy thông tin: " + e.getMessage());
        }
        
        return ResponseEntity.ok(context);
    }
    
    private String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VNĐ";
    }
}
