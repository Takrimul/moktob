package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSummaryDTO {
    private Long studentId;
    private String studentName;
    private Long classId;
    private String className;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // Summary statistics
    private Integer totalAssessments;
    private Double averageScore;
    private String averageGrade;
    private Integer assessmentsCompleted;
    private Integer assessmentsPending;
    
    // Score breakdown
    private Double averageRecitationScore;
    private Double averageTajweedScore;
    private Double averageMemorizationScore;
    private Double averageComprehensionScore;
    private Double averageDisciplineScore;
    
    // Progress indicators
    private String overallProgress; // IMPROVING, STABLE, DECLINING
    private List<String> strengths;
    private List<String> areasForImprovement;
    
    // Recent assessments
    private List<AssessmentResponseDTO> recentAssessments;
}
