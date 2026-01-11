package com.ductien.hrmanagement.config;

import com.ductien.hrmanagement.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Demo data in database.sql is stored in plain text (e.g., 123456),
        // so use NoOpPasswordEncoder to avoid bcrypt mismatch.
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Static resources - cho tất cả
                .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // Login/logout - cho tất cả
                .requestMatchers("/login", "/logout").permitAll()
                // Trang chủ - cho tất cả
                .requestMatchers("/", "/index").permitAll()
                // Kế toán chỉ được truy cập phần lương
                .requestMatchers("/admin/salary/**").hasAnyRole("ADMIN", "KE_TOAN_TRUONG", "KE_TOAN")
                .requestMatchers("/admin/dashboard").hasAnyRole("ADMIN", "KE_TOAN_TRUONG", "KE_TOAN", "TRUONG_PHONG", "GIAM_DOC")
                // Admin full quyền (trừ kế toán)
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "KE_TOAN_TRUONG", "TRUONG_PHONG", "GIAM_DOC")
                // User pages - yêu cầu đăng nhập
                .requestMatchers("/user/**").authenticated()
                // Còn lại - cho tất cả
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // Điều hướng dựa trên role
                    boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    boolean isSpecial = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_KE_TOAN_TRUONG") 
                                    || a.getAuthority().equals("ROLE_TRUONG_PHONG")
                                    || a.getAuthority().equals("ROLE_GIAM_DOC"));
                    boolean isKeToan = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_KE_TOAN"));
                    
                    if (isAdmin || isSpecial) {
                        response.sendRedirect("/admin/dashboard");
                    } else if (isKeToan) {
                        // Kế toán chuyển đến trang lương
                        response.sendRedirect("/admin/salary");
                    } else {
                        response.sendRedirect("/user/dashboard");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
