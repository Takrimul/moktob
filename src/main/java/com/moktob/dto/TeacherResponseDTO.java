package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth; // null for now
    private String address; // null for now
    private String qualification;
    private String specialization; // null for now
    private LocalDate joiningDate;
    private String photoUrl; // null for now
    private Boolean isActive;
    private String departmentName; // null for now
}
