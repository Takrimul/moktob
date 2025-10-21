package com.moktob.controller;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.SettingsRequest;
import com.moktob.service.SettingsService;
import com.moktob.service.UserContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingsController {

    private final SettingsService settingsService;
    private final UserContextService userContextService;

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getGeneralSettings() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        Map<String, Object> settings = settingsService.getGeneralSettings(clientId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/general")
    public ResponseEntity<String> updateGeneralSettings(@RequestBody SettingsRequest request) {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        settingsService.updateGeneralSettings(clientId, request);
        log.info("General settings updated for client: {}", clientId);
        return ResponseEntity.ok("General settings updated successfully");
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> getEmailSettings() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        Map<String, Object> settings = settingsService.getEmailSettings(clientId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/email")
    public ResponseEntity<String> updateEmailSettings(@RequestBody SettingsRequest request) {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        settingsService.updateEmailSettings(clientId, request);
        log.info("Email settings updated for client: {}", clientId);
        return ResponseEntity.ok("Email settings updated successfully");
    }

    @GetMapping("/security")
    public ResponseEntity<Map<String, Object>> getSecuritySettings() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        Map<String, Object> settings = settingsService.getSecuritySettings(clientId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/security")
    public ResponseEntity<String> updateSecuritySettings(@RequestBody SettingsRequest request) {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        settingsService.updateSecuritySettings(clientId, request);
        log.info("Security settings updated for client: {}", clientId);
        return ResponseEntity.ok("Security settings updated successfully");
    }

    @GetMapping("/backup")
    public ResponseEntity<Map<String, Object>> getBackupSettings() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        Map<String, Object> settings = settingsService.getBackupSettings(clientId);
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/backup")
    public ResponseEntity<String> updateBackupSettings(@RequestBody SettingsRequest request) {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        settingsService.updateBackupSettings(clientId, request);
        log.info("Backup settings updated for client: {}", clientId);
        return ResponseEntity.ok("Backup settings updated successfully");
    }

    @PostMapping("/email/test")
    public ResponseEntity<String> testEmail() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        boolean success = settingsService.testEmailSettings(clientId);
        
        if (success) {
            return ResponseEntity.ok("Test email sent successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to send test email");
        }
    }

    @PostMapping("/backup/create")
    public ResponseEntity<String> createBackup() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        
        Long clientId = TenantContextHolder.getTenantId();
        boolean success = settingsService.createBackup(clientId);
        
        if (success) {
            return ResponseEntity.ok("Backup created successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to create backup");
        }
    }

    private boolean isAdmin() {
        String userName = TenantContextHolder.getUsername();
        var userContext = userContextService.buildUserContext(userName);
        return userContext != null && userContext.isAdmin();
    }
}
