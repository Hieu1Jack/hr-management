package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.*;
import com.ductien.hrmanagement.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/employees")
public class AdminEmployeeController {

    @Autowired private EmployeeService employeeService;
    @Autowired private UserService userService;
    @Autowired private DepartmentService departmentService;
    @Autowired private PositionService positionService;

    // --- 1. HIỂN THỊ DANH SÁCH (LOGIC LỌC) ---
    @GetMapping
    public String listEmployees(@RequestParam(required = false) Integer departmentId, Model model) {
        // Lấy danh sách phòng ban để đổ vào Dropdown
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        
        // Logic: Ưu tiên lấy ID từ URL (người dùng chọn). 
        // Nếu không có (departmentId == null) -> Lấy phòng ban ĐẦU TIÊN trong DB.
        Integer selectedDeptId = departmentId;
        if (selectedDeptId == null && !departments.isEmpty()) {
            selectedDeptId = departments.get(0).getDepartmentId();
        }
        
        // Lấy nhân viên theo ID phòng ban đã chốt
        List<Employee> employees = new ArrayList<>();
        if (selectedDeptId != null) {
            employees = employeeService.getEmployeesByDepartment(selectedDeptId);
        }
        
        // Gửi dữ liệu ra View
        model.addAttribute("employees", employees);
        model.addAttribute("selectedDepartmentId", selectedDeptId); // Để dropdown biết đang chọn phòng nào
        
        return "admin/employees/index"; // Trỏ về file giao diện danh sách Tailwind
    }

    // --- 2. FORM THÊM MỚI ---
    @GetMapping("/create")
    public String createEmployeeForm(Model model) {
        Employee employee = new Employee();
        employee.setMNV(employeeService.generateEmployeeCode());// tự động tạo mã nv
        
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("positions", positionService.getAllPositions());
        return "admin/employees/form"; // Trỏ về file form Tailwind
    }

    // --- 3. XỬ LÝ THÊM MỚI ---
    @PostMapping("/create")
    public String createEmployee(@ModelAttribute Employee employee, 
                                 @RequestParam(value = "deptId", required = false) Integer deptId) {
        
        // Xử lý thủ công Department để tránh lỗi binding
        if (deptId != null) {
            Department d = new Department();
            d.setDepartmentId(deptId);
            employee.setDepartment(d);
        }

        Employee savedEmployee = employeeService.createEmployee(employee);
        
        // Tạo User account (giữ nguyên logic của bạn)
        try {
            User user = new User();
            user.setUsername(savedEmployee.getMNV());
            user.setPassword(savedEmployee.getMNV());
            user.setEmail(savedEmployee.getEmail());
            user.setFullName(savedEmployee.getEmployeeName());
            user.setEmployee(savedEmployee);
            
            String specialRole = savedEmployee.getSpecialRole();
            if (specialRole != null && !specialRole.isEmpty()) {
                user.setRole(specialRole);
                user.setIsAdmin(true);
            } else {
                user.setRole("NHAN_VIEN");
                user.setIsAdmin(false);
            }
            userService.createUser(user);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        
        // REDIRECT: Quay về đúng phòng ban vừa thêm
        if (deptId != null) {
            return "redirect:/admin/employees?departmentId=" + deptId;
        }
        return "redirect:/admin/employees";
    }

    // --- 4. FORM SỬA ---
    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Integer id, Model model) {
        Employee employee = employeeService.getEmployeeById(id).orElseThrow();
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("positions", positionService.getAllPositions());
        return "admin/employees/form";
    }

