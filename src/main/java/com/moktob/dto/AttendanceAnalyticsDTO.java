package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAnalyticsDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalAttendanceRecords;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Double overallAttendanceRate;
    private Double averageDailyAttendance;
    private Long totalUniqueStudents;
    private Long totalUniqueClasses;
    private List<DailyAttendanceDTO> dailyBreakdown;
}
