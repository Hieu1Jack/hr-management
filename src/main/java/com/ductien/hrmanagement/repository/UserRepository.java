package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);  
    // -> Truy vấn tìm User theo username, trả về Optional để tránh NullPointer

    Optional<User> findByEmail(String email);
    // -> Tìm User theo email

    boolean existsByUsername(String username);
    // -> Kiểm tra username đã tồn tại chưa

    boolean existsByEmail(String email);
    // -> Kiểm tra email đã tồn tại chưa
}
