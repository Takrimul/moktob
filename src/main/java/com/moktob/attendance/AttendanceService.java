package com.moktob.attendance;

import com.moktob.common.AttendanceStatus;
import com.moktob.common.TenantContextHolder;
import com.moktob.dto.AttendanceRequest;
import com.moktob.dto.AttendanceSummaryResponse;
import com.moktob.dto.AttendanceStatistics;
import com.moktob.dto.BulkAttendanceRequest;
import com.moktob.dto.StudentAttendanceSummary;
import com.moktob.education.Student;
import com.moktob.education.StudentService;
import com.moktob.service.AttendanceBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final AttendanceBusinessService attendanceBusinessService;
    private final StudentService studentService;
    
    public List<Attendance> getAllAttendance() {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientId(clientId);
    }
    
    public Optional<Attendance> getAttendanceById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndId(clientId, id);
    }

    public Attendance saveAttendance(AttendanceRequest attendanceRequest) {
        Long clientId = TenantContextHolder.getTenantId();

        Attendance attendance = attendanceRequest.getId() != null
                ? attendanceRepository.findByClientIdAndId(clientId, attendanceRequest.getId())
                .orElseThrow(() -> new RuntimeException("Attendance not found"))
                : new Attendance();

        attendance.setStudentId(attendanceRequest.getStudentId());
        attendance.setClassId(attendanceRequest.getClassId());
        attendance.setTeacherId(attendanceRequest.getTeacherId());
        attendance.setAttendanceDate(attendanceRequest.getAttendanceDate());
        attendance.setStatus(attendanceRequest.getStatus());
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
    
    public List<Attendance> getAttendanceByDateAndClass(LocalDate date, Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        if (classId != null) {
            return attendanceRepository.findByClientIdAndAttendanceDateAndClassId(clientId, date, classId);
        } else {
            return attendanceRepository.findByClientIdAndAttendanceDate(clientId, date);
        }
    }
    
    public List<Attendance> saveBulkAttendance(List<AttendanceRequest> attendanceRequests) {
        Long clientId = TenantContextHolder.getTenantId();
        
        return attendanceRequests.stream()
                .map(request -> {
                    // Check if attendance already exists for this student, class, and date
                    Optional<Attendance> existingAttendance = attendanceRepository
                            .findByClientIdAndStudentIdAndClassIdAndAttendanceDate(
                                    clientId, request.getStudentId(), request.getClassId(), request.getAttendanceDate());
                    
                    Attendance attendance = existingAttendance.orElse(new Attendance());
                    
                    attendance.setStudentId(request.getStudentId());
                    attendance.setClassId(request.getClassId());
                    attendance.setTeacherId(request.getTeacherId());
                    attendance.setAttendanceDate(request.getAttendanceDate());
                    attendance.setStatus(request.getStatus());
                    attendance.setRemarks(request.getRemarks());
                    attendance.setClientId(clientId);
                    
                    return attendanceRepository.save(attendance);
                })
                .toList();
    }

    /**
     * Enhanced bulk save with validation and summary generation
     */
    @Transactional
    public AttendanceSummaryResponse saveBulkAttendance(BulkAttendanceRequest request) {
        Long clientId = TenantContextHolder.getTenantId();
        
        // Validate the request
        attendanceBusinessService.validateBulkAttendanceRequest(request);
        
        // Process bulk attendance
        List<Attendance> savedAttendance = request.getAttendanceRecords().stream()
                .map(record -> {
                    // Check if attendance already exists
                    Optional<Attendance> existingAttendance = attendanceRepository
                            .findByClientIdAndClassIdAndAttendanceDateAndStudentId(
                                    clientId, request.getClassId(), request.getAttendanceDate(), record.getStudentId());
                    
                    Attendance attendance = existingAttendance.orElse(new Attendance());
                    
                    attendance.setStudentId(record.getStudentId());
                    attendance.setClassId(request.getClassId());
                    attendance.setTeacherId(request.getTeacherId());
                    attendance.setAttendanceDate(request.getAttendanceDate());
                    attendance.setStatus(record.getStatus());
                    attendance.setRemarks(record.getRemarks());
                    attendance.setClientId(clientId);
                    
                    return attendanceRepository.save(attendance);
                })
                .collect(Collectors.toList());
        
        // Generate and return summary
        return attendanceBusinessService.processBulkAttendance(request);
    }

    /**
     * Get attendance with summary for specific class and date
     */
    public AttendanceSummaryResponse getClassAttendanceByDate(Long classId, LocalDate date) {
        Long clientId = TenantContextHolder.getTenantId();
        
        // Check if attendance exists for this class and date
        List<Attendance> existingAttendance = attendanceRepository
                .findByClientIdAndClassIdAndAttendanceDate(clientId, classId, date);
        
        if (!existingAttendance.isEmpty()) {
            // Return existing attendance summary
            return buildAttendanceSummaryFromExisting(classId, date, existingAttendance);
        } else {
            // Generate new attendance summary (no attendance marked yet)
            return attendanceBusinessService.generateAttendanceSummary(classId, date);
        }
    }

    /**
     * Get attendance history with pagination
     */
    public Page<AttendanceSummaryResponse> getClassAttendanceHistory(Long classId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Long clientId = TenantContextHolder.getTenantId();
        
        List<Attendance> attendanceList = attendanceRepository
                .findByClientIdAndClassIdAndAttendanceDateBetween(clientId, classId, startDate, endDate);
        
        // Group by date and build summaries
        Map<LocalDate, List<Attendance>> attendanceByDate = attendanceList.stream()
                .collect(Collectors.groupingBy(Attendance::getAttendanceDate));
        
        List<AttendanceSummaryResponse> summaries = attendanceByDate.entrySet().stream()
                .map(entry -> buildAttendanceSummaryFromExisting(classId, entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getAttendanceDate().compareTo(a.getAttendanceDate()))
                .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), summaries.size());
        List<AttendanceSummaryResponse> pageContent = summaries.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, summaries.size());
    }

    /**
     * Update existing attendance for specific class and date
     */
    @Transactional
    public AttendanceSummaryResponse updateClassAttendance(Long classId, LocalDate date, BulkAttendanceRequest request) {
        Long clientId = TenantContextHolder.getTenantId();
        
        // Delete existing attendance for this class and date
        List<Attendance> existingAttendance = attendanceRepository
                .findByClientIdAndClassIdAndAttendanceDate(clientId, classId, date);
        
        if (!existingAttendance.isEmpty()) {
            attendanceRepository.deleteAll(existingAttendance);
        }
        
        // Save new attendance
        return saveBulkAttendance(request);
    }

    /**
     * Get student attendance history
     */
    public List<Attendance> getStudentAttendanceHistory(Long studentId, LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return attendanceRepository.findByClientIdAndStudentIdAndAttendanceDateBetween(clientId, studentId, startDate, endDate);
    }

    /**
     * Calculate attendance percentage for a student
     */
    public Double calculateAttendancePercentage(Long studentId, LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        
        Long presentCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                clientId, studentId, AttendanceStatus.PRESENT, startDate, endDate);
        
        Long lateCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                clientId, studentId, AttendanceStatus.LATE, startDate, endDate);
        
        Long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        return totalDays > 0 ? ((double)(presentCount + lateCount) / totalDays) * 100 : 0.0;
    }

    /**
     * Get attendance statistics
     */
    public AttendanceStatistics getAttendanceStatistics(Long classId, Long studentId, LocalDate startDate, LocalDate endDate) {
        if (classId != null) {
            return getClassAttendanceStatistics(classId, startDate, endDate);
        } else if (studentId != null) {
            return getStudentAttendanceStatistics(studentId, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Either classId or studentId must be provided");
        }
    }

    private AttendanceStatistics getClassAttendanceStatistics(Long classId, LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        Long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        List<Student> students = studentService.getStudentsByClass(classId);
        Long totalStudents = (long) students.size();
        Long totalPossibleAttendance = totalDays * totalStudents;
        
        Long presentCount = attendanceRepository.countAttendanceByClassAndStatusAndDateRange(
                clientId, classId, AttendanceStatus.PRESENT, startDate, endDate);
        
        Long absentCount = attendanceRepository.countAttendanceByClassAndStatusAndDateRange(
                clientId, classId, AttendanceStatus.ABSENT, startDate, endDate);
        
        Long lateCount = attendanceRepository.countAttendanceByClassAndStatusAndDateRange(
                clientId, classId, AttendanceStatus.LATE, startDate, endDate);
        
        Double attendancePercentage = totalPossibleAttendance > 0 ? 
            ((double)(presentCount + lateCount) / totalPossibleAttendance) * 100 : 0.0;
        
        // Calculate student summaries
        List<StudentAttendanceSummary> studentSummaries = students.stream()
                .map(student -> {
                    Long studentPresentCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                            clientId, student.getId(), AttendanceStatus.PRESENT, startDate, endDate);
                    
                    Long studentAbsentCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                            clientId, student.getId(), AttendanceStatus.ABSENT, startDate, endDate);
                    
                    Long studentLateCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                            clientId, student.getId(), AttendanceStatus.LATE, startDate, endDate);
                    
                    Double studentAttendancePercentage = totalDays > 0 ? 
                        ((double)(studentPresentCount + studentLateCount) / totalDays) * 100 : 0.0;
                    
                    return StudentAttendanceSummary.builder()
                            .studentId(student.getId())
                            .studentName(student.getName())
                            .presentCount(studentPresentCount)
                            .absentCount(studentAbsentCount)
                            .lateCount(studentLateCount)
                            .totalDays(totalDays)
                            .attendancePercentage(studentAttendancePercentage)
                            .build();
                })
                .collect(Collectors.toList());
        
        return AttendanceStatistics.builder()
                .classId(classId)
                .startDate(startDate)
                .endDate(endDate)
                .totalDays(totalDays)
                .totalStudents(totalStudents)
                .totalPossibleAttendance(totalPossibleAttendance)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .attendancePercentage(attendancePercentage)
                .studentSummaries(studentSummaries)
                .build();
    }

    private AttendanceStatistics getStudentAttendanceStatistics(Long studentId, LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        Long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        Long presentCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                clientId, studentId, AttendanceStatus.PRESENT, startDate, endDate);
        
        Long absentCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                clientId, studentId, AttendanceStatus.ABSENT, startDate, endDate);
        
        Long lateCount = attendanceRepository.countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(
                clientId, studentId, AttendanceStatus.LATE, startDate, endDate);
        
        Double attendancePercentage = totalDays > 0 ? 
            ((double)(presentCount + lateCount) / totalDays) * 100 : 0.0;
        
        return AttendanceStatistics.builder()
                .studentId(studentId)
                .startDate(startDate)
                .endDate(endDate)
                .totalDays(totalDays)
                .totalStudents(1L)
                .totalPossibleAttendance(totalDays)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .attendancePercentage(attendancePercentage)
                .build();
    }

    private AttendanceSummaryResponse buildAttendanceSummaryFromExisting(Long classId, LocalDate date, List<Attendance> attendanceList) {
        // This method builds a summary from existing attendance records
        // Implementation details would go here
        return attendanceBusinessService.generateAttendanceSummary(classId, date);
    }
}
