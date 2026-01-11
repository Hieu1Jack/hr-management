package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Sinh mã nhân viên tự động theo format: NV001, NV002, ...
     */
    public String generateEmployeeCode() {
        String maxMNV = employeeRepository.findMaxMNV();
        if (maxMNV == null || maxMNV.isEmpty()) {
            return "NV001";
        }
        // Lấy số từ mã NV (bỏ 2 ký tự "NV")
        String numberPart = maxMNV.substring(2);
        int nextNumber = Integer.parseInt(numberPart) + 1;
        return String.format("NV%03d", nextNumber);
    }

    public Employee createEmployee(Employee employee) {
        // Tự động sinh mã nhân viên nếu chưa có
        if (employee.getMNV() == null || employee.getMNV().isEmpty()) {
            employee.setMNV(generateEmployeeCode());
        }
        employee.setCreatedAt(LocalDateTime.now());
        employee.setIsActive(true);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        Optional<Employee> existing = employeeRepository.findById(employee.getEmployeeId());
        if (existing.isPresent()) {
            Employee e = existing.get();
            e.setEmployeeName(employee.getEmployeeName());
            e.setEmail(employee.getEmail());
            e.setPhone(employee.getPhone());
            e.setAddress(employee.getAddress());
            e.setDateOfBirth(employee.getDateOfBirth());
            e.setGender(employee.getGender());
            e.setDepartment(employee.getDepartment());
            e.setPosition(employee.getPosition());
            e.setJoinDate(employee.getJoinDate());
            e.setBasicSalary(employee.getBasicSalary());
            e.setIsActive(employee.getIsActive());
            e.setSpecialRole(employee.getSpecialRole());
            e.setUpdatedAt(LocalDateTime.now());
            return employeeRepository.save(e);
        }
        throw new RuntimeException("Employee not found");
    }

    public void deleteEmployee(Integer employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    public Optional<Employee> getEmployeeById(Integer employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByIsActive(true);
    }

    public List<Employee> getEmployeesByDepartment(Integer departmentId) {
        return employeeRepository.findByDepartmentDepartmentId(departmentId);
    }

    public List<Employee> getEmployeesByPosition(Integer positionId) {
        return employeeRepository.findByPositionPositionId(positionId);
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
    
    public Employee getEmployeeByMNV(String mnv) {
        return employeeRepository.findByMNV(mnv).orElse(null);
    }
}
