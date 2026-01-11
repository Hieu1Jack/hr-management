package com.ductien.hrmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeeId")
    private Integer employeeId;

    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String employeeName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String address;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "NVARCHAR(10)")
    private String gender;

    @Column(length = 20)
    private String CCCD;

    @Column(length = 20, unique = true)
    private String MNV;
    
    // Vai trò đặc biệt: KE_TOAN_TRUONG, TRUONG_PHONG, null = nhân viên thường
    @Column(length = 50)
    private String specialRole;

    @ManyToOne
    @JoinColumn(name = "DepartmentId")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "PositionId")
    private Position position;

    @Column(name = "joinDate")
    private LocalDate joinDate;

    @Column(name = "basicSalary")
    private java.math.BigDecimal basicSalary;

    @Column(nullable = false)
    private Boolean isLeader = false;

    @Column(length = 255)
    private String Imgs = "noimage.png";

    @Column(name = "EmployeeStatus")
    private Integer EmployeeStatus = 1;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(name = "CreatedAt", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Salary> salaries;

    // Getters and Setters
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getCCCD() { return CCCD; }
    public void setCCCD(String CCCD) { this.CCCD = CCCD; }

    public String getMNV() { return MNV; }
    public void setMNV(String MNV) { this.MNV = MNV; }
    
    public String getSpecialRole() { return specialRole; }
    public void setSpecialRole(String specialRole) { this.specialRole = specialRole; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public java.math.BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(java.math.BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public Boolean getIsLeader() { return isLeader; }
    public void setIsLeader(Boolean isLeader) { this.isLeader = isLeader; }

    public String getImgs() { return Imgs; }
    public void setImgs(String Imgs) { this.Imgs = Imgs; }

    public Integer getEmployeeStatus() { return EmployeeStatus; }
    public void setEmployeeStatus(Integer EmployeeStatus) { this.EmployeeStatus = EmployeeStatus; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<LeaveRequest> getLeaveRequests() { return leaveRequests; }
    public void setLeaveRequests(List<LeaveRequest> leaveRequests) { this.leaveRequests = leaveRequests; }

    public List<Salary> getSalaries() { return salaries; }
    public void setSalaries(List<Salary> salaries) { this.salaries = salaries; }
}
