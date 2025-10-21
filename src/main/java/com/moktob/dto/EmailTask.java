package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTask {
    
    private String taskId;
    private EmailType emailType;
    private String toEmail;
    private String toName;
    private String username;
    private String temporaryPassword;
    private String subject;
    private String messageBody;
    private LocalDateTime createdAt;
    private int retryCount;
    private EmailStatus status;
    
    public enum EmailType {
        STUDENT_CREDENTIALS,
        TEACHER_CREDENTIALS,
        WELCOME_EMAIL,
        PASSWORD_RESET,
        VERIFICATION_EMAIL
    }
    
    public enum EmailStatus {
        PENDING,
        PROCESSING,
        SENT,
        FAILED,
        RETRY
    }
}
