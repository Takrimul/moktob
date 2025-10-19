package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDTO {
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalClasses;
    private Long totalAttendanceRecords;
    private Double overallAttendanceRate;
    private Long activeStudents;
    private Long activeTeachers;
    private Long activeClasses;
    private List<ClassAttendanceSummaryDTO> classAttendanceSummaries;
    private List<AttendanceTrendDTO> attendanceTrends;
    private List<StudentPerformanceDTO> topPerformingStudents;
    private List<TeacherPerformanceDTO> teacherPerformance;
}
