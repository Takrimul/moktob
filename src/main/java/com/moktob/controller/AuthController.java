package com.moktob.controller;

import com.moktob.config.JwtUtil;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import com.moktob.dto.AuthenticationRequest;
import com.moktob.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        String passHash = passwordEncoder.encode(authenticationRequest.getPassword());
        log.info("Encoded password: {}", passHash);

        // Get user details from the authentication
        final UserDetails userDetails = userAccountRepository.findByUsername(authenticationRequest.getUsername())
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPasswordHash(),
                        user.getIsActive(),
                        true, true, true,
                        java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
                ))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String storedPasswordHash = userDetails.getPassword();
        log.info("Stored password hash: {}", storedPasswordHash);
        boolean matched = passwordEncoder.matches(authenticationRequest.getPassword(), storedPasswordHash);

        Optional<UserAccount> userAccount = userAccountRepository.findByUsernameWithRole(authenticationRequest.getUsername());

        if (userAccount.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }

        UserAccount user = userAccount.get();
        final String jwt = jwtUtil.generateToken(userDetails, user.getClientId(), user.getId());

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "ADMIN";

        return ResponseEntity.ok(new AuthenticationResponse(
                jwt,
                user.getClientId(),
                user.getId(),
                user.getUsername(),
                roleName
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Successfully logged out");
    }
}
