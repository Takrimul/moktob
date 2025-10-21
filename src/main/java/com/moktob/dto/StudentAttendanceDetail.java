package com.moktob.dto;

import com.moktob.common.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDetail {
    private Long studentId;
    private String studentName;
    private String guardianName;
    private String guardianContact;
    private AttendanceStatus status;
    private String remarks;
    private LocalTime checkInTime;
}