    // --- 5. XỬ LÝ SỬA ---
    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute Employee employee,
                                 @RequestParam(value = "deptId", required = false) Integer deptId) {
        
        if (deptId != null) {
            Department d = new Department();
            d.setDepartmentId(deptId);
            employee.setDepartment(d);
        }
        
        employeeService.updateEmployee(employee);
        
        // Redirect về đúng phòng ban
        if (deptId != null) {
            return "redirect:/admin/employees?departmentId=" + deptId;
        }
        return "redirect:/admin/employees";
    }

    // --- 6. XÓA ---
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id) {
        // Lấy thông tin phòng ban cũ để redirect về đó sau khi xóa
        Employee e = employeeService.getEmployeeById(id).orElse(null);
        Integer deptId = (e != null && e.getDepartment() != null) ? e.getDepartment().getDepartmentId() : null;
        
        employeeService.deleteEmployee(id);
        
        if (deptId != null) {
            return "redirect:/admin/employees?departmentId=" + deptId;
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        if (file.isEmpty()) { ra.addFlashAttribute("error", "Vui lòng chọn file"); return "redirect:/admin/employees"; }
        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            int count = 0;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header
                String name = getCell(row, 0);
                if (name == null || name.isBlank()) continue;
                
                Employee e = new Employee();
                e.setEmployeeName(name);
                e.setEmail(getCell(row, 1));
                e.setPhone(getCell(row, 2));
                e.setGender(getCell(row, 3));
                e.setAddress(getCell(row, 4));
                e.setCCCD(getCell(row, 5));
                
                String dob = getCell(row, 6);
                if (dob != null && !dob.isBlank()) e.setDateOfBirth(LocalDate.parse(dob));
                String join = getCell(row, 7);
                if (join != null && !join.isBlank()) e.setJoinDate(LocalDate.parse(join));
                String salary = getCell(row, 8);
                if (salary != null && !salary.isBlank()) e.setBasicSalary(new BigDecimal(salary.replace(",", "")));
                
                // Tìm phòng ban và chức vụ theo tên
                String deptName = getCell(row, 9);
                if (deptName != null) departmentService.getAllDepartments().stream()
                    .filter(d -> d.getDepartmentName().equalsIgnoreCase(deptName.trim())).findFirst()
                    .ifPresent(e::setDepartment);
                String posName = getCell(row, 10);
                if (posName != null) positionService.getAllPositions().stream()
                    .filter(p -> p.getPositionName().equalsIgnoreCase(posName.trim())).findFirst()
                    .ifPresent(e::setPosition);
                
                e.setSpecialRole(getCell(row, 11));
                employeeService.createEmployee(e);
                
                // Tạo user tự động
                try {
                    User u = new User();
                    u.setUsername(e.getMNV());
                    u.setPassword(e.getMNV());
                    u.setEmail(e.getEmail());
                    u.setFullName(e.getEmployeeName());
                    u.setEmployee(e);
                    u.setRole(e.getSpecialRole() != null && !e.getSpecialRole().isEmpty() ? e.getSpecialRole() : "NHAN_VIEN");
                    u.setIsAdmin(e.getSpecialRole() != null && !e.getSpecialRole().isEmpty());
                    userService.createUser(u);
                } catch (Exception ignored) {}
                count++;
            }
            ra.addFlashAttribute("success", "Đã import " + count + " nhân viên");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/admin/employees";
    }
    
    private String getCell(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return null;
        c.setCellType(CellType.STRING);
        return c.getStringCellValue().trim();
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse res) throws IOException {
        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader("Content-Disposition", "attachment; filename=mau_nhanvien.xlsx");
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("Nhân viên");
            String[] h = {"Họ tên", "Email", "SĐT", "Giới tính", "Địa chỉ", "CCCD", "Ngày sinh (yyyy-MM-dd)", "Ngày vào (yyyy-MM-dd)", "Lương cơ bản", "Phòng ban", "Chức vụ", "Vai trò (TRUONG_PHONG/KE_TOAN_TRUONG)"};
            Row hr = s.createRow(0);
            CellStyle cs = wb.createCellStyle(); cs.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (int i = 0; i < h.length; i++) { Cell c = hr.createCell(i); c.setCellValue(h[i]); c.setCellStyle(cs); s.setColumnWidth(i, 5000); }
            Row ex = s.createRow(1);
            String[] v = {"Nguyễn Văn A", "nva@company.com", "0901234567", "Nam", "123 Nguyễn Huệ", "079123456789", "1990-05-15", "2024-01-01", "10000000", "Phòng IT", "Nhân viên", ""};
            for (int i = 0; i < v.length; i++) ex.createCell(i).setCellValue(v[i]);
            wb.write(res.getOutputStream());
        }
    }

    @GetMapping("/export")
    public void exportExcel(@RequestParam(required = false) Integer departmentId, HttpServletResponse res) throws IOException {
        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader("Content-Disposition", "attachment; filename=danhsach_nhanvien.xlsx");
        List<Employee> list = departmentId != null ? employeeService.getEmployeesByDepartment(departmentId) : employeeService.getAllEmployees();
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet("Nhân viên");
            String[] h = {"Mã NV", "Họ tên", "Email", "SĐT", "Giới tính", "Địa chỉ", "CCCD", "Ngày sinh", "Ngày vào", "Lương cơ bản", "Phòng ban", "Chức vụ", "Vai trò", "Trạng thái"};
            Row hr = s.createRow(0);
            CellStyle cs = wb.createCellStyle(); cs.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (int i = 0; i < h.length; i++) { Cell c = hr.createCell(i); c.setCellValue(h[i]); c.setCellStyle(cs); s.setColumnWidth(i, 4500); }
            int r = 1;
            for (Employee e : list) {
                Row row = s.createRow(r++);
                row.createCell(0).setCellValue(e.getMNV() != null ? e.getMNV() : "");
                row.createCell(1).setCellValue(e.getEmployeeName() != null ? e.getEmployeeName() : "");
                row.createCell(2).setCellValue(e.getEmail() != null ? e.getEmail() : "");
                row.createCell(3).setCellValue(e.getPhone() != null ? e.getPhone() : "");
                row.createCell(4).setCellValue(e.getGender() != null ? e.getGender() : "");
                row.createCell(5).setCellValue(e.getAddress() != null ? e.getAddress() : "");
                row.createCell(6).setCellValue(e.getCCCD() != null ? e.getCCCD() : "");
                row.createCell(7).setCellValue(e.getDateOfBirth() != null ? e.getDateOfBirth().toString() : "");
                row.createCell(8).setCellValue(e.getJoinDate() != null ? e.getJoinDate().toString() : "");
                row.createCell(9).setCellValue(e.getBasicSalary() != null ? e.getBasicSalary().toString() : "");
                row.createCell(10).setCellValue(e.getDepartment() != null ? e.getDepartment().getDepartmentName() : "");
                row.createCell(11).setCellValue(e.getPosition() != null ? e.getPosition().getPositionName() : "");
                row.createCell(12).setCellValue(e.getSpecialRole() != null ? e.getSpecialRole() : "");
                row.createCell(13).setCellValue(e.getIsActive() ? "Hoạt động" : "Đã nghỉ");
            }
            wb.write(res.getOutputStream());
        }
    }
}
