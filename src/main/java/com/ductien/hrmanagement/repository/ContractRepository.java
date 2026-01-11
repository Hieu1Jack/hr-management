package com.ductien.hrmanagement.repository;

import com.ductien.hrmanagement.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    List<Contract> findByEmployeeEmployeeId(Integer employeeId);
    List<Contract> findByContractStatus(String contractStatus);
    
    @Query("SELECT c FROM Contract c WHERE c.employee.department.departmentId = :departmentId")
    List<Contract> findByDepartmentId(@Param("departmentId") Integer departmentId);
}
