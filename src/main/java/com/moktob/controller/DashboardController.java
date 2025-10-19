package com.moktob.controller;

import com.moktob.dto.*;
import com.moktob.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDTO> getDashboardOverview(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting dashboard overview for date range: {} to {}", startDate, endDate);
        
        try {
            DashboardOverviewDTO overview = dashboardService.getDashboardOverview(startDate, endDate);
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            log.error("Error getting dashboard overview", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/class-attendance")
    public ResponseEntity<List<ClassAttendanceSummaryDTO>> getClassAttendanceSummaries(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting class attendance summaries for date range: {} to {}", startDate, endDate);
        
        try {
            List<ClassAttendanceSummaryDTO> summaries = dashboardService.getClassAttendanceSummaries(startDate, endDate);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("Error getting class attendance summaries", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/attendance-trends")
    public ResponseEntity<List<AttendanceTrendDTO>> getAttendanceTrends(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting attendance trends for date range: {} to {}", startDate, endDate);
        
        try {
            List<AttendanceTrendDTO> trends = dashboardService.getAttendanceTrends(startDate, endDate);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            log.error("Error getting attendance trends", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/top-students")
    public ResponseEntity<List<StudentPerformanceDTO>> getTopPerformingStudents(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting top performing students for date range: {} to {}, limit: {}", startDate, endDate, limit);
        
        try {
            List<StudentPerformanceDTO> students = dashboardService.getTopPerformingStudents(startDate, endDate);
            List<StudentPerformanceDTO> limitedStudents = students.stream()
                    .limit(limit)
                    .toList();
            return ResponseEntity.ok(limitedStudents);
        } catch (Exception e) {
            log.error("Error getting top performing students", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/teacher-performance")
    public ResponseEntity<List<TeacherPerformanceDTO>> getTeacherPerformance(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting teacher performance for date range: {} to {}", startDate, endDate);
        
        try {
            List<TeacherPerformanceDTO> teachers = dashboardService.getTeacherPerformance(startDate, endDate);
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            log.error("Error getting teacher performance", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/class-wise-students")
    public ResponseEntity<List<ClassWiseStudentDTO>> getClassWiseStudents(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting class-wise students for date range: {} to {}", startDate, endDate);
        
        try {
            List<ClassWiseStudentDTO> classWiseStudents = dashboardService.getClassWiseStudents(startDate, endDate);
            return ResponseEntity.ok(classWiseStudents);
        } catch (Exception e) {
            log.error("Error getting class-wise students", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/attendance-analytics")
    public ResponseEntity<AttendanceAnalyticsDTO> getAttendanceAnalytics(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting attendance analytics for date range: {} to {}", startDate, endDate);
        
        try {
            AttendanceAnalyticsDTO analytics = dashboardService.getAttendanceAnalytics(startDate, endDate);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error getting attendance analytics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Getting dashboard stats for date range: {} to {}", startDate, endDate);
        
        try {
            DashboardOverviewDTO overview = dashboardService.getDashboardOverview(startDate, endDate);
            
            DashboardStatsDTO stats = new DashboardStatsDTO(
                    overview.getTotalStudents(),
                    overview.getTotalTeachers(),
                    overview.getTotalClasses(),
                    overview.getTotalAttendanceRecords(),
                    overview.getOverallAttendanceRate(),
                    overview.getActiveStudents(),
                    overview.getActiveTeachers(),
                    overview.getActiveClasses()
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting dashboard stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
