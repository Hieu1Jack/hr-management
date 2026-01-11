package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    List<Salary> findByEmployeeEmployeeId(Integer employeeId);// Lấy lương theo nhân viên
    List<Salary> findByYearAndMonth(Integer year, Integer month);// Lấy lương theo năm và tháng
    List<Salary> findByEmployeeEmployeeIdAndYearAndMonth(Integer employeeId, Integer year, Integer month);// Lấy lương theo nhân viên, năm và tháng
    
    // Lấy lương theo phòng ban và tháng/năm
    List<Salary> findByEmployee_Department_DepartmentIdAndMonthAndYear(Integer departmentId, Integer month, Integer year);
    
    // Lấy lương theo phòng ban và năm
    List<Salary> findByEmployee_Department_DepartmentIdAndYear(Integer departmentId, Integer year);
    
    // Lấy lương theo tháng/năm
    List<Salary> findByMonthAndYear(Integer month, Integer year);
    
    // Lấy lương theo năm
    List<Salary> findByYear(Integer year);
    
    // Lấy lương theo nhân viên và tháng/năm
    List<Salary> findByEmployee_EmployeeIdAndMonthAndYear(Integer employeeId, Integer month, Integer year);
}
