package com.moktob.controller;

import com.moktob.config.JwtUtil;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import com.moktob.dto.AuthenticationRequest;
import com.moktob.dto.AuthenticationResponse;
import com.moktob.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenService redisTokenService;
    
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
}
