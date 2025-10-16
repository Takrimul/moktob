package com.moktob.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j // Add this annotation
public class CustomPasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);

    @Override
    public String encode(CharSequence rawPassword) {
        String encoded = bcrypt.encode(rawPassword);
        log.debug("Encoding password. Result starts with BCrypt prefix: {}",
                encoded.startsWith("$2a$") || encoded.startsWith("$2b$") || encoded.startsWith("$2y$"));
        return encoded;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        boolean b = encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") ||
                encodedPassword.startsWith("$2y$");
        log.debug("Password matching attempt. Encoded format: {}",
                b ? "BCrypt" : "Unknown");

        // Rest of your implementation...

        // Log result
        boolean result = false;
        if (encodedPassword.trim().isEmpty()) {
            log.debug("Password matching failed: Null or empty encoded password");
            return false;
        }

        if (b) {
            result = bcrypt.matches(rawPassword, encodedPassword);
            log.debug("BCrypt password matching result: {}", result);
            return result;
        }

        try {
            result = bcrypt.matches(rawPassword, encodedPassword);
            log.debug("Fallback password matching result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Password matching error: {}", e.getMessage());
            return false;
        }
    }
}
