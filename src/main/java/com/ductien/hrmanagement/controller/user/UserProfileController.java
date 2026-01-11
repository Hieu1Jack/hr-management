package com.ductien.hrmanagement.controller.user;

import com.ductien.hrmanagement.entity.Contract;
import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.service.ContractService;
import com.ductien.hrmanagement.service.EmployeeService;
import com.ductien.hrmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContractService contractService;

    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) return "redirect:/login";
        
        // Tự động liên kết User với Employee nếu chưa có
        user = userService.autoLinkEmployee(user);
        
        // Lấy employee từ relationship trực tiếp, fallback qua email, sau đó MNV
        var employee = user.getEmployee();
        if (employee == null) {
            employee = employeeService.getEmployeeByEmail(user.getEmail());
        }
        if (employee == null) {
            // Fallback: tìm theo MNV (username = employeeCode)
            employee = employeeService.getEmployeeByMNV(username.toUpperCase());
        }
        
        model.addAttribute("user", user);
        if (employee != null) {
            model.addAttribute("employee", employee);
            // Lấy hợp đồng hiện tại của nhân viên
            List<Contract> contracts = contractService.getContractsByEmployee(employee.getEmployeeId());
            // Lấy hợp đồng đang có hiệu lực (Active)
            Contract currentContract = contracts.stream()
                .filter(c -> "Active".equalsIgnoreCase(c.getContractStatus()) || "Còn hiệu lực".equals(c.getContractStatus()))
                .findFirst()
                .orElse(contracts.isEmpty() ? null : contracts.get(0));
            model.addAttribute("contract", currentContract);
            model.addAttribute("contracts", contracts);
        }
        return "user/profile";
    }

    @GetMapping("/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) return "redirect:/login";
        
        // Lấy employee từ relationship trực tiếp, fallback qua email, sau đó MNV
        var employee = user.getEmployee();
        if (employee == null) {
            employee = employeeService.getEmployeeByEmail(user.getEmail());
        }
        if (employee == null) {
            // Fallback: tìm theo MNV (username = employeeCode)
            employee = employeeService.getEmployeeByMNV(username.toUpperCase());
        }
        
        model.addAttribute("user", user);
        if (employee != null) {
            model.addAttribute("employee", employee);
        }
        return "user/profile-edit";
    }

    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute Employee employee,
            Authentication authentication,
            Model model) {
        try {
            employeeService.updateEmployee(employee);
            model.addAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi cập nhật thông tin!");
            return "user/profile-edit";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            Model model) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) return "redirect:/login";
        
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu mới không trùng khớp!");
            return "user/change-password";
        }
        
        try {
            userService.changePassword(user.getUserId(), oldPassword, newPassword);
            model.addAttribute("successMessage", "Thay đổi mật khẩu thành công!");
            return "redirect:/user/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Thay đổi mật khẩu thất bại: " + e.getMessage());
            return "user/change-password";
        }
    }
}
