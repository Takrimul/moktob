package com.moktob.controller;

import com.moktob.config.JwtUtil;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import com.moktob.dto.AuthenticationRequest;
import com.moktob.dto.AuthenticationResponse;
import com.moktob.dto.ForgotPasswordRequest;
import com.moktob.dto.ResetPasswordRequest;
import com.moktob.service.RedisTokenService;
import com.moktob.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenService redisTokenService;
    private final EmailService emailService;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        // Get user with role eagerly loaded
        Optional<UserAccount> userAccount = userAccountRepository.findByUsernameWithRole(authenticationRequest.getUsername());
        
        if (userAccount.isEmpty()) {
            log.error("User not found: {}", authenticationRequest.getUsername());
            return ResponseEntity.status(401).body("Incorrect username or password");
        }

        UserAccount user = userAccount.get();
        
        // Custom password check using the same encoder used during registration
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPasswordHash())) {
            String hashPass = passwordEncoder.encode(authenticationRequest.getPassword());
            log.debug("Password hash of login request data: {} is {}", user.getUsername(), hashPass);
            log.debug("Password in db: {}", user.getPasswordHash());
            log.error("Bad credentials for user: {}", authenticationRequest.getUsername());
            return ResponseEntity.status(401).body("Incorrect username or password");
        }
        
        // Check if user is active
        if (!user.getIsActive()) {
            log.error("Inactive user attempted login: {}", authenticationRequest.getUsername());
            return ResponseEntity.status(401).body("Account is deactivated");
        }

        // Create UserDetails for JWT generation
        final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getIsActive(),
                true, true, true,
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
        );
        
        final String jwt = jwtUtil.generateToken(userDetails, user.getClientId(), user.getId());
        
        // Store token in Redis with expiration
        redisTokenService.storeToken(
            jwt, 
            user.getUsername(), 
            user.getClientId(), 
            user.getId(), 
            java.time.Duration.ofMillis(jwtExpiration)
        );
        
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

        return ResponseEntity.ok(new AuthenticationResponse(
                jwt,
                user.getClientId(),
                user.getId(),
                user.getUsername(),
                roleName
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                
                // Remove token from Redis
                redisTokenService.removeToken(jwtToken);
                
                log.info("User logged out successfully");
                return ResponseEntity.ok("Successfully logged out");
            } else {
                return ResponseEntity.badRequest().body("No valid token provided");
            }
        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.status(500).body("Logout failed");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            log.info("Forgot password request for email: {}", request.getEmail());
            
            // Find user by email
            Optional<UserAccount> userOptional = userAccountRepository.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                // For security reasons, don't reveal if email exists or not
                log.warn("Forgot password request for non-existent email: {}", request.getEmail());
                return ResponseEntity.ok().body("If the email exists, a password reset link has been sent.");
            }
            
            UserAccount user = userOptional.get();
            
            // Check if user is active
            if (!user.getIsActive()) {
                log.warn("Forgot password request for inactive user: {}", request.getEmail());
                return ResponseEntity.ok().body("If the email exists, a password reset link has been sent.");
            }
            
            // Generate reset token
            String resetToken = UUID.randomUUID().toString();
            
            // Store reset token in Redis with 1 hour expiration
            redisTokenService.storeResetToken(
                resetToken, 
                user.getUsername(), 
                user.getClientId(), 
                user.getId(),
                java.time.Duration.ofHours(1)
            );
            
            // Send reset email
            String resetUrl = "http://localhost:8080/moktob/reset-password?token=" + resetToken;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetUrl);
            
            log.info("Password reset email sent to: {}", request.getEmail());
            return ResponseEntity.ok().body("If the email exists, a password reset link has been sent.");
            
        } catch (Exception e) {
            log.error("Error processing forgot password request", e);
            return ResponseEntity.status(500).body("An error occurred. Please try again.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            log.info("Reset password request for token: {}", request.getToken());
            
            // Validate reset token
            Optional<String> usernameOptional = redisTokenService.validateResetToken(request.getToken());
            
            if (usernameOptional.isEmpty()) {
                log.warn("Invalid or expired reset token: {}", request.getToken());
                return ResponseEntity.badRequest().body("Invalid or expired reset token.");
            }
            
            String username = usernameOptional.get();
            
            // Find user
            Optional<UserAccount> userOptional = userAccountRepository.findByUsernameWithRole(username);
            
            if (userOptional.isEmpty()) {
                log.error("User not found for reset token: {}", username);
                return ResponseEntity.badRequest().body("Invalid reset token.");
            }
            
            UserAccount user = userOptional.get();
            
            // Update password
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPasswordHash(encodedPassword);
            userAccountRepository.save(user);
            
            // Remove reset token
            redisTokenService.removeResetToken(request.getToken());
            
            log.info("Password reset successful for user: {}", username);
            return ResponseEntity.ok().body("Password has been reset successfully.");
            
        } catch (Exception e) {
            log.error("Error processing reset password request", e);
            return ResponseEntity.status(500).body("An error occurred. Please try again.");
        }
    }
}
