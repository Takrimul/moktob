package com.moktob.attendance;

import com.moktob.common.AttendanceStatus;
import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    
    public List<Attendance> getAllAttendance() {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientId(clientId);
    }
    
    public Optional<Attendance> getAttendanceById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndId(clientId, id);
    }
    
    public Attendance saveAttendance(Attendance attendance) {
        Long clientId = TenantContextHolder.getTenantId();
        attendance.setClientId(clientId);
        return attendanceRepository.save(attendance);
    }
    
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
    
    public List<Attendance> getAttendanceByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndStudentId(clientId, studentId);
    }
    
    public List<Attendance> getAttendanceByClass(Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndClassId(clientId, classId);
    }
    
    public List<Attendance> getAttendanceByTeacher(Long teacherId) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndTeacherId(clientId, teacherId);
    }
    
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndAttendanceDate(clientId, date);
    }
    
    public List<Attendance> getAttendanceByStatus(AttendanceStatus status) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndStatus(clientId, status);
    }
    
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndAttendanceDateBetween(clientId, startDate, endDate);
    }
}
