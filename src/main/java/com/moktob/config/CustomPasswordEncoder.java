package com.moktob.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder {
    
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(10);
    
    public String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
    
    public BCryptPasswordEncoder getEncoder() {
        return ENCODER;
    }
}
