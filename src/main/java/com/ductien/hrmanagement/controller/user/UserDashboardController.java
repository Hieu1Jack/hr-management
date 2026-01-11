package com.ductien.hrmanagement.controller.user;

import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.entity.LeaveRequest;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.service.AttendanceService;
import com.ductien.hrmanagement.service.EmployeeService;
import com.ductien.hrmanagement.service.LeaveRequestService;
import com.ductien.hrmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        // Lấy employee từ relationship trực tiếp, fallback qua email, sau đó MNV
        Employee employee = user.getEmployee();
        if (employee == null) {
            employee = employeeService.getEmployeeByEmail(user.getEmail());
        }
        if (employee == null) {
            // Fallback: tìm theo MNV (username = employeeCode)
            employee = employeeService.getEmployeeByMNV(username.toUpperCase());
        }

        if (employee != null) {
            model.addAttribute("employee", employee);
            model.addAttribute("user", user);

            // Lấy danh sách nghỉ phép gần đây
            List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employee.getEmployeeId());
            model.addAttribute("recentLeaves", leaveRequests.stream().limit(5).toList());

            // Đếm số đơn chờ duyệt
            long pendingCount = leaveRequests.stream()
                    .filter(l -> "Pending".equals(l.getStatus()))
                    .count();
            model.addAttribute("pendingRequests", pendingCount);

            // Số ngày nghỉ còn lại (mặc định 12 ngày/năm)
            long approvedDays = leaveRequests.stream()
                    .filter(l -> "Approved".equals(l.getStatus()))
                    .mapToInt(l -> l.getNumberOfDays() != null ? l.getNumberOfDays() : 0)
                    .sum();
            model.addAttribute("remainingLeaves", Math.max(0, 12 - approvedDays));

            // Thông tin chấm công
            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();
            
            // Chấm công hôm nay
            var todayAttendance = attendanceService.getTodayAttendance(employee.getEmployeeId());
            model.addAttribute("todayAttendance", todayAttendance.orElse(null));
            
            // Trạng thái check-in/out
            boolean canCheckIn = todayAttendance.isEmpty() || todayAttendance.get().getCheckInTime() == null;
            boolean canCheckOut = todayAttendance.isPresent() && 
                                  todayAttendance.get().getCheckInTime() != null && 
                                  todayAttendance.get().getCheckOutTime() == null;
            model.addAttribute("canCheckIn", canCheckIn);
            model.addAttribute("canCheckOut", canCheckOut);

            // Tổng giờ làm tháng này
            Double totalRegular = attendanceService.getTotalRegularHours(employee.getEmployeeId(), currentYear, currentMonth);
            Double totalOvertime = attendanceService.getTotalOvertimeHours(employee.getEmployeeId(), currentYear, currentMonth);
            Long fullDays = attendanceService.getFullDaysCount(employee.getEmployeeId(), currentYear, currentMonth);
            
            model.addAttribute("totalRegularHours", totalRegular);
            model.addAttribute("totalOvertimeHours", totalOvertime);
            model.addAttribute("fullDaysCount", fullDays);
            model.addAttribute("totalWorkHours", String.format("%.1fh", totalRegular + totalOvertime));
        }

        return "user/dashboard";
    }
}
