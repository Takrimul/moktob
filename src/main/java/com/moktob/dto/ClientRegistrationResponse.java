package com.moktob.dto;

import com.moktob.core.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationResponse {
    private Client client;
    private String adminUsername;
    private String adminPassword;
}
