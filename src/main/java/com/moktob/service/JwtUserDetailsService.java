package com.moktob.service;

import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        Optional<UserAccount> userAccount = userAccountRepository.findByUsername(username);
        
        if (userAccount.isEmpty()) {
            log.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        UserAccount user = userAccount.get();
        log.debug("Found user: {}, client_id: {}, is_active: {}", username, user.getClientId(), user.getIsActive());
        
        // Check if user is active
        if (!user.getIsActive()) {
            log.error("User account is disabled: {}", username);
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }
        
        return new User(
            user.getUsername(),
            user.getPasswordHash(),
            user.getIsActive(),
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
