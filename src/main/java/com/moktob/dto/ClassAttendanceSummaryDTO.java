package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassAttendanceSummaryDTO {
    private Long classId;
    private String className;
    private String teacherName;
    private Long totalStudents;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Double attendanceRate;
    private Long totalAttendanceRecords;
    private String timeSlot;
    private String daysOfWeek;
}
