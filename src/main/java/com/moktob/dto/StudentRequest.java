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

}
