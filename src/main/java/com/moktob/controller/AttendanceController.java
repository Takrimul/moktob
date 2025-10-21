package com.moktob.controller;

import com.moktob.attendance.Attendance;
import com.moktob.attendance.AttendanceService;
import com.moktob.common.AttendanceStatus;
import com.moktob.dto.AttendanceRequest;
import com.moktob.dto.AttendanceSummaryResponse;
import com.moktob.dto.AttendanceStatistics;
import com.moktob.dto.BulkAttendanceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        Optional<Attendance> attendance = attendanceService.getAttendanceById(id);
        return attendance.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody AttendanceRequest attendanceRequest) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendanceRequest));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long id, @RequestBody AttendanceRequest attendanceRequest) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendanceRequest));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId));
    }
    
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Attendance>> getAttendanceByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClass(classId));
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Attendance>> getAttendanceByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByTeacher(teacherId));
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Attendance>> getAttendanceByStatus(@PathVariable AttendanceStatus status) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStatus(status));
    }
    
    @GetMapping("/by-date")
    public ResponseEntity<List<Attendance>> getAttendanceByDateAndClass(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDateAndClass(date, classId));
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<Attendance>> createBulkAttendance(@RequestBody List<AttendanceRequest> attendanceRequests) {
        return ResponseEntity.ok(attendanceService.saveBulkAttendance(attendanceRequests));
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<Attendance>> getTodayAttendance() {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(LocalDate.now()));
    }

    // Enhanced endpoints for robust attendance management
    
    /**
     * Submit bulk attendance for a class on a specific date
     */
    @PostMapping("/bulk-submit")
    public ResponseEntity<AttendanceSummaryResponse> submitBulkAttendance(@RequestBody BulkAttendanceRequest request) {
        try {
            log.info("Submitting bulk attendance for class {} on date {}", request.getClassId(), request.getAttendanceDate());
            AttendanceSummaryResponse response = attendanceService.saveBulkAttendance(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting bulk attendance", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get attendance for specific class and date
     */
    @GetMapping("/class/{classId}/date/{date}")
    public ResponseEntity<AttendanceSummaryResponse> getClassAttendanceByDate(
            @PathVariable Long classId, 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            AttendanceSummaryResponse response = attendanceService.getClassAttendanceByDate(classId, date);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting class attendance for class {} on date {}", classId, date, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get attendance history for a class
     */
    @GetMapping("/class/{classId}/history")
    public ResponseEntity<Page<AttendanceSummaryResponse>> getClassAttendanceHistory(
            @PathVariable Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        try {
            LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? endDate : LocalDate.now();
            
            Page<AttendanceSummaryResponse> response = attendanceService.getClassAttendanceHistory(classId, start, end, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting class attendance history for class {}", classId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update attendance for specific class and date
     */
    @PutMapping("/class/{classId}/date/{date}")
    public ResponseEntity<AttendanceSummaryResponse> updateClassAttendance(
            @PathVariable Long classId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody BulkAttendanceRequest request) {
        try {
            log.info("Updating attendance for class {} on date {}", classId, date);
            AttendanceSummaryResponse response = attendanceService.updateClassAttendance(classId, date, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating class attendance for class {} on date {}", classId, date, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get student attendance history
     */
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<Attendance>> getStudentAttendanceHistory(
            @PathVariable Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? endDate : LocalDate.now();
            
            List<Attendance> response = attendanceService.getStudentAttendanceHistory(studentId, start, end);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting student attendance history for student {}", studentId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get attendance statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AttendanceStatistics> getAttendanceStatistics(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            AttendanceStatistics response = attendanceService.getAttendanceStatistics(classId, studentId, startDate, endDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting attendance statistics", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Calculate attendance percentage for a student
     */
    @GetMapping("/student/{studentId}/percentage")
    public ResponseEntity<Double> getStudentAttendancePercentage(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Double percentage = attendanceService.calculateAttendancePercentage(studentId, startDate, endDate);
            return ResponseEntity.ok(percentage);
        } catch (Exception e) {
            log.error("Error calculating attendance percentage for student {}", studentId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
