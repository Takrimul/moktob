package com.moktob.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Sha512PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {
    private final Sha512PasswordEncoder sha512 = new Sha512PasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return CoreUtils.password.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // sha512
        if (CoreUtils.string.length(encodedPassword) >= 128) {
            return sha512.matches(rawPassword, encodedPassword);
        }

        // BCrypt
        return CoreUtils.password.compare(rawPassword, encodedPassword);
    }
}
