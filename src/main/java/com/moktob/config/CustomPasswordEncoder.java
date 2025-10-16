package com.moktob.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);

    @Override
    public String encode(CharSequence rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Handle null or empty encoded password
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            return false;
        }
        
        // If it's already a BCrypt hash, use BCrypt directly
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            return bcrypt.matches(rawPassword, encodedPassword);
        }
        
        // For other formats, try BCrypt anyway (it will handle gracefully)
        try {
            return bcrypt.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
