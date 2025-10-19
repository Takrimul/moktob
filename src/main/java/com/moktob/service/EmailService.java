package com.moktob.service;

import com.moktob.core.Client;
import com.moktob.core.UserAccount;

public interface EmailService {
    void sendClientRegistrationEmail(Client client, UserAccount adminUser, String temporaryPassword);
}
