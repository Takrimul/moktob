package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatistics {
    private Long classId;
    private String className;
    private Long studentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalDays;
    private Long totalStudents;
    private Long totalPossibleAttendance;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Double attendancePercentage;
    private List<StudentAttendanceSummary> studentSummaries;
}
