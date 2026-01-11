package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.*;
import com.ductien.hrmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/contracts")
public class AdminContractController {

    @Value("${app.upload.dir:uploads/contracts}")
    private String uploadDir;

    @Autowired private ContractService contractService;
    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentService departmentService;

    // --- 1. DANH SÁCH (FILTER) ---
    @GetMapping
    public String list(@RequestParam(required = false) Integer departmentId,
                       @RequestParam(required = false) String contractType,
                       @RequestParam(required = false) String status, 
                       Model model) {
        
        List<Department> departments = departmentService.getActiveDepartments();
        model.addAttribute("departments", departments);

        // Logic chọn phòng ban mặc định
        Integer selectedDeptId = departmentId;
        if (selectedDeptId == null && !departments.isEmpty()) {
            selectedDeptId = departments.get(0).getDepartmentId();
        }

        // Lấy danh sách hợp đồng theo phòng ban
        List<Contract> contracts = contractService.getContractsByDepartment(selectedDeptId);
        
        // Filter in-memory (hoặc chuyển xuống Service/Repository nếu dữ liệu lớn)
        LocalDate now = LocalDate.now();
        if (contractType != null && !contractType.isEmpty()) {
            contracts = contracts.stream().filter(c -> contractType.equals(c.getContractType())).collect(Collectors.toList());
        }
        
        if ("active".equals(status)) {
            contracts = contracts.stream().filter(c -> c.getEndDate() == null || c.getEndDate().isAfter(now)).collect(Collectors.toList());
        } else if ("expired".equals(status)) {
            contracts = contracts.stream().filter(c -> c.getEndDate() != null && !c.getEndDate().isAfter(now)).collect(Collectors.toList());
        }

        model.addAttribute("contracts", contracts);
        model.addAttribute("selectedDepartmentId", selectedDeptId);
        model.addAttribute("selectedContractType", contractType);
        model.addAttribute("selectedStatus", status);
        
        return "admin/contracts/index";
    }

    // --- 2. FORM TẠO MỚI ---
    @GetMapping("/create")
    public String createForm(@RequestParam(required = false) Integer departmentId, Model model) {
        List<Department> depts = departmentService.getActiveDepartments();
        
        // Mặc định chọn phòng đầu tiên nếu chưa chọn
        if (departmentId == null && !depts.isEmpty()) {
            departmentId = depts.get(0).getDepartmentId();
        }
        
        model.addAttribute("contract", new Contract());
        model.addAttribute("departments", depts);
        model.addAttribute("selectedDepartmentId", departmentId);
        
        // Load nhân viên thuộc phòng ban đã chọn
        List<Employee> employees = (departmentId != null) ? employeeService.getEmployeesByDepartment(departmentId) : new ArrayList<>();
        model.addAttribute("employees", employees);
        
        return "admin/contracts/form";
    }

    // --- 3. XỬ LÝ TẠO MỚI ---
    @PostMapping("/create")
    public String create(@ModelAttribute Contract contract, 
                         @RequestParam(value = "empId", required = false) Integer empId, // Lấy ID nhân viên thủ công
                         @RequestParam(required = false) Integer departmentId,
                         @RequestParam(required = false) MultipartFile contractFileUpload, 
                         Model model) {
        
        if (empId == null) {
            return "redirect:/admin/contracts/create?departmentId=" + departmentId;
        }

        Employee emp = employeeService.getEmployeeById(empId).orElse(null);
        if (emp != null) {
            contract.setEmployee(emp);
        }

        // Xử lý file
        if (contractFileUpload != null && !contractFileUpload.isEmpty()) {
            contract.setContractFile(saveFile(contractFileUpload));
        }

        contractService.createContract(contract);
        
        // Redirect về đúng phòng ban
        return "redirect:/admin/contracts?departmentId=" + (departmentId != null ? departmentId : "");
    }

