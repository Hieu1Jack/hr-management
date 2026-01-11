package com.ductien.hrmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "Attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendanceId")
    private Integer attendanceId;

    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = false)
    private Employee employee;

    // Mapping đúng cột trong DB để tránh lỗi NULL
    @Column(name = "attendanceDate", nullable = false) 
    private LocalDate workDate; 

    @Column
    private LocalTime checkInTime;

    @Column
    private LocalTime checkOutTime;

    @Column
    private Double totalHours = 0.0;

    @Column
    private Double regularHours = 0.0;

    @Column
    private Double overtimeHours = 0.0;

    @Column(length = 50)
    private String status; 

    @Column(length = 500)
    private String notes;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // --- LOGIC TÍNH CÔNG MỚI (QUAN TRỌNG) ---
    public void calculateHours() {
        if (checkInTime != null && checkOutTime != null) {
            // 1. Tính toán giờ làm thực tế
            Duration duration = Duration.between(checkInTime, checkOutTime);
            this.totalHours = duration.toMinutes() / 60.0;
            
            // Trừ 1 tiếng nghỉ trưa nếu làm > 5 tiếng
            if (this.totalHours > 5) {
                this.totalHours -= 1.0;
            }
            
            // Làm tròn
            this.totalHours = Math.round(this.totalHours * 10.0) / 10.0;
            
            // Tính giờ chuẩn/tăng ca để lưu DB (cho đẹp báo cáo)
            if (this.totalHours >= 8) {
                this.regularHours = 8.0;
                this.overtimeHours = this.totalHours - 8.0;
            } else {
                this.regularHours = this.totalHours;
                this.overtimeHours = 0.0;
            }
            
            // 2. ÉP TRẠNG THÁI "PRESENT"
            // Bất kể làm bao nhiêu phút, cứ có check-out là tính "Present" (Có mặt/Đủ công)
            this.status = "Present"; 
        }
    }

    // Getters and Setters (Giữ nguyên)
    public Integer getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Integer attendanceId) { this.attendanceId = attendanceId; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }
    public LocalTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalTime checkOutTime) { this.checkOutTime = checkOutTime; }
    public Double getTotalHours() { return totalHours; }
    public void setTotalHours(Double totalHours) { this.totalHours = totalHours; }
    public Double getRegularHours() { return regularHours; }
    public void setRegularHours(Double regularHours) { this.regularHours = regularHours; }
    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}