package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDTO {
    private Long id;
    private String className;
    private Long teacherId;
    private String teacherName; // To hold the teacher name from join query
    private LocalTime startTime;
    private LocalTime endTime;
    private String daysOfWeek;
    private Long studentCount; // To hold the count of students in this class
}
