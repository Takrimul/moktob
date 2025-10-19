package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalClasses;
    private Long totalAttendanceRecords;
    private Double overallAttendanceRate;
    private Long activeStudents;
    private Long activeTeachers;
    private Long activeClasses;
}
