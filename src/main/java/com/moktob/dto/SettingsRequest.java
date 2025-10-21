package com.moktob.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SettingsRequest {
    private Map<String, Object> settings;
    
    // General settings
    private String schoolName;
    private String schoolAddress;
    private String phoneNumber;
    private String email;
    private String academicYear;
    
    // Email settings
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private Boolean enableSSL;
    
    // Security settings
    private Integer sessionTimeout;
    private String passwordPolicy;
    private Boolean twoFactor;
    private Boolean loginLogging;
    
    // Backup settings
    private String backupFrequency;
    private String backupLocation;
    private Boolean autoBackup;
    private Integer retentionPeriod;
}
