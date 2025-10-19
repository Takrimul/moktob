package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailDTO {
    private Long studentId;
    private String studentName;
    private String guardianName;
    private String guardianContact;
    private LocalDate enrollmentDate;
    private Double attendanceRate;
    private Double averageScore;
    private Boolean isActive;
}
