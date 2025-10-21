package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentTrendDTO {
    private LocalDate date;
    private String period; // WEEK, MONTH
    private Double averageScore;
    private Integer totalAssessments;
    private Integer assessmentsCompleted;
    private Double completionRate;
    private String trend; // IMPROVING, STABLE, DECLINING
}
