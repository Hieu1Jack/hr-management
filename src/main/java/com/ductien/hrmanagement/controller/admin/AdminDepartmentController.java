package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.Department;
import com.ductien.hrmanagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/departments")
public class AdminDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "admin/departments/index";
    }

    @GetMapping("/create")
    public String createDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "admin/departments/form";
    }

    @PostMapping("/create")
    public String createDepartment(@ModelAttribute Department department) {
        departmentService.createDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/edit/{id}")
    public String editDepartmentForm(@PathVariable Integer id, Model model) {
        Department department = departmentService.getDepartmentById(id).orElseThrow();
        model.addAttribute("department", department);
        return "admin/departments/form";
    }

    @PostMapping("/update")
    public String updateDepartment(@ModelAttribute Department department) {
        departmentService.updateDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Integer id) {
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }
}
