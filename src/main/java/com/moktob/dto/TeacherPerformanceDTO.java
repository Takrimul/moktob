package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherPerformanceDTO {
    private Long teacherId;
    private String teacherName;
    private Long totalClasses;
    private Long totalStudents;
    private Double averageClassAttendanceRate;
    private Long totalAssessmentsConducted;
    private Double averageStudentScores;
    private String qualification;
    private Boolean isActive;
}
