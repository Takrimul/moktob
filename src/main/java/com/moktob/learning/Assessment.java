package com.moktob.learning;

import com.moktob.common.BaseEntity;
import com.moktob.education.Student;
import com.moktob.education.Teacher;
import com.moktob.education.ClassEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Assessment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "class_id")
    private Long classId;
    
    @Column(name = "assessment_type", length = 50)
    private String assessmentType; // RECITATION, TAJWEED, MEMORIZATION, COMPREHENSION, DISCIPLINE
    
    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;
    
    @Column(name = "assessment_time")
    private LocalDateTime assessmentTime;
    
    // Scoring fields
    @Column(name = "recitation_score")
    private Integer recitationScore; // 0-100
    
    @Column(name = "tajweed_score")
    private Integer tajweedScore; // 0-100
    
    @Column(name = "memorization_score")
    private Integer memorizationScore; // 0-100
    
    @Column(name = "comprehension_score")
    private Integer comprehensionScore; // 0-100
    
    @Column(name = "discipline_score")
    private Integer disciplineScore; // 0-100
    
    @Column(name = "overall_score")
    private Double overallScore; // Calculated average
    
    @Column(name = "grade", length = 5)
    private String grade; // A+, A, B+, B, C+, C, D, F
    
    // Content fields
    @Column(name = "surah_name", length = 100)
    private String surahName;
    
    @Column(name = "start_ayah")
    private Integer startAyah;
    
    @Column(name = "end_ayah")
    private Integer endAyah;
    
    @Column(name = "verses_assessed")
    private Integer versesAssessed;
    
    @Column(name = "mistakes_count")
    private Integer mistakesCount;
    
    @Column(name = "corrections_given")
    private Integer correctionsGiven;
    
    // Feedback and comments
    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;
    
    @Column(name = "student_strengths", columnDefinition = "TEXT")
    private String studentStrengths;
    
    @Column(name = "areas_for_improvement", columnDefinition = "TEXT")
    private String areasForImprovement;
    
    @Column(name = "homework_assigned", columnDefinition = "TEXT")
    private String homeworkAssigned;
    
    @Column(name = "next_assessment_date")
    private LocalDate nextAssessmentDate;
    
    // Status and flags
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "is_reassessment")
    private Boolean isReassessment = false;
    
    @Column(name = "parent_notified")
    private Boolean parentNotified = false;
    
    @Column(name = "assessment_duration_minutes")
    private Integer assessmentDurationMinutes;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
    
    // Helper methods
    public void calculateOverallScore() {
        int totalScore = 0;
        int count = 0;
        
        if (recitationScore != null) {
            totalScore += recitationScore;
            count++;
        }
        if (tajweedScore != null) {
            totalScore += tajweedScore;
            count++;
        }
        if (memorizationScore != null) {
            totalScore += memorizationScore;
            count++;
        }
        if (comprehensionScore != null) {
            totalScore += comprehensionScore;
            count++;
        }
        if (disciplineScore != null) {
            totalScore += disciplineScore;
            count++;
        }
        
        this.overallScore = count > 0 ? (double) totalScore / count : 0.0;
        this.grade = calculateGrade(this.overallScore);
    }
    
    private String calculateGrade(Double score) {
        if (score >= 95) return "A+";
        if (score >= 90) return "A";
        if (score >= 85) return "B+";
        if (score >= 80) return "B";
        if (score >= 75) return "C+";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }
}
