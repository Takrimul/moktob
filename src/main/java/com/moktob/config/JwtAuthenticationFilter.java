package com.moktob.config;

import com.moktob.common.TenantContextHolder;
import com.moktob.common.UserContext;
import com.moktob.service.RedisTokenService;
import com.moktob.service.UserContextService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;
    private final UserContextService userContextService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        Long clientId = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
                clientId = jwtUtil.getClientIdFromToken(jwtToken);
            } catch (Exception e) {
                log.error("Unable to get JWT Token or JWT Token has expired: {}", e.getMessage());
            }
        } else if (requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")) {
            log.debug("JWT Token does not begin with Bearer String");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Check if a token exists in Redis (not logged out)
//                if (!redisTokenService.isTokenValid(jwtToken)) {
//                    log.debug("Token not found in Redis or has been logged out: {}", username);
//                    chain.doFilter(request, response);
//                    return;
//                }
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Build and set complete user context
                    try {
                        UserContext userContext = userContextService.buildUserContext(username);
                        if (userContext != null) {
                            TenantContextHolder.setUserContext(userContext);
                            log.debug("JWT Filter: Set complete user context for {}: userId={}, role={}, tenantId={}", 
                                     username, userContext.getUserId(), userContext.getRoleName(), userContext.getTenantId());
                        } else {
                            // Fallback to legacy tenant ID setting
                            TenantContextHolder.setTenantId(clientId);
                            log.debug("JWT Filter: Set tenant ID fallback for {}: clientId={}", username, clientId);
                        }
                    } catch (Exception e) {
                        log.error("Failed to set user context for {}: {}", username, e.getMessage());
                        // Fallback to legacy tenant ID setting
                        TenantContextHolder.setTenantId(clientId);
                    }
                }
            } catch (Exception e) {
                log.error("JWT authentication failed for user: {}", username, e);
            }
        }

        chain.doFilter(request, response);
        
        TenantContextHolder.clear();
    }
}
