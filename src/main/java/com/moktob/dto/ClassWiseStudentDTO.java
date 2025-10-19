package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassWiseStudentDTO {
    private Long classId;
    private String className;
    private String teacherName;
    private Long totalStudents;
    private List<StudentDetailDTO> students;
    private Double classAttendanceRate;
    private String timeSlot;
    private String daysOfWeek;
}
