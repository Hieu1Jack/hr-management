package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.Role;
import com.ductien.hrmanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller                           // -> Đây là controller xử lý request cho trang Admin
@RequestMapping("/admin/roles")       // -> URL gốc cho tất cả API quản lý Role
public class AdminRoleController {

    @Autowired
    private RoleService roleService;  // -> Inject RoleService để thao tác dữ liệu

    @GetMapping
    public String listRoles(Model model) {
        List<Role> roles = roleService.getAllRoles();  
        // -> Lấy danh sách tất cả vai trò

        model.addAttribute("roles", roles);
        // -> Truyền danh sách role sang view

        return "admin/roles/index";   
        // -> Trả về file giao diện: templates/admin/roles/list.html
    }

    @GetMapping("/create")
    public String createRoleForm(Model model) {
        model.addAttribute("role", new Role());  
        // -> Truyền object Role trống sang form để binding

        return "admin/roles/form";   
        // -> Trả về giao diện form thêm mới
    }

    @PostMapping("/create")
    public String createRole(@ModelAttribute Role role) {
        roleService.createRole(role);
        // -> Lưu role mới vào database

        return "redirect:/admin/roles";
        // -> Quay lại trang danh sách sau khi tạo
    }

    @GetMapping("/edit/{id}")
    public String editRoleForm(@PathVariable Integer id, Model model) {
        Role role = roleService.getRoleById(id).orElseThrow();
        // -> Lấy role theo ID, nếu không có thì throw exception

        model.addAttribute("role", role);
        // -> Truyền role sang form để hiển thị dữ liệu

        return "admin/roles/form";   
        // -> Dùng chung template form để sửa
    }

    @PostMapping("/edit/{id}")
    public String updateRoleById(@PathVariable Integer id, @ModelAttribute Role role) {
        roleService.updateRole(id, role);
        // -> Cập nhật role theo ID

        return "redirect:/admin/roles";
        // -> Chuyển về danh sách roles
    }

    @PostMapping("/update")
    public String updateRole(@ModelAttribute Role role) {
        roleService.updateRole(role.getRoleId(), role);
        // -> Hàm update khác cho trường hợp form gửi data có field roleId

        return "redirect:/admin/roles";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);  
        // -> Xóa role theo ID

        return "redirect:/admin/roles";  
        // -> Quay lại trang danh sách
    }
}
