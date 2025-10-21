package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentAnalyticsDTO {
    private Long classId;
    private String className;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // Class performance metrics
    private Integer totalAssessments;
    private Double classAverageScore;
    private String classAverageGrade;
    private Integer studentsAssessed;
    private Integer totalStudents;
    
    // Score distribution
    private Map<String, Integer> gradeDistribution; // A+: 5, A: 10, etc.
    private Map<String, Double> averageScoresByType; // RECITATION: 85.5, TAJWEED: 78.2, etc.
    
    // Performance trends
    private List<AssessmentTrendDTO> weeklyTrends;
    private List<AssessmentTrendDTO> monthlyTrends;
    
    // Top performers
    private List<StudentPerformanceDTO> topPerformers;
    private List<StudentPerformanceDTO> studentsNeedingAttention;
    
    // Assessment completion rates
    private Double completionRate;
    private Integer assessmentsCompleted;
    private Integer assessmentsPending;
    
    // Teacher performance
    private Map<String, Integer> assessmentsByTeacher;
    private Map<String, Double> averageScoresByTeacher;
}
