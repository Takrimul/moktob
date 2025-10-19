package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPerformanceDTO {
    private Long studentId;
    private String studentName;
    private String className;
    private Double attendanceRate;
    private Double averageRecitationScore;
    private Double averageTajweedScore;
    private Double averageDisciplineScore;
    private Double overallScore;
    private Long totalAssessments;
    private Long totalAttendanceRecords;
}
