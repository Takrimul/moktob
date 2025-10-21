package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceSummary {
    private Long studentId;
    private String studentName;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Long totalDays;
    private Double attendancePercentage;
}
