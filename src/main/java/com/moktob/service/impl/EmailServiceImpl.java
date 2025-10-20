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
            
            Login URL: http://localhost:8080/moktob/login
            
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

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String resetUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset - Moktob Management System");
            
            String emailBody = buildPasswordResetEmailBody(username, resetUrl);
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    private String buildPasswordResetEmailBody(String username, String resetUrl) {
        return String.format("""
            Assalamu Alaikum %s,
            
            You have requested to reset your password for your Moktob Management System account.
            
            To reset your password, please click on the link below:
            %s
            
            This link will expire in 1 hour for security reasons.
            
            If you did not request this password reset, please ignore this email and your password will remain unchanged.
            
            For security reasons, please do not share this link with anyone.
            
            Best regards,
            Moktob Management System Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """, username, resetUrl);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username, String temporaryPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to Moktob Management System");
            
            String emailBody = buildWelcomeEmailBody(username, temporaryPassword);
            message.setText(emailBody);
            
            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
    
    private String buildWelcomeEmailBody(String username, String temporaryPassword) {
        return String.format("""
            Assalamu Alaikum %s,
            
            Welcome to Moktob Management System!
            
            Your account has been created successfully. Please use the following credentials to login:
            
            Username: %s
            Temporary Password: %s
            
            IMPORTANT: Please change your password after your first login for security reasons.
            
            You can access the system at: http://localhost:8080/moktob/login
            
            If you have any questions or need assistance, please contact our support team.
            
            Best regards,
            Moktob Management System Team
            
            ---
            This is an automated message. Please do not reply to this email.
            """, username, username, temporaryPassword);
    }
}
