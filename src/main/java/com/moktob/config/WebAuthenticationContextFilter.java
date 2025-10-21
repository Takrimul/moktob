package com.moktob.config;

import com.moktob.common.TenantContextHolder;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class WebAuthenticationContextFilter implements Filter {

    private final UserAccountRepository userAccountRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // Only process web page requests (not API requests)
            if (isWebPageRequest(httpRequest)) {
                populateTenantContextFromJwtToken(httpRequest);
            }
            
        } catch (Exception e) {
            log.error("Error populating tenant context for web request", e);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            // Don't clear here - let TenantContextFilter handle it
        }
    }
    
    private boolean isWebPageRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // Process web page requests but not API requests
        return requestURI.startsWith("/moktob/") && !requestURI.startsWith("/moktob/api/");
    }
    
    private void populateTenantContextFromJwtToken(HttpServletRequest request) {
        // First try to get clientId from JWT token in Authorization header
        String requestTokenHeader = request.getHeader("Authorization");
        
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                Long clientId = jwtUtil.getClientIdFromToken(jwtToken);
                if (clientId != null) {
                    TenantContextHolder.setTenantId(clientId);
                    log.debug("WebAuthenticationContextFilter: Set tenant context from Authorization header to clientId: {}", clientId);
                    return;
                }
            } catch (Exception e) {
                log.debug("WebAuthenticationContextFilter: Could not extract clientId from Authorization header JWT token: {}", e.getMessage());
            }
        }
        
        // Try to get JWT token from cookie
        String jwtTokenFromCookie = getJwtTokenFromCookie(request);
        if (jwtTokenFromCookie != null) {
            try {
                Long clientId = jwtUtil.getClientIdFromToken(jwtTokenFromCookie);
                if (clientId != null) {
                    TenantContextHolder.setTenantId(clientId);
                    log.debug("WebAuthenticationContextFilter: Set tenant context from cookie to clientId: {}", clientId);
                    return;
                }
            } catch (Exception e) {
                log.debug("WebAuthenticationContextFilter: Could not extract clientId from cookie JWT token: {}", e.getMessage());
            }
        }
        
        // If no JWT token or couldn't extract clientId, try SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof UserDetails) {
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // Get user account with client information
            Optional<UserAccount> userAccountOpt = userAccountRepository.findByUsernameWithRole(username);
            
            if (userAccountOpt.isPresent()) {
                UserAccount userAccount = userAccountOpt.get();
                Long clientId = userAccount.getClientId();
                
                // Set tenant context
                TenantContextHolder.setTenantId(clientId);
                log.debug("WebAuthenticationContextFilter: Set tenant context from SecurityContext to clientId: {} for user: {}", 
                         clientId, username);
            } else {
                log.warn("WebAuthenticationContextFilter: User account not found for username: {}", username);
            }
        } else {
            log.debug("WebAuthenticationContextFilter: No JWT token or authenticated user found");
        }
    }
    
    private String getJwtTokenFromCookie(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] parts = cookie.trim().split("=", 2);
                if (parts.length == 2 && "authToken".equals(parts[0])) {
                    return parts[1];
                }
            }
        }
        return null;
    }
}