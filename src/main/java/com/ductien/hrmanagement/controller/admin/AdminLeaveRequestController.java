package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.Department;
import com.ductien.hrmanagement.entity.LeaveRequest;
import com.ductien.hrmanagement.service.LeaveRequestService;
import com.ductien.hrmanagement.service.EmployeeService;
import com.ductien.hrmanagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/leaves")
public class AdminLeaveRequestController {

    @Autowired private LeaveRequestService leaveRequestService;
    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentService departmentService;

    // Các hằng số trạng thái (Khớp với Service)
    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_APPROVED = "Approved";
    private static final String STATUS_REJECTED = "Rejected";

    // 1. Trang duyệt đơn (Chỉ hiện Pending)
    @GetMapping
    public String listPendingLeaves(@RequestParam(required = false) Integer departmentId, Model model) {
        List<Department> departments = departmentService.getActiveDepartments();
        model.addAttribute("departments", departments);
        
        // Nếu không có departmentId, chọn phòng ban đầu tiên
        Integer selectedDeptId = departmentId;
        if (selectedDeptId == null && !departments.isEmpty()) {
            selectedDeptId = departments.get(0).getDepartmentId();
        }
        model.addAttribute("selectedDepartmentId", selectedDeptId);
        
        List<LeaveRequest> leaves;
        if (selectedDeptId != null) {
            leaves = leaveRequestService.getLeaveRequestsByDepartmentAndStatus(selectedDeptId, STATUS_PENDING);
        } else {
            leaves = leaveRequestService.getLeaveRequestsByStatus(STATUS_PENDING);
        }
        
        model.addAttribute("leaves", leaves);
        model.addAttribute("pendingCount", leaves.size());
        
        return "admin/leaves/index";
    }
    
    // 2. Trang lịch sử đã duyệt/từ chối
    @GetMapping("/approved")
    public String listApprovedLeaves(@RequestParam(required = false) Integer departmentId,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(required = false) String monthYear,
                                     Model model) {
        List<Department> departments = departmentService.getActiveDepartments();
        model.addAttribute("departments", departments);
        
        // Nếu không có departmentId, chọn phòng ban đầu tiên
        Integer selectedDeptId = departmentId;
        if (selectedDeptId == null && !departments.isEmpty()) {
            selectedDeptId = departments.get(0).getDepartmentId();
        }
        model.addAttribute("selectedDepartmentId", selectedDeptId);
        model.addAttribute("status", status);
        model.addAttribute("monthYear", monthYear);
        
        // Lấy danh sách gốc theo phòng ban
        List<LeaveRequest> allLeaves;
        if (selectedDeptId != null) {
            allLeaves = leaveRequestService.getLeaveRequestsByDepartment(selectedDeptId);
        } else {
            allLeaves = leaveRequestService.getAllLeaveRequests();
        }

        // Lọc dữ liệu: Loại bỏ Pending + Filter theo status & tháng
        List<LeaveRequest> filteredLeaves = allLeaves.stream()
            .filter(l -> !STATUS_PENDING.equals(l.getStatus()) && !"Chờ duyệt".equals(l.getStatus())) // Loại bỏ đơn chờ duyệt
            .filter(l -> (status == null || status.isEmpty()) || l.getStatus().equals(status)) // Lọc theo status (nếu có)
            .filter(l -> { // Lọc theo tháng (nếu có)
                if (monthYear == null || monthYear.isEmpty()) return true;
                return YearMonth.from(l.getStartDate()).equals(YearMonth.parse(monthYear));
            })
            .collect(Collectors.toList());
        
        // Thống kê
        long approvedCount = filteredLeaves.stream().filter(l -> STATUS_APPROVED.equals(l.getStatus())).count();
        long rejectedCount = filteredLeaves.stream().filter(l -> STATUS_REJECTED.equals(l.getStatus())).count();
        
        model.addAttribute("leaves", filteredLeaves);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        
        return "admin/leaves/approved";
    }

    // --- CÁC HÀM XỬ LÝ (ACTION) ---

    @PostMapping("/approve/{id}")
    public String approveLeaveRequest(@PathVariable Integer id) {
        // Có thể thay "Admin" bằng tên user đang đăng nhập nếu có Spring Security
        leaveRequestService.approveLeaveRequest(id, "Admin"); 
        return "redirect:/admin/leaves";
    }

    @PostMapping("/reject/{id}")
    public String rejectLeaveRequest(@PathVariable Integer id) {
        leaveRequestService.rejectLeaveRequest(id);
        return "redirect:/admin/leaves";
    }

    @PostMapping("/delete/{id}")
    public String deleteLeaveRequest(@PathVariable Integer id) {
        leaveRequestService.deleteLeaveRequest(id);
        return "redirect:/admin/leaves";
    }
    
    // --- CÁC FORM CRUD (NẾU CẦN) ---
    // (Giữ lại nếu bạn có tính năng Admin tự tạo đơn cho nhân viên)
    
    @GetMapping("/create")
    public String createLeaveRequestForm(Model model) {
        model.addAttribute("leaveRequest", new LeaveRequest());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/leaves/form";
    }

    @PostMapping("/create")
    public String createLeaveRequest(@ModelAttribute LeaveRequest leaveRequest) {
        leaveRequestService.createLeaveRequest(leaveRequest);
        return "redirect:/admin/leaves";
    }

    @GetMapping("/edit/{id}")
    public String editLeaveRequestForm(@PathVariable Integer id, Model model) {
        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequestById(id).orElseThrow();
        model.addAttribute("leaveRequest", leaveRequest);
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "admin/leaves/form";
    }

    @PostMapping("/update")
    public String updateLeaveRequest(@ModelAttribute LeaveRequest leaveRequest) {
        leaveRequestService.updateLeaveRequest(leaveRequest);
        return "redirect:/admin/leaves";
    }
}