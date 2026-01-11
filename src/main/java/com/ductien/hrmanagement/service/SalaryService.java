package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Salary;
import com.ductien.hrmanagement.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    public Salary createSalary(Salary salary) {
        salary.setCreatedAt(LocalDateTime.now());
        return salaryRepository.save(salary);
    }

    public Salary updateSalary(Salary salary) {// Cập nhật thông tin lương
        Optional<Salary> existing = salaryRepository.findById(salary.getSalaryId());// Tìm lương theo ID
        if (existing.isPresent()) {// Nếu tìm thấy
            Salary s = existing.get();// Lấy đối tượng lương hiện tại
            s.setBasicSalary(salary.getBasicSalary());// Cập nhật các trường thông tin
            s.setAllowance(salary.getAllowance());// Cập nhật các trường thông tin
            s.setDeduction(salary.getDeduction());
            s.setTotalSalary(salary.getTotalSalary());
            s.setUpdatedAt(LocalDateTime.now());
            return salaryRepository.save(s);
        }
        throw new RuntimeException("Salary not found");
    }

    public void deleteSalary(Integer salaryId) {
        salaryRepository.deleteById(salaryId);
    }

    public Optional<Salary> getSalaryById(Integer salaryId) {// Lấy thông tin lương theo ID
        return salaryRepository.findById(salaryId);
    }

    public List<Salary> getAllSalaries() {// Lấy tất cả lương
        return salaryRepository.findAll();
    }

    public List<Salary> getSalariesByEmployee(Integer employeeId) {// Lấy lương theo nhân viên
        return salaryRepository.findByEmployeeEmployeeId(employeeId);
    }

    public List<Salary> getSalariesByYearMonth(Integer year, Integer month) {
        return salaryRepository.findByYearAndMonth(year, month);
    }

    public List<Salary> getSalariesByDepartmentAndMonth(Integer departmentId, Integer month, Integer year) {
        return salaryRepository.findByEmployee_Department_DepartmentIdAndMonthAndYear(departmentId, month, year);
    }
    
    public List<Salary> getSalariesByDepartmentAndYear(Integer departmentId, Integer year) {
        return salaryRepository.findByEmployee_Department_DepartmentIdAndYear(departmentId, year);
    }
    
    public List<Salary> getSalariesByMonth(Integer month, Integer year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }
    
    public List<Salary> getSalariesByYear(Integer year) {
        return salaryRepository.findByYear(year);
    }
    
    public List<Salary> getSalariesByEmployeeAndMonth(Integer employeeId, Integer month, Integer year) {
        return salaryRepository.findByEmployee_EmployeeIdAndMonthAndYear(employeeId, month, year);
    }
}
