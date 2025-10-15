package com.moktob.learning;

import com.moktob.common.BaseEntity;
import com.moktob.education.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "memorization_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MemorizationRecord extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "surah_name", nullable = false, length = 100)
    private String surahName;
    
    @Column(name = "start_ayah")
    private Integer startAyah;
    
    @Column(name = "end_ayah")
    private Integer endAyah;
    
    @Column(name = "times_revised")
    private Integer timesRevised = 0;
    
    @Column(name = "last_checked_date")
    private LocalDate lastCheckedDate;
    
    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
}
