package com.moktob.service.impl;

import com.moktob.core.Client;
import com.moktob.core.UserAccount;
import com.moktob.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendClientRegistrationEmail(Client client, UserAccount adminUser, String temporaryPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(client.getContactEmail());
            message.setSubject("Welcome to Moktob Management System - Your Account Details");
            
            String emailBody = buildRegistrationEmailBody(client, adminUser, temporaryPassword);
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Registration email sent successfully to: {}", client.getContactEmail());
            
        } catch (Exception e) {
            log.error("Failed to send registration email to: {}. Error: {}", 
                     client.getContactEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send registration email", e);
        }
    }

    private String buildRegistrationEmailBody(Client client, UserAccount adminUser, String temporaryPassword) {
        return String.format("""
            Dear %s,
            
            Welcome to Moktob Management System!
            
            Your organization "%s" has been successfully registered in our system.
            
            Your Admin Account Details:
            ===========================
            Username: %s
            Temporary Password: %s
            
            Important Instructions:
            ======================
            1. Please log in using the credentials above
            2. Change your password immediately after first login
            3. You can now start managing your organization
            
            Login URL: http://localhost:8080/moktob/api/auth/login
            
            If you have any questions or need assistance, please don't hesitate to contact us.
            
            Best regards,
            Moktob Management Team
            """, 
            adminUser.getFullName(),
            client.getClientName(),
            adminUser.getUsername(),
            temporaryPassword
        );
    }
}