    // --- 4. FORM SỬA ---
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Contract contract = contractService.getContractById(id).orElseThrow();
        model.addAttribute("contract", contract);
        model.addAttribute("departments", departmentService.getActiveDepartments());
        
        // Logic: Lấy phòng ban của nhân viên trong hợp đồng để hiển thị đúng
        Integer deptId = null;
        if (contract.getEmployee() != null && contract.getEmployee().getDepartment() != null) {
            deptId = contract.getEmployee().getDepartment().getDepartmentId();
        }
        model.addAttribute("selectedDepartmentId", deptId);
        
        // Load danh sách nhân viên của phòng ban đó (để nếu muốn đổi nhân viên khác cùng phòng)
        if (deptId != null) {
            model.addAttribute("employees", employeeService.getEmployeesByDepartment(deptId));
        } else {
            model.addAttribute("employees", employeeService.getAllEmployees());
        }

        return "admin/contracts/form";
    }

    // --- 5. XỬ LÝ SỬA ---
    @PostMapping("/update")
    public String update(@ModelAttribute Contract contract, 
                         @RequestParam(value = "empId", required = false) Integer empId,
                         @RequestParam(required = false) MultipartFile contractFileUpload) {
        
        // Cập nhật nhân viên nếu có thay đổi
        if (empId != null) {
            Employee emp = employeeService.getEmployeeById(empId).orElse(null);
            contract.setEmployee(emp);
        }

        // Xử lý file
        if (contractFileUpload != null && !contractFileUpload.isEmpty()) {
            contract.setContractFile(saveFile(contractFileUpload));
        } else {
            // Giữ lại file cũ nếu không upload mới
            contractService.getContractById(contract.getContractId())
                .ifPresent(c -> contract.setContractFile(c.getContractFile()));
        }

        contractService.updateContract(contract);
        
        // Lấy ID phòng ban để redirect
        Integer deptId = (contract.getEmployee() != null && contract.getEmployee().getDepartment() != null) 
                         ? contract.getEmployee().getDepartment().getDepartmentId() : null;

        return "redirect:/admin/contracts" + (deptId != null ? "?departmentId=" + deptId : "");
    }

    // --- XÓA ---
    @PostMapping("/delete/{id}") // Đổi thành PostMapping để an toàn hơn hoặc dùng Get nếu form list dùng Get
    public String delete(@PathVariable Integer id) {
        Contract c = contractService.getContractById(id).orElse(null);
        Integer deptId = (c != null && c.getEmployee() != null && c.getEmployee().getDepartment() != null) 
                         ? c.getEmployee().getDepartment().getDepartmentId() : null;
        
        contractService.deleteContract(id);
        
        return "redirect:/admin/contracts" + (deptId != null ? "?departmentId=" + deptId : "");
    }

    // --- DOWNLOAD FILE ---
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Integer id) {
        var contract = contractService.getContractById(id).orElseThrow();
        if (contract.getContractFile() == null) return ResponseEntity.notFound().build();
        try {
            var resource = new UrlResource(Paths.get(uploadDir).resolve(contract.getContractFile()).normalize().toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();
            
            // Lấy đuôi file
            String ext = contract.getContractFile().contains(".") ? contract.getContractFile().substring(contract.getContractFile().lastIndexOf('.') + 1) : "dat";
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + contract.getEmployee().getEmployeeName() + "_HopDong." + ext + "\"")
                .body(resource);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    // --- HELPER SAVE FILE ---
    private String saveFile(MultipartFile file) {
        try {
            var path = Paths.get(uploadDir);
            if (!Files.exists(path)) Files.createDirectories(path);
            String name = UUID.randomUUID() + "." + (file.getOriginalFilename().contains(".") ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1) : "");
            Files.copy(file.getInputStream(), path.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            return name;
        } catch (Exception e) { throw new RuntimeException("Lỗi lưu file: " + e.getMessage()); }
    }
}