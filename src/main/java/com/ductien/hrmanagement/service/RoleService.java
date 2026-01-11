package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Role;
import com.ductien.hrmanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional       // -> Tự động quản lý transaction cho tất cả method
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    // -> Tiêm RoleRepository để thao tác CRUD với bảng Role

    public Role createRole(Role role) {
        role.setCreatedAt(LocalDateTime.now());
        // -> Gán thời gian tạo mới: trường createdAt

        return roleRepository.save(role);
        // -> Lưu role mới vào database
    }

    public Role updateRole(Integer id, Role roleDetails) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        // -> Lấy Role theo ID, trả về Optional để tránh NullPointer

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();

            // -> Cập nhật các thuộc tính từ roleDetails
            role.setRoleName(roleDetails.getRoleName()); 
            // -> Cập nhật tên quyền

            role.setDescription(roleDetails.getDescription());
            // -> Cập nhật mô tả quyền

            role.setIsActive(roleDetails.getIsActive());
            // -> Cập nhật trạng thái hoạt động

            role.setUpdatedAt(LocalDateTime.now());
            // -> Gán thời gian update

            return roleRepository.save(role);
            // -> Lưu lại Role đã chỉnh sửa
        }

        throw new RuntimeException("Role not found with id: " + id);
        // -> Nếu ID không tồn tại → báo lỗi
    }

    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
        // -> Xoá role theo ID (xóa cứng)
    }

    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
        // -> Trả về Role theo ID
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
        // -> Lấy toàn bộ Role trong database
    }

    public List<Role> getActiveRoles() {
        return roleRepository.findByIsActiveTrue();
        // -> Lấy tất cả Role có isActive = true
    }

    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
        // -> Lấy Role theo tên quyền (roleName), ví dụ: "ADMIN", "NHAN_VIEN"
    }
}
