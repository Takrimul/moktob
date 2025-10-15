package com.moktob.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank
    @Size(min = 6)
    private String currentPassword;
    
    @NotBlank
    @Size(min = 6)
    private String newPassword;
}
