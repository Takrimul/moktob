package com.moktob.dto;

import com.moktob.common.AttendanceStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceRequest {
    private Long id;
    private Long studentId;
    private Long classId;
    private Long teacherId;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String remarks;
}
