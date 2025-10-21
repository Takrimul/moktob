package com.moktob.service;

import com.moktob.common.AttendanceStatus;
import com.moktob.common.TenantContextHolder;
import com.moktob.dto.AttendanceRecord;
import com.moktob.dto.AttendanceStatistics;
import com.moktob.dto.AttendanceSummaryResponse;
import com.moktob.dto.BulkAttendanceRequest;
import com.moktob.dto.StudentAttendanceDetail;
import com.moktob.dto.StudentAttendanceSummary;
import com.moktob.education.ClassEntity;
import com.moktob.education.Student;
import com.moktob.education.Teacher;
import com.moktob.education.StudentService;
import com.moktob.education.TeacherService;
import com.moktob.education.ClassEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceBusinessService {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ClassEntityService classEntityService;

    /**
     * Validate attendance date (no weekends, holidays, etc.)
     */
    public boolean isValidAttendanceDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot mark attendance for future dates");
        }
        
        if (date.isBefore(LocalDate.now().minusDays(30))) {
            throw new IllegalArgumentException("Cannot mark attendance for dates older than 30 days");
        }
        
        if (isWeekend(date)) {
            throw new IllegalArgumentException("Cannot mark attendance on weekends");
        }
        
        if (isHoliday(date)) {
            throw new IllegalArgumentException("Cannot mark attendance on holidays");
        }
        
        return true;
    }

    /**
     * Check if attendance already exists for class and date
     */
    public boolean attendanceExistsForClassAndDate(Long classId, LocalDate date) {
        // This will be implemented in the service layer
        return false; // Placeholder
    }

    /**
     * Calculate attendance statistics
     */
    public AttendanceStatistics calculateAttendanceStatistics(Long classId, LocalDate startDate, LocalDate endDate) {
        Long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<Student> students = studentService.getStudentsByClass(classId);
        Long totalStudents = (long) students.size();
        Long totalPossibleAttendance = totalDays * totalStudents;
        
        // This will be enhanced with actual repository calls
        Long presentCount = 0L;
        Long absentCount = 0L;
        Long lateCount = 0L;
        
        Double attendancePercentage = totalPossibleAttendance > 0 ? 
            ((double)(presentCount + lateCount) / totalPossibleAttendance) * 100 : 0.0;
        
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
            .build();
    }

    /**
     * Generate attendance summary
     */
    public AttendanceSummaryResponse generateAttendanceSummary(Long classId, LocalDate date) {
        try {
            ClassEntity classEntity = classEntityService.getClassById(classId).orElse(null);
            List<Student> students = studentService.getStudentsByClass(classId);
            
            // Get teacher for the class
            Teacher teacher = null;
            if (classEntity != null && classEntity.getTeacherId() != null) {
                teacher = teacherService.getTeacherById(classEntity.getTeacherId()).orElse(null);
            }
            
            // Convert students to attendance details
            List<StudentAttendanceDetail> studentDetails = students.stream()
                .map(student -> StudentAttendanceDetail.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .guardianName(student.getGuardianName())
                    .guardianContact(student.getGuardianContact())
                    .status(AttendanceStatus.ABSENT) // Default status
                    .build())
                .collect(Collectors.toList());
            
            return AttendanceSummaryResponse.builder()
                .classId(classId)
                .className(classEntity != null ? classEntity.getClassName() : "Unknown Class")
                .attendanceDate(date)
                .totalStudents((long) students.size())
                .presentCount(0L)
                .absentCount((long) students.size())
                .lateCount(0L)
                .attendancePercentage(0.0)
                .studentDetails(studentDetails)
                .teacherId(teacher != null ? teacher.getId() : null)
                .teacherName(teacher != null ? teacher.getName() : "No Teacher")
                .build();
                
        } catch (Exception e) {
            log.error("Error generating attendance summary for class {} on date {}", classId, date, e);
            throw new RuntimeException("Failed to generate attendance summary", e);
        }
    }

    /**
     * Validate bulk attendance request
     */
    public void validateBulkAttendanceRequest(BulkAttendanceRequest request) {
        isValidAttendanceDate(request.getAttendanceDate());
        
        if (request.getClassId() == null) {
            throw new IllegalArgumentException("Class ID is required");
        }
        
        if (request.getAttendanceRecords() == null || request.getAttendanceRecords().isEmpty()) {
            throw new IllegalArgumentException("Attendance records are required");
        }
        
        // Validate each attendance record
        request.getAttendanceRecords().forEach(record -> {
            if (record.getStudentId() == null) {
                throw new IllegalArgumentException("Student ID is required for each record");
            }
            if (record.getStatus() == null) {
                throw new IllegalArgumentException("Attendance status is required for each record");
            }
        });
    }

    /**
     * Process bulk attendance with business rules
     */
    public AttendanceSummaryResponse processBulkAttendance(BulkAttendanceRequest request) {
        validateBulkAttendanceRequest(request);
        
        // Generate summary from the request
        ClassEntity classEntity = classEntityService.getClassById(request.getClassId()).orElse(null);
        List<Student> students = studentService.getStudentsByClass(request.getClassId());
        
        // Create a map of student attendance for quick lookup
        Map<Long, AttendanceRecord> attendanceMap = request.getAttendanceRecords().stream()
            .collect(Collectors.toMap(AttendanceRecord::getStudentId, record -> record));
        
        // Convert students to attendance details with their status
        List<StudentAttendanceDetail> studentDetails = students.stream()
            .map(student -> {
                AttendanceRecord record = attendanceMap.get(student.getId());
                return StudentAttendanceDetail.builder()
                    .studentId(student.getId())
                    .studentName(student.getName())
                    .guardianName(student.getGuardianName())
                    .guardianContact(student.getGuardianContact())
                    .status(record != null ? record.getStatus() : AttendanceStatus.ABSENT)
                    .remarks(record != null ? record.getRemarks() : null)
                    .checkInTime(record != null ? record.getCheckInTime() : null)
                    .build();
            })
            .collect(Collectors.toList());
        
        // Calculate summary statistics
        long presentCount = studentDetails.stream()
            .mapToLong(detail -> detail.getStatus() == AttendanceStatus.PRESENT ? 1 : 0)
            .sum();
        
        long absentCount = studentDetails.stream()
            .mapToLong(detail -> detail.getStatus() == AttendanceStatus.ABSENT ? 1 : 0)
            .sum();
        
        long lateCount = studentDetails.stream()
            .mapToLong(detail -> detail.getStatus() == AttendanceStatus.LATE ? 1 : 0)
            .sum();
        
        double attendancePercentage = students.size() > 0 ? 
            ((double)(presentCount + lateCount) / students.size()) * 100 : 0.0;
        
        // Use teacherId from request, or fall back to logged-in user's teacherId
        Long teacherId = request.getTeacherId() != null ? request.getTeacherId() : TenantContextHolder.getTeacherId();
        
        // If still null, try to get teacher from class
        if (teacherId == null) {
            try {
                if (classEntity != null && classEntity.getTeacherId() != null) {
                    teacherId = classEntity.getTeacherId();
                }
            } catch (Exception e) {
                log.warn("Could not get teacher from class: {}", e.getMessage());
            }
        }
        
        // If still null, throw an error
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID is required for attendance. Please ensure you are logged in as a teacher or the class has an assigned teacher.");
        }
        
        return AttendanceSummaryResponse.builder()
                .classId(request.getClassId())
                .className(classEntity != null ? classEntity.getClassName() : "Unknown Class")
                .attendanceDate(request.getAttendanceDate())
                .totalStudents((long) students.size())
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .attendancePercentage(attendancePercentage)
                .studentDetails(studentDetails)
                .remarks(request.getRemarks())
                .teacherId(teacherId)
                .build();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isHoliday(LocalDate date) {
        // Implement holiday checking logic
        // This could be from a holidays table or configuration
        return false; // Placeholder
    }
}
