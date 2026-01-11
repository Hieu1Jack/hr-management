package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.User;
import com.ductien.hrmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User u = user.get();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Phân quyền dựa trên role
            String role = u.getRole();
            if (u.getIsAdmin() || "ADMIN".equals(role)) {// Nếu isAdmin là true hoặc role là "ADMIN"
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));// Thêm quyền ROLE_ADMIN
            }
            
            if ("KE_TOAN_TRUONG".equals(role)) {// Nếu role là "KE_TOAN_TRUONG"
                authorities.add(new SimpleGrantedAuthority("ROLE_KE_TOAN_TRUONG"));// Thêm quyền ROLE_KE_TOAN_TRUONG
                authorities.add(new SimpleGrantedAuthority("ROLE_SPECIAL")); // Quyền truy cập admin hạn chế
            }
            
            if ("TRUONG_PHONG".equals(role)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_TRUONG_PHONG"));
                authorities.add(new SimpleGrantedAuthority("ROLE_SPECIAL")); // Quyền truy cập admin hạn chế
            }
            
            if ("GiamDoc".equalsIgnoreCase(role) || "GIAM_DOC".equals(role)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_GIAM_DOC"));
                authorities.add(new SimpleGrantedAuthority("ROLE_SPECIAL")); // Quyền truy cập admin hạn chế (chỉ lương & nghỉ phép)
            }
            
            // Kế toán chỉ được nhập lương
            if ("KE_TOAN".equals(role) || "KETOAN".equalsIgnoreCase(role)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_KE_TOAN"));
            }
            
            // Mọi user đều có ROLE_USER
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                u.getIsActive(),
                true,
                true,
                true,
                authorities
            );
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
