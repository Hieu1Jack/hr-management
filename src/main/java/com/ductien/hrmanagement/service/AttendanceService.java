package com.ductien.hrmanagement.service;

import com.ductien.hrmanagement.entity.Attendance;
import com.ductien.hrmanagement.entity.Employee;
import com.ductien.hrmanagement.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Optional<Attendance> getTodayAttendance(Integer employeeId) {
        return attendanceRepository.findByEmployeeEmployeeIdAndWorkDate(employeeId, LocalDate.now());
    }

    // --- CHECK IN ---
    public Attendance checkIn(Employee employee) {
        Optional<Attendance> existing = getTodayAttendance(employee.getEmployeeId());
        if (existing.isPresent()) {
            throw new RuntimeException("Bạn đã check-in hôm nay rồi!");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setWorkDate(LocalDate.now());
        attendance.setCheckInTime(LocalTime.now());
        attendance.setCreatedAt(LocalDateTime.now());
        
        // Logic đi muộn: Sau 8:30 là Late
        if (attendance.getCheckInTime().isAfter(LocalTime.of(8, 30))) {
            attendance.setStatus("Late");
        } else {
            attendance.setStatus("Present");
        }

        return attendanceRepository.save(attendance);
    }

    // --- CHECK OUT ---
    public Attendance checkOut(Employee employee) {// kiểm tra xem nhân viên đã check-in hôm nay chưa
        Attendance attendance = getTodayAttendance(employee.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Bạn chưa check-in hôm nay!"));

        if (attendance.getCheckOutTime() != null) {// kiểm tra xem nhân viên đã check-out chưa
            throw new RuntimeException("Bạn đã check-out rồi!");
        }

        attendance.setCheckOutTime(LocalTime.now());// đặt thời gian check-out là thời gian hiện tại
        attendance.setUpdatedAt(LocalDateTime.now());// 

        // Tính toán giờ làm (Logic nằm trong Entity Attendance)
        // Lưu ý: Nếu trong Entity bạn set status="Present" khi check-out, nó sẽ ghi đè status "Late"
        // Nếu bạn muốn giữ "Late" để phạt đi muộn, hãy kiểm tra kỹ file Entity.
        attendance.calculateHours(); // Gọi phương thức tính toán giờ làm từ Entity Attendance

        return attendanceRepository.save(attendance);// lưu bản ghi chấm công đã được cập nhật vào database
    }

    // --- THỐNG KÊ ---
    public List<Attendance> getAttendanceByMonth(Integer employeeId, int year, int month) {// Lấy ngày bắt đầu và kết thúc của tháng
        LocalDate startDate = LocalDate.of(year, month, 1);// Ngày bắt đầu là ngày 1 của tháng
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);// Ngày kết thúc là ngày cuối cùng của tháng
        // Hàm này giờ đã có trong Repository, không còn lỗi đỏ nữa
        return attendanceRepository.findByEmployeeEmployeeIdAndWorkDateBetween(employeeId, startDate, endDate);// Trả về danh sách chấm công của nhân viên trong khoảng thời gian đã xác định
    }

    public Double getTotalRegularHours(Integer employeeId, int year, int month) {// Lấy danh sách chấm công trong tháng
        List<Attendance> list = getAttendanceByMonth(employeeId, year, month);// Tính tổng giờ làm chuẩn từ danh sách chấm công
        return list.stream().mapToDouble(a -> a.getRegularHours() == null ? 0 : a.getRegularHours()).sum();// Trả về tổng giờ làm chuẩn
    }

    public Double getTotalOvertimeHours(Integer employeeId, int year, int month) {// Lấy danh sách chấm công trong tháng
        List<Attendance> list = getAttendanceByMonth(employeeId, year, month);// Tính tổng giờ tăng ca từ danh sách chấm công
        return list.stream().mapToDouble(a -> a.getOvertimeHours() == null ? 0 : a.getOvertimeHours()).sum();// Trả về tổng giờ tăng ca
    }

    // [QUAN TRỌNG] Đếm ngày công cho cả "Present" và "Late"
    public Long getFullDaysCount(Integer employeeId, int year, int month) {// Lấy danh sách chấm công trong tháng
        List<Attendance> list = getAttendanceByMonth(employeeId, year, month);// Đếm số ngày làm việc (cả đúng giờ và đi muộn)
        
        return list.stream()
                   .filter(a -> "Present".equals(a.getStatus()) || "Late".equals(a.getStatus()))// Lọc  nhũng ngày Present là đúng giờ, Late là đi muộn
                   .count();// Trả về số ngày làm việc đầy đủ
    }

    /**
     * TÍNH CÔNG THEO CƠ CHẾ 8 TIẾNG = 1 NGÀY
     * - Tổng hợp tất cả giờ làm trong tháng
     * - Chia cho 8 để ra số ngày công
     * - Phần dư (giờ thừa mỗi ngày > 8h) = OT
     * @return Map chứa: workDays (số ngày công), overtimeHours (giờ OT)
     */
    public java.util.Map<String, Double> calculateWorkDaysAndOT(Integer employeeId, int year, int month) {// Lấy danh sách chấm công trong tháng
        List<Attendance> list = getAttendanceByMonth(employeeId, year, month);// Tính tổng giờ làm chuẩn và giờ OT trong tháng
        
        double totalRegularHours = 0; // Giờ làm chuẩn (max 8h/ngày)
        double totalOvertimeHours = 0; // Giờ OT (phần > 8h/ngày)
        
        for (Attendance a : list) {// Lặp qua từng bản ghi chấm công
            if (!("Present".equals(a.getStatus()) || "Late".equals(a.getStatus()))) {
                continue; // Bỏ qua ngày nghỉ/vắng
            }
            
            Double totalHours = a.getTotalHours();// Lấy tổng giờ làm trong ngày
            if (totalHours == null || totalHours <= 0) {// nếu không có giờ làm hoặc <=0 thì bỏ qua
                continue;
            }
            
            // ========== CHUẨN: 8 TIẾNG = 1 NGÀY CÔNG ==========
            if (totalHours > 8) {// Nếu ngày này làm việc quá 8 giờ
                // Ngày này làm quá 8h -> 8h là regular, còn lại là OT
                totalRegularHours += 8;// cộng 8h vào giờ làm chuẩn
                totalOvertimeHours += (totalHours - 8);// cộng phần dư vào giờ OT
            } else {
                // Ngày này làm <= 8h -> dồn hết vào regular
                totalRegularHours += totalHours;// cộng toàn bộ giờ làm vào giờ làm chuẩn
            }
            
            // ========== TEST: CHECK IN + CHECK OUT = 1 NGÀY CÔNG ==========
            /*
            totalRegularHours += 1;
            if (totalHours > 8) {
                totalOvertimeHours += (totalHours - 8);
            }
            */
            // ========== HẾT TEST ==========
        }
        
        // ========== CHUẨN: Tính ngày công = tổng giờ / 8 ==========
        double workDays = totalRegularHours / 8.0;
        
        // ========== TEST: workDays = số ngày đã chấm công ==========
        // double workDays = totalRegularHours;
        
        // Làm tròn 2 chữ số
        workDays = Math.round(workDays * 100.0) / 100.0;
        totalOvertimeHours = Math.round(totalOvertimeHours * 100.0) / 100.0;
        
        java.util.Map<String, Double> result = new java.util.HashMap<>();
        result.put("workDays", workDays);
        result.put("overtimeHours", totalOvertimeHours);
        result.put("totalRegularHours", totalRegularHours);
        
        return result;
    }
}