package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    List<Role> findByIsActiveTrue();
    // -> Lấy tất cả Role có isActive = true
    // -> Dùng để load danh sách quyền đang được kích hoạt

    Optional<Role> findByRoleName(String roleName);
    // -> Tìm role theo tên quyền (ví dụ: “ADMIN”, “NHAN_VIEN”, “TRUONG_PHONG”)
    // -> Trả về Optional để tránh lỗi NullPointerException
}
