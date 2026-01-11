package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.LeaveRequest;
import com.ductien.hrmanagement.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_APPROVED = "Approved";
    private static final String STATUS_REJECTED = "Rejected";

    // --- CÁC HÀM GET DATA ---
    
    public List<LeaveRequest> getAllLeaveRequests() { 
        return leaveRequestRepository.findAllOrderByCreatedAtDesc(); 
    }

    public List<LeaveRequest> getLeaveRequestsByEmployee(Integer employeeId) { 
        return leaveRequestRepository.findByEmployeeEmployeeId(employeeId); 
    }

    public Optional<LeaveRequest> getLeaveRequestById(Integer id) { 
        return leaveRequestRepository.findById(id); 
    }

    public List<LeaveRequest> getLeaveRequestsByStatus(String status) { 
        return leaveRequestRepository.findByStatus(status); 
    }

    public List<LeaveRequest> getLeaveRequestsByDepartment(Integer deptId) { 
        return leaveRequestRepository.findByEmployeeDepartmentDepartmentId(deptId); 
    }

    public List<LeaveRequest> getLeaveRequestsByDepartmentAndStatus(Integer departmentId, String status) {
        return leaveRequestRepository.findByEmployeeDepartmentDepartmentIdAndStatus(departmentId, status);
    }

    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequest.setStatus(STATUS_PENDING); 
        if (leaveRequest.getCreatedAt() == null) leaveRequest.setCreatedAt(LocalDateTime.now());
        return leaveRequestRepository.save(leaveRequest);
    }
    
    public LeaveRequest updateLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        return leaveRequestRepository.save(leaveRequest);
    }

    public void approveLeaveRequest(Integer id, String approverName) {
        processLeaveStatus(id, STATUS_APPROVED, approverName);
    }

    public void rejectLeaveRequest(Integer id) {
        processLeaveStatus(id, STATUS_REJECTED, null);
    }

    public void deleteLeaveRequest(Integer id) {
        leaveRequestRepository.deleteById(id);
    }

    private void processLeaveStatus(Integer id, String newStatus, String approverName) {
        LeaveRequest request = leaveRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ID: " + id));

        boolean isPending = STATUS_PENDING.equals(request.getStatus()) || "Chờ duyệt".equals(request.getStatus());

        if (isPending) {
            request.setStatus(newStatus);
            request.setUpdatedAt(LocalDateTime.now());
            request.setApprovedAt(LocalDateTime.now());
            if (STATUS_APPROVED.equals(newStatus)) request.setApprovedBy(approverName);
            leaveRequestRepository.save(request);
        } else {
            throw new RuntimeException("Chỉ có thể xử lý đơn đang ở trạng thái chờ duyệt!");
        }
    }
}