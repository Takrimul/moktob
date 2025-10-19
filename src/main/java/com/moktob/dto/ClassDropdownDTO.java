package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassDropdownDTO {
    private Long id;
    private String className;
    private String teacherName;
    private Long teacherId;
}
