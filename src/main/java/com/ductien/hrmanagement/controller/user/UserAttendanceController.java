package com.ductien.hrmanagement.controller.user;

import com.ductien.hrmanagement.entity.Attendance;
import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.service.AttendanceService;
import com.ductien.hrmanagement.service.EmployeeService;
import com.ductien.hrmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/user/attendance")
public class UserAttendanceController {

    @Autowired private AttendanceService attendanceService;
    @Autowired private EmployeeService employeeService;
    @Autowired private UserService userService;

    // Lấy Employee từ user đang đăng nhập
    private Employee getEmployee(Authentication auth) {
        if (auth == null) return null;
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) return null;
        
        Employee emp = user.getEmployee();
        if (emp == null) emp = employeeService.getEmployeeByEmail(user.getEmail());
        if (emp == null) emp = employeeService.getEmployeeByMNV(username.toUpperCase());
        return emp;
    }

    @GetMapping
    public String attendancePage(Authentication auth, Model model,
                                  @RequestParam(required = false) Integer year,
                                  @RequestParam(required = false) Integer month) {
        if (auth == null) return "redirect:/login";
        
        Employee emp = getEmployee(auth);
        int y = year != null ? year : LocalDate.now().getYear();
        int m = month != null ? month : LocalDate.now().getMonthValue();
        
        model.addAttribute("selectedYear", y);
        model.addAttribute("selectedMonth", m);
        model.addAttribute("currentYear", LocalDate.now().getYear());
        
        if (emp == null) {
            model.addAttribute("errorMessage", "Tài khoản chưa được liên kết với nhân viên. Vui lòng liên hệ Admin.");
            model.addAttribute("canCheckIn", false);
            model.addAttribute("canCheckOut", false);
            return "user/attendance";
        }

        var today = attendanceService.getTodayAttendance(emp.getEmployeeId());
        model.addAttribute("todayAttendance", today.orElse(null));
        model.addAttribute("canCheckIn", today.isEmpty() || today.get().getCheckInTime() == null);
        model.addAttribute("canCheckOut", today.isPresent() && today.get().getCheckInTime() != null && today.get().getCheckOutTime() == null);
        model.addAttribute("attendanceHistory", attendanceService.getAttendanceByMonth(emp.getEmployeeId(), y, m));
        model.addAttribute("totalRegularHours", attendanceService.getTotalRegularHours(emp.getEmployeeId(), y, m));
        model.addAttribute("totalOvertimeHours", attendanceService.getTotalOvertimeHours(emp.getEmployeeId(), y, m));
        model.addAttribute("fullDaysCount", attendanceService.getFullDaysCount(emp.getEmployeeId(), y, m));
        model.addAttribute("employee", emp);

        return "user/attendance";
    }

    @PostMapping("/check-in")
    public String checkIn(Authentication auth, RedirectAttributes ra) {
        Employee emp = getEmployee(auth);
        if (emp == null) {
            ra.addFlashAttribute("errorMessage", "Tài khoản chưa được liên kết với nhân viên.");
            return "redirect:/user/attendance";
        }
        try {
            Attendance att = attendanceService.checkIn(emp);
            ra.addFlashAttribute("successMessage", "Check-in thành công lúc " + att.getCheckInTime());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/attendance";
    }

    @PostMapping("/check-out")
    public String checkOut(Authentication auth, RedirectAttributes ra) {
        Employee emp = getEmployee(auth);
        if (emp == null) {
            ra.addFlashAttribute("errorMessage", "Tài khoản chưa được liên kết với nhân viên.");
            return "redirect:/user/attendance";
        }
        try {
            Attendance att = attendanceService.checkOut(emp);
            ra.addFlashAttribute("successMessage", String.format(
                "Check-out thành công! Tổng: %.1fh (Chuẩn: %.1f, Tăng ca: %.1f)",
                att.getTotalHours(), att.getRegularHours(), att.getOvertimeHours()));
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/attendance";
    }
}
