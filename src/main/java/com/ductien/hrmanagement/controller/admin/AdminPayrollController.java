package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.*;
import com.ductien.hrmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Controller
@RequestMapping("/admin/salary")
public class AdminPayrollController {

    @Autowired private SalaryService salaryService;
    @Autowired private DepartmentService departmentService;
    @Autowired private EmployeeService employeeService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private PositionService positionService;

    // --- 1. DANH SÁCH NHÂN VIÊN & THỐNG KÊ CHẤM CÔNG ---
    @GetMapping// Hiển thị danh sách nhân viên chưa được tính lương
    public String listSalaries(@RequestParam(required = false) Integer departmentId,// Lọc theo phòng ban
                               @RequestParam(required = false) Integer month,
                               @RequestParam(required = false) Integer year,
                               Model model) {
        YearMonth now = YearMonth.now();// Lấy tháng năm hiện tại
        int m = (month == null) ? now.getMonthValue() : month;// Nếu month truyền vào là null thì lấy tháng hiện tại
        int y = (year == null) ? now.getYear() : year;// Nếu year truyền vào là null thì lấy năm hiện tại
        
        List<Department> departments = departmentService.getActiveDepartments();// Lấy danh sách phòng ban
        if (departmentId == null && !departments.isEmpty()) {// Nếu không có departmentId, chọn phòng ban đầu tiên
            departmentId = departments.get(0).getDepartmentId();// Gán departmentId bằng id của phòng ban đầu tiên
        }

        model.addAttribute("departments", departments);// Truyền danh sách phòng ban sang view
        model.addAttribute("selectedDepartmentId", departmentId);// Truyền phòng ban đã chọn sang view
        model.addAttribute("month", m);// Truyền tháng đã chọn sang view
        model.addAttribute("year", y);// Truyền năm đã chọn sang view

        if (departmentId != null) {// kiểm tra xem phòng ban đực chọn có nhân viên hay không
            List<Employee> allEmployees = employeeService.getEmployeesByDepartment(departmentId);// Lấy danh sách tất cả nhân viên trong phòng ban
            
            List<Employee> employees = allEmployees.stream() 
                .filter(emp -> salaryService.getSalariesByEmployeeAndMonth(emp.getEmployeeId(), m, y).isEmpty())
                .toList();
            
            model.addAttribute("employees", employees);// Truyền danh sách nhân viên chưa được tính lương sang view
            model.addAttribute("totalEmployees", allEmployees.size());// Tổng số nhân viên trong phòng ban
            model.addAttribute("calculatedCount", allEmployees.size() - employees.size());// Số nhân viên đã được tính lương
            
            Map<Integer, Map<String, Object>> stats = new HashMap<>();// Bản đồ lưu trữ thống kê chấm công
            for (Employee emp : employees) {// Lặp qua từng nhân viên
                // LOGIC MỚI: 8 tiếng = 1 ngày công, giờ thừa > 8h = OT
                Map<String, Double> workData = attendanceService.calculateWorkDaysAndOT(emp.getEmployeeId(), y, m);
                double workDays = workData.get("workDays");
                double otHours = workData.get("overtimeHours");
                
                stats.put(emp.getEmployeeId(), Map.of(
                    "workDays", workDays,// Số ngày công (tổng giờ / 8)
                    "overtimeHours", BigDecimal.valueOf(otHours),// Giờ OT (phần > 8h mỗi ngày)
                    "totalRegularHours", workData.get("totalRegularHours")// Tổng giờ làm chuẩn
                ));
            }
            model.addAttribute("attendanceStats", stats);// Truyền thống kê chấm công sang view
        }
        return "admin/salary/index";
    }

    // --- 2. DANH SÁCH LƯƠNG ĐÃ DUYỆT ---
    @GetMapping("/approved")
    public String approvedSalaries(@RequestParam(required = false) Integer departmentId,
                                   @RequestParam(required = false) Integer month,
                                   @RequestParam(required = false) Integer year,
                                   Model model) {
        int y = (year == null) ? YearMonth.now().getYear() : year;// Nếu year truyền vào là null thì lấy năm hiện tại
        
        List<Department> departments = departmentService.getActiveDepartments();// Lấy danh sách phòng ban
        // Nếu không có departmentId, chọn phòng ban đầu tiên
        Integer selectedDeptId = departmentId;
        if (selectedDeptId == null && !departments.isEmpty()) {
            selectedDeptId = departments.get(0).getDepartmentId();
        }
        
        List<Salary> salaries;// Danh sách lương đã duyệt
        if (selectedDeptId != null && month != null) salaries = salaryService.getSalariesByDepartmentAndMonth(selectedDeptId, month, y);// Lấy lương theo phòng ban và tháng
        else if (selectedDeptId != null) salaries = salaryService.getSalariesByDepartmentAndYear(selectedDeptId, y);// Lấy lương theo phòng ban và năm
        else if (month != null) salaries = salaryService.getSalariesByMonth(month, y);// Lấy lương theo tháng và năm
        else salaries = salaryService.getSalariesByYear(y);// Lấy lương theo năm

        model.addAttribute("departments", departments);// Truyền danh sách phòng ban sang view
        model.addAttribute("salaries", salaries);// Truyền danh sách lương đã duyệt sang view
        model.addAttribute("selectedDepartmentId", selectedDeptId);// Truyền phòng ban đã chọn sang view
        model.addAttribute("month", month);// Truyền tháng đã chọn sang view
        model.addAttribute("year", y);// Truyền năm đã chọn sang view   

        if (!salaries.isEmpty()) {// Nếu có lương đã duyệt, tính tổng và trung bình
            BigDecimal total = salaries.stream().map(Salary::getTotalSalary).reduce(BigDecimal.ZERO, BigDecimal::add);// Tính tổng lương
            model.addAttribute("totalAmount", total);
            model.addAttribute("averageAmount", total.divide(BigDecimal.valueOf(salaries.size()), 0, RoundingMode.HALF_UP));// Tính lương trung bình
        }
        return "admin/salary/approved";
    }

