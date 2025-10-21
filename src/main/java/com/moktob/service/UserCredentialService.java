package com.moktob.service;

import com.moktob.core.UserAccount;
import com.moktob.dto.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCredentialService {

    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Generate a secure temporary password
     */
    public String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure password has at least one of each type
        password.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt(26))); // Uppercase
        password.append("abcdefghijklmnopqrstuvwxyz".charAt(random.nextInt(26))); // Lowercase
        password.append("0123456789".charAt(random.nextInt(10))); // Number
        password.append("!@#$%^&*".charAt(random.nextInt(8))); // Special char
        
        // Fill remaining length (total 8-12 characters)
        int remainingLength = 4 + random.nextInt(5); // 4-8 more characters
        for (int i = 0; i < remainingLength; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        String result = password.toString();
        char[] array = result.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        
        return new String(array);
    }

    /**
     * Generate username from name
     */
    public String generateUsername(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
        
        // Convert to lowercase, replace spaces with dots, remove special characters
        String username = fullName.toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z0-9.]", "");
        
        // Ensure it starts and ends with a letter or number
        username = username.replaceAll("^\\.+|\\.+$", "");
        
        // Add random number if username is too short
        if (username.length() < 3) {
            username += new SecureRandom().nextInt(1000);
        }
        
        return username;
    }

    /**
     * Create user account for student
     */
    public UserAccount createStudentUser(String studentName, String email, String phone, boolean sendEmail) {
        try {
            // Generate username and password
            String username = generateUsername(studentName);
            String temporaryPassword = generateTemporaryPassword();
            
            // Create user request
            CreateUserRequest userRequest = new CreateUserRequest();
            userRequest.setUsername(username);
            userRequest.setPassword(temporaryPassword);
            userRequest.setFullName(studentName);
            userRequest.setEmail(email);
            userRequest.setPhone(phone); // Include phone number
            userRequest.setRoleName("STUDENT");
            
            // Create user account
            UserAccount userAccount = authenticationService.createUser(userRequest);
            
            // Send specific student email if requested
            if (sendEmail && email != null && !email.trim().isEmpty()) {
                try {
                    emailService.sendStudentCredentialsEmail(email, studentName, username, temporaryPassword);
                    log.info("Student credentials email sent successfully to: {} for user: {}", email, username);
                } catch (Exception e) {
                    log.error("Failed to send student credentials email to: {} for user: {}. Error: {}", 
                             email, username, e.getMessage(), e);
                    // Don't fail the user creation if email fails
                }
            }
            
            // Clear password hash from response for security
            userAccount.setPasswordHash(null);
            
            log.info("Student user account created successfully: {}", username);
            return userAccount;
            
        } catch (Exception e) {
            log.error("Failed to create student user account for: {}. Error: {}", 
                     studentName, e.getMessage(), e);
            throw new RuntimeException("Failed to create student user account: " + e.getMessage(), e);
        }
    }

    /**
     * Create user account for teacher
     */
    public UserAccount createTeacherUser(String teacherName, String email, String phone, boolean sendEmail) {
        try {
            // Generate username and password
            String username = generateUsername(teacherName);
            String temporaryPassword = generateTemporaryPassword();
            
            // Create user request
            CreateUserRequest userRequest = new CreateUserRequest();
            userRequest.setUsername(username);
            userRequest.setPassword(temporaryPassword);
            userRequest.setFullName(teacherName);
            userRequest.setEmail(email);
            userRequest.setPhone(phone); // Include phone number
            userRequest.setRoleName("TEACHER");
            
            // Create user account
            UserAccount userAccount = authenticationService.createUser(userRequest);
            
            // Send specific teacher email if requested
            if (sendEmail && email != null && !email.trim().isEmpty()) {
                try {
                    emailService.sendTeacherCredentialsEmail(email, teacherName, username, temporaryPassword);
                    log.info("Teacher credentials email sent successfully to: {} for user: {}", email, username);
                } catch (Exception e) {
                    log.error("Failed to send teacher credentials email to: {} for user: {}. Error: {}", 
                             email, username, e.getMessage(), e);
                    // Don't fail the user creation if email fails
                }
            }
            
            // Clear password hash from response for security
            userAccount.setPasswordHash(null);
            
            log.info("Teacher user account created successfully: {}", username);
            return userAccount;
            
        } catch (Exception e) {
            log.error("Failed to create teacher user account for: {}. Error: {}", 
                     teacherName, e.getMessage(), e);
            throw new RuntimeException("Failed to create teacher user account: " + e.getMessage(), e);
        }
    }
}