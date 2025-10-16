package com.moktob.controller;

import com.moktob.config.JwtAuthenticationResponse;
import com.moktob.config.LoginRequest;
import com.moktob.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtAuthenticationResponse response = authenticationService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.error("Bad credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.error("User not found: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Successfully logged out");
    }
}
