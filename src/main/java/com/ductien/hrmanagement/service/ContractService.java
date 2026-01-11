package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Contract;
import com.ductien.hrmanagement.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    public Contract createContract(Contract contract) {
        contract.setCreatedAt(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    public Contract updateContract(Contract contract) {
        Optional<Contract> existing = contractRepository.findById(contract.getContractId());
        if (existing.isPresent()) {
            Contract c = existing.get();
            c.setContractType(contract.getContractType());
            c.setStartDate(contract.getStartDate());
            c.setEndDate(contract.getEndDate());
            c.setSalary(contract.getSalary());
            c.setContractStatus(contract.getContractStatus());
            c.setDescription(contract.getDescription());
            c.setContractFile(contract.getContractFile());
            c.setUpdatedAt(LocalDateTime.now());
            return contractRepository.save(c);
        }
        throw new RuntimeException("Contract not found");
    }

    public void deleteContract(Integer contractId) {
        contractRepository.deleteById(contractId);
    }

    public Optional<Contract> getContractById(Integer contractId) {
        return contractRepository.findById(contractId);
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public List<Contract> getContractsByEmployee(Integer employeeId) {
        return contractRepository.findByEmployeeEmployeeId(employeeId);
    }

    public List<Contract> getContractsByStatus(String status) {
        return contractRepository.findByContractStatus(status);
    }

    public List<Contract> getContractsByDepartment(Integer departmentId) {
        return contractRepository.findByDepartmentId(departmentId);
    }
}
