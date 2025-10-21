package com.moktob.service;

import com.moktob.core.Client;
import com.moktob.core.UserAccount;

public interface EmailService {
    void sendClientRegistrationEmail(Client client, UserAccount adminUser, String temporaryPassword);
    void sendPasswordResetEmail(String toEmail, String username, String resetUrl);
    void sendWelcomeEmail(String toEmail, String username, String temporaryPassword);
    void sendStudentCredentialsEmail(String toEmail, String studentName, String username, String temporaryPassword);
    void sendTeacherCredentialsEmail(String toEmail, String teacherName, String username, String temporaryPassword);
}