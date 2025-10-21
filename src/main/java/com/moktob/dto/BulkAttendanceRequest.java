package com.moktob.dto;

import com.moktob.common.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAttendanceRequest {
    private Long classId;
    private LocalDate attendanceDate;
    private Long teacherId;
    private List<AttendanceRecord> attendanceRecords;
    private String remarks;
}
