package com.moktob.service;

import com.moktob.config.JwtAuthenticationResponse;
import com.moktob.config.JwtTokenUtil;
import com.moktob.config.LoginRequest;
import com.moktob.core.Role;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(username);
        
        if (userAccount.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        UserAccount user = userAccount.get();
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(user.getRole() != null ? user.getRole().getRoleName() : "USER")
                .build();
    }

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
}
