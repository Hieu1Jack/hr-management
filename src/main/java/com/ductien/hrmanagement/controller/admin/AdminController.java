package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.Department;
import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.entity.LeaveRequest;
import com.ductien.hrmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private LeaveRequestService leaveRequestService;
    
    @Autowired
    private SalaryService salaryService;

    @GetMapping({"" , "/dashboard"})
    public String adminDashboard(Model model) {
        // Lấy danh sách nhân viên đang hoạt động
        List<Employee> activeEmployees = employeeService.getActiveEmployees();
        List<Employee> allEmployees = employeeService.getAllEmployees();
        
        // Tổng số nhân viên (đang hoạt động)
        model.addAttribute("totalEmployees", activeEmployees.size());
        
        // Nhân viên mới trong tháng này (joinDate trong tháng hiện tại)
        LocalDate now = LocalDate.now();
        long newEmployeesThisMonth = activeEmployees.stream()
            .filter(e -> e.getJoinDate() != null 
                && e.getJoinDate().getMonth() == now.getMonth() 
                && e.getJoinDate().getYear() == now.getYear())
            .count();
        model.addAttribute("newEmployees", newEmployeesThisMonth);
        
        // Số phòng ban
        model.addAttribute("departmentCount", departmentService.getActiveDepartments().size());
        
        // Số chức vụ
        model.addAttribute("positionCount", positionService.getAllPositions().size());
        
        // Số hợp đồng
        model.addAttribute("contractCount", contractService.getAllContracts().size());
        
        // Số tài khoản user
        model.addAttribute("userCount", userService.getAllUsers().size());
        
        // Số đơn nghỉ phép đang chờ duyệt (Pending)
        List<LeaveRequest> pendingLeaves = leaveRequestService.getLeaveRequestsByStatus("Pending");
        model.addAttribute("pendingLeaveRequests", pendingLeaves.size());
        
        // Thống kê nhân viên theo phòng ban
        List<Department> departments = departmentService.getActiveDepartments();
        List<Map<String, Object>> departmentStats = new ArrayList<>();
        int maxEmployeeCount = 1; // Tránh chia cho 0
        
        for (Department dept : departments) {
            List<Employee> deptEmployees = employeeService.getEmployeesByDepartment(dept.getDepartmentId());
            int count = (int) deptEmployees.stream().filter(Employee::getIsActive).count();
            if (count > maxEmployeeCount) maxEmployeeCount = count;
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("name", dept.getDepartmentName());
            stat.put("count", count);
            departmentStats.add(stat);
        }
        
        // Tính phần trăm height cho biểu đồ
        for (Map<String, Object> stat : departmentStats) {
            int count = (int) stat.get("count");
            int heightPercent = (int) ((count * 100.0) / maxEmployeeCount);
            stat.put("heightPercent", Math.max(heightPercent, 5)); // Tối thiểu 5%
        }
        
        model.addAttribute("departmentStats", departmentStats);
        
        // Hoạt động gần đây - Đơn nghỉ phép mới nhất
        List<LeaveRequest> recentLeaves = leaveRequestService.getAllLeaveRequests()
            .stream()
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentLeaves", recentLeaves);
        
        // Nhân viên mới nhất
        List<Employee> recentEmployees = allEmployees.stream()
            .filter(e -> e.getCreatedAt() != null)
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(3)
            .collect(Collectors.toList());
        model.addAttribute("recentEmployees", recentEmployees);
        
        return "dashboard";
    }
}
