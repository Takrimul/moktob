package com.moktob.config;

import com.moktob.common.TenantContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = httpRequest.getHeader("X-Tenant-ID");
        
        if (tenantId != null) {
            try {
                Long tenantIdLong = Long.parseLong(tenantId);
                TenantContextHolder.setTenantId(tenantIdLong);
                log.debug("Set tenant ID from header: {}", tenantIdLong);
            } catch (NumberFormatException e) {
                log.warn("Invalid tenant ID format: {}", tenantId);
            }
        } else {
            log.debug("No tenant ID provided in request header - will be set by JWT filter if authenticated");
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}
