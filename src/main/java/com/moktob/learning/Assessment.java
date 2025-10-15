package com.moktob.learning;

import com.moktob.common.BaseEntity;
import com.moktob.education.Student;
import com.moktob.education.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "assessment_date")
    private LocalDate assessmentDate;
    
    @Column(name = "recitation_score")
    private Integer recitationScore;
    
    @Column(name = "tajweed_score")
    private Integer tajweedScore;
    
    @Column(name = "discipline_score")
    private Integer disciplineScore;
    
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;
}
