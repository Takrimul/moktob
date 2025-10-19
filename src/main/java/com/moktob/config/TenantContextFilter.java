package com.moktob.config;

import com.moktob.common.TenantContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class TenantContextFilter implements Filter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // Get client ID from JWT token in Authorization header
            Long clientId = extractClientIdFromAuthHeader(httpRequest);
            
            // If client ID is found, set it in tenant context
            if (clientId != null) {
                TenantContextHolder.setTenantId(clientId);
                log.info("TenantContextFilter: Set tenant context to clientId: {}", clientId);
            } else {
                log.debug("TenantContextFilter: No client ID found in Authorization header");
            }
            
        } catch (Exception e) {
            log.error("Error setting tenant context", e);
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            // Clear tenant context after request
            TenantContextHolder.clear();
        }
    }
    
    private Long extractClientIdFromAuthHeader(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.debug("Extracting client ID from token: {}", token.substring(0, Math.min(20, token.length())) + "...");
                return jwtTokenUtil.getClientIdFromToken(token);
            }
            return null;
        } catch (Exception e) {
            log.warn("Error extracting client ID from auth header: {}", e.getMessage());
            return null;
        }
    }
}
