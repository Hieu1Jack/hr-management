package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByIsActive(Boolean isActive);
    List<Employee> findByDepartmentDepartmentId(Integer departmentId);
    List<Employee> findByPositionPositionId(Integer positionId);
    Employee findByEmail(String email);
    Optional<Employee> findByMNV(String mnv);
    
    // Tìm mã nhân viên lớn nhất để sinh mã mới
    @Query("SELECT MAX(e.MNV) FROM Employee e WHERE e.MNV LIKE 'NV%'")
    String findMaxMNV();
}
