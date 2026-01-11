package com.ductien.hrmanagement.controller.user;

import com.ductien.hrmanagement.entity.LeaveRequest;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.service.LeaveRequestService;
import com.ductien.hrmanagement.service.EmployeeService;
import com.ductien.hrmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@RequestMapping("/user/leaves")
public class UserLeaveRequestController {

    @Autowired private LeaveRequestService leaveRequestService;
    @Autowired private EmployeeService employeeService;
    @Autowired private UserService userService;

    // Xem lịch sử nghỉ phép
    @GetMapping
    public String viewLeaveHistory(Authentication authentication, Model model) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        
        if (user == null) return "redirect:/login";

        // Logic lấy nhân viên (giữ nguyên logic fallback của bạn)
        var employee = user.getEmployee();
        if (employee == null) employee = employeeService.getEmployeeByEmail(user.getEmail());
        if (employee == null) employee = employeeService.getEmployeeByMNV(username.toUpperCase());

        if (employee != null) {
            // Chỉ lấy danh sách của nhân viên này
            model.addAttribute("leaveRequests", leaveRequestService.getLeaveRequestsByEmployee(employee.getEmployeeId()));
            model.addAttribute("employee", employee);
        } else {
            model.addAttribute("leaveRequests", new ArrayList<>());
            model.addAttribute("errorMessage", "Tài khoản chưa liên kết thông tin nhân viên.");
        }
        
        return "user/leave-history";
    }

    // Form tạo đơn
    @GetMapping("/request")
    public String leaveRequestForm(Authentication authentication, Model model) {
        model.addAttribute("leaveRequest", new LeaveRequest());
        return "user/leave-request";
    }

    // Xử lý gửi đơn
    @PostMapping("/request")
    public String submitLeaveRequest(@ModelAttribute LeaveRequest leaveRequest, Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        
        if (user != null) {
            var employee = user.getEmployee();
            if (employee == null) employee = employeeService.getEmployeeByEmail(user.getEmail());
            if (employee == null) employee = employeeService.getEmployeeByMNV(username.toUpperCase());

            if (employee != null) {
                leaveRequest.setEmployee(employee);
                // Service đã tự set status = Pending
                leaveRequestService.createLeaveRequest(leaveRequest);
                return "redirect:/user/leaves?success";
            }
        }
        return "redirect:/user/leaves?error";
    }
}