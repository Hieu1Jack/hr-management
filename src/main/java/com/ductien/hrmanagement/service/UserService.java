package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.repository.EmployeeRepository;
import com.ductien.hrmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional     // -> Đảm bảo tất cả hàm trong service chạy trong 1 transaction
public class UserService {

    @Autowired
    private UserRepository userRepository;  
    // -> Tiêm repository để dùng các hàm CRUD

    @Autowired
    private PasswordEncoder passwordEncoder;
    // -> Dùng để mã hoá mật khẩu
    
    @Autowired
    private EmployeeRepository employeeRepository;
    // -> Dùng để tự động liên kết User với Employee

    public User createUser(User user) {
        // Kiểm tra username trùng
        if (userRepository.existsByUsername(user.getUsername())) {// Nếu username đã tồn tại
            throw new RuntimeException("Username already exists");
        }

        // Kiểm tra email trùng
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Mã hoá password trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Gán ngày tạo
        user.setCreatedAt(LocalDateTime.now());

        // User mặc định đang hoạt động
        user.setIsActive(true);

        // Mặc định không phải admin
        user.setIsAdmin(false);
        
        // TỰ ĐỘNG LIÊN KẾT với Employee nếu email khớp
        if (user.getEmployee() == null && user.getEmail() != null) {
            Employee employee = employeeRepository.findByEmail(user.getEmail());
            if (employee != null) {
                user.setEmployee(employee);
            }
        }

        // Lưu vào DB
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        // Lấy user theo ID
        Optional<User> existingUser = userRepository.findById(user.getUserId());// Tìm user theo ID

        if (existingUser.isPresent()) {// Nếu tìm thấy
            User u = existingUser.get();

            // Cập nhật thông tin cho user hiện tại
            u.setFullName(user.getFullName());
            u.setEmail(user.getEmail());
            u.setIsActive(user.getIsActive());
            u.setIsAdmin(user.getIsAdmin());

            // Cập nhật thời gian update
            u.setUpdatedAt(LocalDateTime.now());

            return userRepository.save(u);
        }

        throw new RuntimeException("User not found");
    }

    public void deleteUser(Integer userId) {
        // Xoá user theo ID
        userRepository.deleteById(userId);
    }

    public Optional<User> getUserById(Integer userId) {// Lấy user theo ID
        // Trả về Optional<User>
        return userRepository.findById(userId);// Tìm user theo ID
    }

    public Optional<User> getUserByUsername(String username) {// Lấy user theo username
        // Tìm user theo username
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        // Lấy tất cả user
        return userRepository.findAll();// Trả về danh sách tất cả user
    }

    public User changePassword(Integer userId, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            User u = user.get();

            // Kiểm tra mật khẩu cũ có đúng không
            if (passwordEncoder.matches(oldPassword, u.getPassword())) {

                // Mã hoá và đổi mật khẩu mới
                u.setPassword(passwordEncoder.encode(newPassword));

                // Cập nhật thời gian sửa đổi
                u.setUpdatedAt(LocalDateTime.now());

                return userRepository.save(u);
            }

            throw new RuntimeException("Old password is incorrect");
        }

        throw new RuntimeException("User not found");
    }

    public User setAdminStatus(Integer userId, Boolean isAdmin) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            User u = user.get();

            // Đặt trạng thái admin
            u.setIsAdmin(isAdmin);

            // Cập nhật thời gian
            u.setUpdatedAt(LocalDateTime.now());

            return userRepository.save(u);
        }

        throw new RuntimeException("User not found");
    }
    
    /**
     * Tự động liên kết User với Employee nếu chưa có liên kết
     * Tìm theo email trước, sau đó tìm theo MNV (username)
     */
    public User autoLinkEmployee(User user) {
        if (user.getEmployee() != null) {
            return user; // Đã có liên kết
        }
        
        // Tìm Employee theo email
        if (user.getEmail() != null) {
            Employee employee = employeeRepository.findByEmail(user.getEmail());
            if (employee != null) {
                user.setEmployee(employee);
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        
        // Tìm Employee theo MNV (username có thể là mã nhân viên)
        if (user.getUsername() != null) {
            Optional<Employee> employee = employeeRepository.findByMNV(user.getUsername().toUpperCase());
            if (employee.isPresent()) {
                user.setEmployee(employee.get());
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        
        return user; // Không tìm thấy Employee phù hợp
    }
    
    /**
     * Liên kết User với Employee cụ thể
     */
    public User linkEmployee(Integer userId, Integer employeeId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        if (employeeId != null) {
            Optional<Employee> empOpt = employeeRepository.findById(employeeId);
            if (empOpt.isPresent()) {
                user.setEmployee(empOpt.get());
            }
        } else {
            user.setEmployee(null);
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}
