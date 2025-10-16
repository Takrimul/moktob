package com.moktob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String jwt;
    private String type = "Bearer";
    private Long clientId;
    private Long userId;
    private String username;
    private String role;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public AuthenticationResponse(String jwt, Long clientId, Long userId, String username, String role) {
        this.jwt = jwt;
        this.clientId = clientId;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}
