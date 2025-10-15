package com.moktob.service;

import com.moktob.dto.ClientRegistrationRequest;
import com.moktob.dto.ClientRegistrationResponse;

public interface ClientRegistrationService {
    ClientRegistrationResponse registerClient(ClientRegistrationRequest request);
}
