package com.moktob.config;

import com.moktob.common.TenantContextHolder;
import com.moktob.common.UserContext;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import com.moktob.service.UserContextService;
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
@Order(2) // Ensure this runs after TenantContextFilter but before JwtAuthenticationFilter
public class WebAuthenticationContextFilter implements Filter {

    private final UserAccountRepository userAccountRepository;
    private final JwtUtil jwtUtil;
    private final UserContextService userContextService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            // Only process web page requests (not API requests)
            if (isWebPageRequest(httpRequest)) {
                populateUserContextFromRequest(httpRequest);
            }

        } catch (Exception e) {
            log.error("Error populating user context for web request", e);
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

    private void populateUserContextFromRequest(HttpServletRequest request) {
        // First try to get user context from JWT token in Authorization header
        String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(jwtToken);
                UserContext userContext = userContextService.buildUserContext(username);
                if (userContext != null) {
                    TenantContextHolder.setUserContext(userContext);
                    log.debug("WebAuthenticationContextFilter: Set user context from Authorization header for user: {}", username);
                    return;
                }
            } catch (Exception e) {
                log.debug("WebAuthenticationContextFilter: Could not extract user context from Authorization header JWT token: {}", e.getMessage());
            }
        }

        // Try to get JWT token from cookie
        String jwtTokenFromCookie = getJwtTokenFromCookie(request);
        if (jwtTokenFromCookie != null) {
            try {
                String username = jwtUtil.extractUsername(jwtTokenFromCookie);
                UserContext userContext = userContextService.buildUserContext(username);
                if (userContext != null) {
                    TenantContextHolder.setUserContext(userContext);
                    log.debug("WebAuthenticationContextFilter: Set user context from cookie for user: {}", username);
                    return;
                }
            } catch (Exception e) {
                log.debug("WebAuthenticationContextFilter: Could not extract user context from cookie JWT token: {}", e.getMessage());
            }
        }

        // If no JWT token or couldn't extract user context, try SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
            authentication.getPrincipal() instanceof UserDetails) {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // Build complete user context
            UserContext userContext = userContextService.buildUserContext(username);
            if (userContext != null) {
                TenantContextHolder.setUserContext(userContext);
                log.debug("WebAuthenticationContextFilter: Set user context from SecurityContext for user: {}", username);
            } else {
                log.warn("WebAuthenticationContextFilter: Could not build user context for username: {}", username);
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