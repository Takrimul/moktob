package com.moktob.attendance;

import com.moktob.common.AttendanceStatus;
import com.moktob.common.BaseEntity;
import com.moktob.education.ClassEntity;
import com.moktob.education.Student;
import com.moktob.education.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_id")
    private Long classId;
    
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "attendance_date")
    private LocalDate attendanceDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttendanceStatus status;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private Teacher teacher;
}
