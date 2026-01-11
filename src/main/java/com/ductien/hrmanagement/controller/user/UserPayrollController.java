package com.ductien.hrmanagement.controller.user;

import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.entity.Salary;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.service.SalaryService;
import com.ductien.hrmanagement.service.EmployeeService;
import com.ductien.hrmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user/salary")
public class UserPayrollController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    /**
     * Hiển thị danh sách lương của nhân viên đang đăng nhập
     */
    @GetMapping
    public String viewSalaryHistory(Authentication authentication, Model model) {
        Employee employee = getCurrentEmployee(authentication);// Lấy thông tin nhân viên từ user đang đăng nhập
        
        model.addAttribute("currentYear", LocalDate.now().getYear());// Lấy năm hiện tại để hiển thị trên giao diện
        
        if (employee != null) {// Kiểm tra nếu nhân viên tồn tại
            List<Salary> salaries = salaryService.getSalariesByEmployee(employee.getEmployeeId());// Lấy danh sách lương của nhân viên
            model.addAttribute("salaries", salaries);// Truyền danh sách lương sang view
            model.addAttribute("employee", employee);// Truyền thông tin nhân viên sang view
        } else {
            model.addAttribute("salaries", new ArrayList<>());// Nếu không tìm thấy nhân viên, truyền danh sách rỗng
            model.addAttribute("errorMessage", "Tài khoản chưa được liên kết với nhân viên. Vui lòng liên hệ Admin.");
        }
        
        return "user/salary";
    }

    /**
     * Xem chi tiết một phiếu lương
     */
    @GetMapping("/detail/{id}")
    public String viewSalaryDetail(@PathVariable Integer id, Authentication authentication, Model model) {
        Employee employee = getCurrentEmployee(authentication);// Lấy thông tin nhân viên từ user đang đăng nhập
        Salary salary = salaryService.getSalaryById(id).orElse(null);// Lấy thông tin lương theo id
        
        // Kiểm tra quyền: chỉ xem được lương của chính mình
        if (salary == null || employee == null || 
            !salary.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {// Nếu không tìm thấy lương hoặc nhân viên hoặc lương không thuộc về nhân viên đang đăng nhập
            return "redirect:/user/salary";
        }
        
        model.addAttribute("salary", salary);// Truyền thông tin lương sang view
        model.addAttribute("employee", employee);
        return "user/salary-detail";
    }

    /**
     * Lấy thông tin nhân viên của user đang đăng nhập
     */
    private Employee getCurrentEmployee(Authentication authentication) {
        if (authentication == null) return null;// Kiểm tra nếu authentication là null
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();// Lấy username từ authentication
        User user = userService.getUserByUsername(username).orElse(null);// Lấy thông tin user từ database
        if (user == null) return null;// Kiểm tra nếu user không tồn tại
        
        // Lấy employee từ relationship trực tiếp
        Employee employee = user.getEmployee();// Lấy thông tin nhân viên từ user qua quan hệ trực tiếp
        
        // Fallback qua email
        if (employee == null) {
            employee = employeeService.getEmployeeByEmail(user.getEmail());
        }
        
        // Fallback qua MNV (username = employeeCode)
        if (employee == null) {// Kiểm tra nếu vẫn chưa tìm thấy nhân viên
            employee = employeeService.getEmployeeByMNV(username.toUpperCase());// Lấy thông tin nhân viên từ mã nhân viên (employeeCode)
        }
        
        return employee;
    }
}
