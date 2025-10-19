package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private String guardianName;
    private String guardianContact;
    private String address;
    private LocalDate enrollmentDate;
    private Long currentClassId;
    private String photoUrl;
    private String className; // Will be populated separately if needed
}
