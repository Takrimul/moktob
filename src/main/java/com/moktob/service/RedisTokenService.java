package com.moktob.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_PREFIX = "jwt:token:";
    private static final String USER_PREFIX = "jwt:user:";
    private static final String RESET_TOKEN_PREFIX = "reset:token:";
    
    public void storeToken(String token, String username, Long clientId, Long userId, Duration expiration) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            String userKey = USER_PREFIX + username;
            
            // Store token with user info
            TokenInfo tokenInfo = new TokenInfo(username, clientId, userId, System.currentTimeMillis());
            redisTemplate.opsForValue().set(tokenKey, tokenInfo, expiration);
            
            // Store user's current token for logout purposes
            redisTemplate.opsForValue().set(userKey, token, expiration);
            
            log.debug("Stored token for user: {} with expiration: {} seconds", username, expiration.getSeconds());
        } catch (Exception e) {
            log.error("Failed to store token in Redis for user: {}", username, e);
        }
    }
    
    public boolean isTokenValid(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
        } catch (Exception e) {
            log.error("Failed to check token validity in Redis", e);
            return false;
        }
    }
    
    public TokenInfo getTokenInfo(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            return (TokenInfo) redisTemplate.opsForValue().get(tokenKey);
        } catch (Exception e) {
            log.error("Failed to get token info from Redis", e);
            return null;
        }
    }
    
    public void removeToken(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            TokenInfo tokenInfo = getTokenInfo(token);
            
            if (tokenInfo != null) {
                String userKey = USER_PREFIX + tokenInfo.getUsername();
                redisTemplate.delete(tokenKey);
                redisTemplate.delete(userKey);
                log.debug("Removed token for user: {}", tokenInfo.getUsername());
            }
        } catch (Exception e) {
            log.error("Failed to remove token from Redis", e);
        }
    }
    
    public void removeUserToken(String username) {
        try {
            String userKey = USER_PREFIX + username;
            String token = (String) redisTemplate.opsForValue().get(userKey);
            
            if (token != null) {
                String tokenKey = TOKEN_PREFIX + token;
                redisTemplate.delete(tokenKey);
                redisTemplate.delete(userKey);
                log.debug("Removed token for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Failed to remove user token from Redis", e);
        }
    }
    
    // Reset token methods
    public void storeResetToken(String resetToken, String username, Long clientId, Long userId, Duration expiration) {
        try {
            String resetTokenKey = RESET_TOKEN_PREFIX + resetToken;
            
            // Store reset token with user info
            TokenInfo tokenInfo = new TokenInfo(username, clientId, userId, System.currentTimeMillis());
            redisTemplate.opsForValue().set(resetTokenKey, tokenInfo, expiration);
            
            log.debug("Stored reset token for user: {} with expiration: {} seconds", username, expiration.getSeconds());
        } catch (Exception e) {
            log.error("Failed to store reset token in Redis for user: {}", username, e);
        }
    }
    
    public java.util.Optional<String> validateResetToken(String resetToken) {
        try {
            String resetTokenKey = RESET_TOKEN_PREFIX + resetToken;
            TokenInfo tokenInfo = (TokenInfo) redisTemplate.opsForValue().get(resetTokenKey);
            
            if (tokenInfo != null) {
                return java.util.Optional.of(tokenInfo.getUsername());
            }
            return java.util.Optional.empty();
        } catch (Exception e) {
            log.error("Failed to validate reset token in Redis", e);
            return java.util.Optional.empty();
        }
    }
    
    public void removeResetToken(String resetToken) {
        try {
            String resetTokenKey = RESET_TOKEN_PREFIX + resetToken;
            redisTemplate.delete(resetTokenKey);
            log.debug("Removed reset token: {}", resetToken);
        } catch (Exception e) {
            log.error("Failed to remove reset token from Redis", e);
        }
    }
    
    public static class TokenInfo {
        private String username;
        private Long clientId;
        private Long userId;
        private Long createdAt;
        
        public TokenInfo() {}
        
        public TokenInfo(String username, Long clientId, Long userId, Long createdAt) {
            this.username = username;
            this.clientId = clientId;
            this.userId = userId;
            this.createdAt = createdAt;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public Long getClientId() { return clientId; }
        public void setClientId(Long clientId) { this.clientId = clientId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Long getCreatedAt() { return createdAt; }
        public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    }
}
