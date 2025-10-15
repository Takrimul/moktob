package com.moktob.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private String type = "Bearer";
    private Long clientId;
    private Long userId;
    private String username;
    private String role;

    public JwtAuthenticationResponse(String token, Long clientId, Long userId, String username, String role) {
        this.token = token;
        this.clientId = clientId;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}