    // --- 3. TÍNH LƯƠNG TỰ ĐỘNG ---
    @PostMapping("/calculate-all")
    public String calculateAllSalaries(@RequestParam Integer departmentId, 
                                       @RequestParam Integer month, 
                                       @RequestParam Integer year, 
                                       RedirectAttributes redirectAttributes) {// Nhận yêu cầu tính lương cho tất cả nhân viên trong phòng ban
        List<Employee> employees = employeeService.getEmployeesByDepartment(departmentId);// Lấy danh sách nhân viên trong phòng ban
        int count = 0;// Biến đếm số nhân viên đã tính lương

        for (Employee emp : employees) {// Lặp qua từng nhân viên
            // Nếu đã tính rồi thì bỏ qua
            if (!salaryService.getSalariesByEmployeeAndMonth(emp.getEmployeeId(), month, year).isEmpty()) continue;//

            Salary salary = calculateSalaryData(emp, month, year);// Gọi hàm tính toán lương
            salaryService.createSalary(salary);// Lưu lương vào database
            count++;// Tăng biến đếm lên 1
        }
        redirectAttributes.addFlashAttribute("message", "Đã tính lương thành công cho " + count + " nhân viên.");
        return "redirect:/admin/salary/approved?departmentId=" + departmentId + "&month=" + month + "&year=" + year;
    }

    // --- 4. XÓA LƯƠNG ---
    @PostMapping("/delete/{id}")
    public String deleteSalary(@PathVariable Integer id) {
        Salary s = salaryService.getSalaryById(id).orElseThrow();
        salaryService.deleteSalary(id);
        return "redirect:/admin/salary/approved?month=" + s.getMonth() + "&year=" + s.getYear();
    }

    // --- HÀM TÍNH TOÁN LƯƠNG (DÙNG CHUNG) ---
    private Salary calculateSalaryData(Employee emp, int month, int year) {
        // 1. Lấy dữ liệu chấm công theo cơ chế MỚI: 8 tiếng = 1 ngày công
        Map<String, Double> workData = attendanceService.calculateWorkDaysAndOT(emp.getEmployeeId(), year, month);
        
        double workDays = workData.get("workDays"); // Số ngày công = tổng giờ / 8
        BigDecimal overtimeHours = BigDecimal.valueOf(workData.get("overtimeHours")); // Giờ OT (phần > 8h mỗi ngày)

        // 2. Lấy thông tin lương & hệ số
        BigDecimal baseSalary = BigDecimal.ZERO;// Lương cơ bản
        BigDecimal coefficient = BigDecimal.ONE;// Hệ số lương mặc định = 1
        if (emp.getPosition() != null) {// Kiểm tra nếu nhân viên có chức vụ
            if (emp.getPosition().getBaseSalary() != null) baseSalary = emp.getPosition().getBaseSalary();// Lấy lương cơ bản từ chức vụ
            if (emp.getPosition().getCoefficient() != null) coefficient = emp.getPosition().getCoefficient();// Lấy hệ số từ chức vụ
        }

        // 3. Tính toán tiền
        // Lương 1 ngày = Lương cơ bản / 26 (26 ngày công chuẩn/tháng)
        BigDecimal dailyRate = baseSalary.divide(BigDecimal.valueOf(26), 2, RoundingMode.HALF_UP);
        
        // Lương chính = Lương ngày * Số ngày công (8h = 1 ngày) * Hệ số
        BigDecimal basicPay = dailyRate.multiply(BigDecimal.valueOf(workDays)).multiply(coefficient);
        
        // Lương tăng ca
        BigDecimal hourlyRate = baseSalary.divide(BigDecimal.valueOf(208), 2, RoundingMode.HALF_UP);
        BigDecimal overtimePay = overtimeHours.multiply(hourlyRate).multiply(BigDecimal.valueOf(1.5));
        
        // Tổng lương
        BigDecimal total = basicPay.add(overtimePay);

        // 4. Tạo đối tượng Salary
        Salary salary = new Salary();
        salary.setEmployee(emp);// Gán nhân viên
        salary.setMonth(month);
        salary.setYear(year);
        salary.setBaseSalary(baseSalary);// Lương cơ bản
        salary.setWorkDays((int) Math.round(workDays));// Số ngày công (làm tròn từ tổng giờ / 8)
        salary.setBasicSalary(basicPay);// Lương chính
        salary.setOvertimeHours(overtimeHours);// Số giờ làm thêm
        salary.setOvertimePay(overtimePay);// Tiền làm thêm
        salary.setAllowance(overtimePay);// Phụ cấp (tạm gán bằng tiền làm thêm)
        salary.setBonus(BigDecimal.ZERO);// Tiền thưởng
        salary.setDeductions(BigDecimal.ZERO);// Tổng các khoản khấu trừ
        salary.setDeduction(BigDecimal.ZERO);// Tiền khấu trừ
        salary.setTotalSalary(total);// Tổng lương
        salary.setCreatedAt(LocalDateTime.now());// Thời gian tạo
        
        return salary;
    }
}