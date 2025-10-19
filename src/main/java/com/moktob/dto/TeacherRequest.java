package com.moktob.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherRequest {

    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String qualification;
    private LocalDate joiningDate;
    private Boolean isActive;

}
