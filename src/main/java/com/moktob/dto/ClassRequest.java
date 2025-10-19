package com.moktob.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ClassRequest {
    private Long id;
    private String className;
    private Long teacherId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String daysOfWeek;

}
