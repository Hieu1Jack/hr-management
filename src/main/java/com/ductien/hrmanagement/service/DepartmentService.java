package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Department;
import com.ductien.hrmanagement.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        department.setCreatedAt(LocalDateTime.now());
        department.setIsActive(true);
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Department department) {
        Optional<Department> existing = departmentRepository.findById(department.getDepartmentId());
        if (existing.isPresent()) {
            Department d = existing.get();
            d.setDepartmentName(department.getDepartmentName());
            d.setDescription(department.getDescription());
            d.setIsActive(department.getIsActive());
            d.setUpdatedAt(LocalDateTime.now());
            return departmentRepository.save(d);
        }
        throw new RuntimeException("Department not found");
    }

    public void deleteDepartment(Integer departmentId) {
        departmentRepository.deleteById(departmentId);
    }

    public Optional<Department> getDepartmentById(Integer departmentId) {
        return departmentRepository.findById(departmentId);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> getActiveDepartments() {
        return departmentRepository.findByIsActive(true);
    }
}
