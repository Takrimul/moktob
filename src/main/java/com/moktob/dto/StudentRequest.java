package com.moktob.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentRequest {
    private Long id;
    private String name;
    private String guardianName;
    private LocalDate dob;
    private String guardianContact;
    private String address;
    private Long classId;
    private String email; // For sending login credentials
    private Boolean sendCredentials = true; // Whether to send login credentials via email
}
