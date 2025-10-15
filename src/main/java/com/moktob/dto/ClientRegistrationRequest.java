package com.moktob.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationRequest {
    @NotBlank
    private String clientName;
    
    @Email
    private String contactEmail;
    
    private String contactPhone;
    private String address;
    private String subscriptionPlan;
    private String expiryDate;
    
    @NotBlank
    private String adminUsername;
    
    @NotBlank
    private String adminFullName;
    
    @Email
    private String adminEmail;
    
    private String adminPhone;
}
