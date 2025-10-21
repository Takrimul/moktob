package com.moktob.controller;

import com.moktob.dto.*;
import com.moktob.learning.AssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@Slf4j
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping
    public ResponseEntity<List<AssessmentResponseDTO>> getAllAssessments() {
        List<AssessmentResponseDTO> assessments = assessmentService.getAllAssessments();
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponseDTO> getAssessmentById(@PathVariable Long id) {
        return assessmentService.getAssessmentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssessmentResponseDTO> createAssessment(@RequestBody AssessmentRequest request) {
        try {
            AssessmentResponseDTO assessment = assessmentService.createAssessment(request);
            log.info("Created assessment: {} for student: {}", assessment.getId(), request.getStudentId());
            return ResponseEntity.ok(assessment);
        } catch (Exception e) {
            log.error("Error creating assessment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentResponseDTO> updateAssessment(@PathVariable Long id, @RequestBody AssessmentRequest request) {
        try {
            AssessmentResponseDTO assessment = assessmentService.updateAssessment(id, request);
            log.info("Updated assessment: {}", id);
            return ResponseEntity.ok(assessment);
        } catch (Exception e) {
            log.error("Error updating assessment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        try {
            assessmentService.deleteAssessment(id);
            log.info("Deleted assessment: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting assessment: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByStudent(@PathVariable Long studentId) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByStudent(studentId);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByTeacher(@PathVariable Long teacherId) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByTeacher(teacherId);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByClass(@PathVariable Long classId) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByClass(classId);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/type/{assessmentType}")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByType(@PathVariable String assessmentType) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByType(assessmentType);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByGrade(@PathVariable String grade) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByGrade(grade);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/score-range")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsByScoreRange(
            @RequestParam Double minScore,
            @RequestParam Double maxScore) {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsByScoreRange(minScore, maxScore);
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AssessmentResponseDTO>> getPendingAssessments() {
        List<AssessmentResponseDTO> assessments = assessmentService.getPendingAssessments();
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/parent-notification")
    public ResponseEntity<List<AssessmentResponseDTO>> getAssessmentsNeedingParentNotification() {
        List<AssessmentResponseDTO> assessments = assessmentService.getAssessmentsNeedingParentNotification();
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/summary/student/{studentId}")
    public ResponseEntity<AssessmentSummaryDTO> getStudentAssessmentSummary(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            AssessmentSummaryDTO summary = assessmentService.getStudentAssessmentSummary(studentId, fromDate, toDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Error getting student assessment summary: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/analytics/class/{classId}")
    public ResponseEntity<AssessmentAnalyticsDTO> getClassAssessmentAnalytics(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            AssessmentAnalyticsDTO analytics = assessmentService.getClassAssessmentAnalytics(classId, fromDate, toDate);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Error getting class assessment analytics: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count/student/{studentId}")
    public ResponseEntity<Long> getAssessmentCountByStudent(@PathVariable Long studentId) {
        Long count = assessmentService.getAssessmentCountByStudent(studentId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/class/{classId}")
    public ResponseEntity<Long> getAssessmentCountByClass(@PathVariable Long classId) {
        Long count = assessmentService.getAssessmentCountByClass(classId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/average/student/{studentId}")
    public ResponseEntity<Double> getAverageScoreByStudent(@PathVariable Long studentId) {
        Double average = assessmentService.getAverageScoreByStudent(studentId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/average/class/{classId}")
    public ResponseEntity<Double> getAverageScoreByClass(@PathVariable Long classId) {
        Double average = assessmentService.getAverageScoreByClass(classId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AssessmentResponseDTO>> createBulkAssessments(@RequestBody List<AssessmentRequest> requests) {
        try {
            List<AssessmentResponseDTO> assessments = requests.stream()
                .map(assessmentService::createAssessment)
                .toList();
            log.info("Created {} bulk assessments", assessments.size());
            return ResponseEntity.ok(assessments);
        } catch (Exception e) {
            log.error("Error creating bulk assessments: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AssessmentResponseDTO> markAssessmentComplete(@PathVariable Long id) {
        try {
            AssessmentRequest request = new AssessmentRequest();
            request.setIsCompleted(true);
            AssessmentResponseDTO assessment = assessmentService.updateAssessment(id, request);
            log.info("Marked assessment {} as complete", id);
            return ResponseEntity.ok(assessment);
        } catch (Exception e) {
            log.error("Error marking assessment complete: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/notify-parent")
    public ResponseEntity<AssessmentResponseDTO> markParentNotified(@PathVariable Long id) {
        try {
            AssessmentRequest request = new AssessmentRequest();
            request.setParentNotified(true);
            AssessmentResponseDTO assessment = assessmentService.updateAssessment(id, request);
            log.info("Marked parent notification for assessment {}", id);
            return ResponseEntity.ok(assessment);
        } catch (Exception e) {
            log.error("Error marking parent notification: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
