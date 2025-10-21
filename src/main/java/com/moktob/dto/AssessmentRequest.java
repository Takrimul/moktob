package com.moktob.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AssessmentRequest {
    private Long id;
    private Long studentId;
    private Long teacherId;
    private Long classId;
    private String assessmentType;
    private LocalDate assessmentDate;
    private LocalDateTime assessmentTime;
    
    // Scoring fields
    private Integer recitationScore;
    private Integer tajweedScore;
    private Integer memorizationScore;
    private Integer comprehensionScore;
    private Integer disciplineScore;
    
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
}
