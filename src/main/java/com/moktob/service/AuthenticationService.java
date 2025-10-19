package com.moktob.service;

import com.moktob.common.TenantContextHolder;
import com.moktob.config.JwtAuthenticationResponse;
import com.moktob.config.JwtTokenUtil;
import com.moktob.config.LoginRequest;
import com.moktob.core.Role;
import com.moktob.core.RoleService;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import com.moktob.dto.ChangePasswordRequest;
import com.moktob.dto.CreateUserRequest;
import com.moktob.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserAccountRepository userAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public JwtAuthenticationResponse authenticate(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(userDetails.getUsername());

        if (userAccount.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserAccount user = userAccount.get();
        String token = jwtTokenUtil.generateToken(userDetails, user.getClientId(), user.getId());

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";

        return new JwtAuthenticationResponse(
                token,
                user.getClientId(),
                user.getId(),
                user.getUsername(),
                roleName
        );
    }

    public UserAccount getUserByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UserAccount createUser(CreateUserRequest request) {
        Long clientId = TenantContextHolder.getTenantId();

        // Check if a username already exists
        if (userAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setIsActive(true);
        user.setClientId(clientId);

        // Set role if provided
        if (request.getRoleName() != null) {
            Optional<Role> role = roleService.getRoleByName(request.getRoleName());
            role.ifPresent(r -> user.setRoleId(r.getId()));
        }

        return userAccountRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserAccount user = userOpt.get();

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Set new password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userAccountRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());
    }

    public void resetPassword(Long userId, ResetPasswordRequest request) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        UserAccount user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userAccountRepository.save(user);

        log.info("Password reset for user: {}", user.getUsername());
    }

    public String generateTemporaryPassword() {


        SecureRandom random = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();
        
        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        String SPECIAL = "!@#$%^&*";
        int LENGTH = 8;

        // Ensure at least one digit and one special character
        passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill remaining with mix of all types
        String allChars = UPPER + LOWER + DIGITS + SPECIAL;
        while (passwordChars.size() < LENGTH) {
            passwordChars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle for randomness
        Collections.shuffle(passwordChars, random);

        // Convert to string
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void debugPasswordEncoding(String plainPassword, String storedHash) {
        log.debug("Plain password: {}", plainPassword);
        log.debug("Stored hash: {}", storedHash);
        boolean matches = passwordEncoder.matches(plainPassword, storedHash);
        log.debug("Password matches: {}", matches);
        
        // Generate new hash for comparison
        String newHash = passwordEncoder.encode(plainPassword);
        log.debug("New hash for same password: {}", newHash);
        log.debug("New hash matches stored: {}", passwordEncoder.matches(plainPassword, newHash));
    }

    public List<UserAccount> getUsersByRole(String roleName) {
        Long clientId = TenantContextHolder.getTenantId();
        Optional<Role> role = roleService.getRoleByName(roleName);

        if (role.isEmpty()) {
            return new ArrayList<>();
        }

        return userAccountRepository.findByClientId(clientId).stream()
                .filter(user -> user.getRoleId() != null && user.getRoleId().equals(role.get().getId()))
                .toList();
    }
}