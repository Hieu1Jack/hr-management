package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.Role;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.service.RoleService;
import com.ductien.hrmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService; // Service để xử lý logic liên quan đến User
    
    @Autowired
    private RoleService roleService;// Service để xử lý logic liên quan đến Role

    @GetMapping
    public String listUsers(Model model) {//Hiển thị danh sách user
        List<User> users = userService.getAllUsers();// tạo danh sách user bằng cách gọi phương thức getAllUsers() từ UserService
        model.addAttribute("users", users);// Truyền danh sách user sang view
        return "admin/users/index";//
    }

    @GetMapping("/create")//Khi user bấm “Thêm mới” Server trả về giao diện để nhập dữ liệu
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());// Tạo một đối tượng User mới và truyền sang view
        // Load roles từ database
        List<Role> roles = roleService.getActiveRoles();// tạo danh sách role bằng cách gọi phương thức getActiveRoles() từ RoleService
        model.addAttribute("roles", roles);// Truyền danh sách role sang view
        return "admin/users/form";// Trả về view form để nhập dữ liệu user mới
    }

    @PostMapping("/create")//Nhận dữ liệu người dùng nhập và lưu vào DB Khi user bấm nút “Lưu” Client gửi dữ liệu từ form lên server bằng POST Server ghi dữ liệu vào database
    public String createUser(@ModelAttribute User user) {
        // Set isAdmin based on role
        user.setIsAdmin("Admin".equals(user.getRole()));// Kiểm tra nếu role là "Admin" thì đặt isAdmin thành true, ngược lại là false
        userService.createUser(user);// Gọi phương thức createUser() từ UserService để lưu user mới vào database
        return "redirect:/admin/users";// Chuyển hướng về trang danh sách user sau khi lưu thành công
    }

    @GetMapping("/edit/{id}")//Lấy thông tin  có id =  để hiển thị lên form
    public String editUserForm(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id).orElseThrow();// Lấy thông tin user từ database dựa trên id
        model.addAttribute("user", user);// Truyền thông tin user sang view
        // Load roles từ database
        List<Role> roles = roleService.getActiveRoles();// tạo danh sách role bằng cách gọi phương thức getActiveRoles() từ RoleService
        model.addAttribute("roles", roles);// Truyền danh sách role sang view
        return "admin/users/form";// Trả về view form để chỉnh sửa dữ liệu user
    }

    @PostMapping("/update")//Nhận dữ liệu người dùng nhập và cập nhật vào DB Khi user bấm nút “Cập nhật” Client gửi dữ liệu từ form lên server bằng POST Server ghi dữ liệu vào database
    public String updateUser(@ModelAttribute User user) {
        // Set isAdmin based on role
        user.setIsAdmin("Admin".equals(user.getRole()));// Kiểm tra nếu role là "Admin" thì đặt isAdmin thành true, ngược lại là false
        userService.updateUser(user);// Gọi phương thức updateUser() từ UserService để cập nhật user vào database
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")// xóa user có id đươc chỉ định khi bấm nút “Xóa” trên giao diện danh sách user  
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id); // Gọi phương thức deleteUser() từ UserService để xóa user khỏi database
        return "redirect:/admin/users";
    }

    @GetMapping("/setAdmin/{id}/{isAdmin}")// Cập nhật trạng thái admin cho user có id đươc chỉ định
    public String setAdminStatus(@PathVariable Integer id, @PathVariable Boolean isAdmin) {
        userService.setAdminStatus(id, isAdmin);// Gọi phương thức setAdminStatus() từ UserService để cập nhật trạng thái admin cho user
        return "redirect:/admin/users";// Chuyển hướng về trang danh sách user sau khi cập nhật thành công
    }
}
