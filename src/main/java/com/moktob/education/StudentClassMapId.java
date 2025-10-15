package com.moktob.education;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentClassMapId implements Serializable {
    private Long studentId;
    private Long classId;
}
