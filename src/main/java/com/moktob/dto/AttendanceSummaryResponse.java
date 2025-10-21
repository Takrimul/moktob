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
public class AttendanceSummaryResponse {
    private Long classId;
    private String className;
    private LocalDate attendanceDate;
    private Long totalStudents;
    private Long presentCount;
    private Long absentCount;
    private Long lateCount;
    private Double attendancePercentage;
    private List<StudentAttendanceDetail> studentDetails;
    private String remarks;
    private Long teacherId;
    private String teacherName;
}
