package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private Long classId;
    private String className;
    private String assessmentType;
    private LocalDate assessmentDate;
    private LocalDateTime assessmentTime;
    
    // Scoring fields
    private Integer recitationScore;
    private Integer tajweedScore;
    private Integer memorizationScore;
    private Integer comprehensionScore;
    private Integer disciplineScore;
    private Double overallScore;
    private String grade;
    
    // Content fields
    private String surahName;
    private Integer startAyah;
    private Integer endAyah;
    private Integer versesAssessed;
    private Integer mistakesCount;
    private Integer correctionsGiven;
    
    // Feedback fields
    private String teacherFeedback;
    private String studentStrengths;
    private String areasForImprovement;
    private String homeworkAssigned;
    private LocalDate nextAssessmentDate;
    
    // Status fields
    private Boolean isCompleted;
    private Boolean isReassessment;
    private Boolean parentNotified;
    private Integer assessmentDurationMinutes;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
