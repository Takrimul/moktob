package com.moktob.core;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserAccount> getAllUsers() {
        Long clientId = TenantContextHolder.getTenantId();
        return userAccountRepository.findByClientId(clientId);
    }
    
    public Optional<UserAccount> getUserById(Long id) {
        return userAccountRepository.findById(id);
    }
    
    public UserAccount saveUser(UserAccount user) {
        Long clientId = TenantContextHolder.getTenantId();
        user.setClientId(clientId);
        
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        return userAccountRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userAccountRepository.deleteById(id);
    }
    
    public Optional<UserAccount> getUserByUsername(String username) {
        Long clientId = TenantContextHolder.getTenantId();
        return userAccountRepository.findByClientIdAndUsername(clientId, username);
    }
    
    public List<UserAccount> getActiveUsers() {
        Long clientId = TenantContextHolder.getTenantId();
        return userAccountRepository.findByClientIdAndIsActiveTrue(clientId);
    }
}
