package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    
    // Tìm theo employee ID
    List<LeaveRequest> findByEmployeeEmployeeId(Integer employeeId);
    
    // Tìm theo status
    List<LeaveRequest> findByStatus(String status);
    
    // Tìm theo employee và status
    List<LeaveRequest> findByEmployeeEmployeeIdAndStatus(Integer employeeId, String status);
    
    // ✅ Tìm theo phòng ban - ORDER BY mới nhất
    @Query("SELECT l FROM LeaveRequest l WHERE l.employee.department.departmentId = :departmentId ORDER BY l.createdAt DESC")
    List<LeaveRequest> findByEmployeeDepartmentDepartmentId(@Param("departmentId") Integer departmentId);
    
    // ✅ Tìm theo phòng ban VÀ status
    @Query("SELECT l FROM LeaveRequest l WHERE l.employee.department.departmentId = :departmentId AND l.status = :status ORDER BY l.createdAt DESC")
    List<LeaveRequest> findByEmployeeDepartmentDepartmentIdAndStatus(
        @Param("departmentId") Integer departmentId, 
        @Param("status") String status
    );
    
    // ✅ Tìm tất cả, ORDER BY mới nhất
    @Query("SELECT l FROM LeaveRequest l ORDER BY l.createdAt DESC")
    List<LeaveRequest> findAllOrderByCreatedAtDesc();
}