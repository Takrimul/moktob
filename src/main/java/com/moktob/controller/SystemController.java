package com.moktob.controller;

import com.moktob.system.AuditLog;
import com.moktob.system.AuditLogService;
import com.moktob.system.SystemSetting;
import com.moktob.system.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {
    
    private final AuditLogService auditLogService;
    private final SystemSettingService systemSettingService;
    
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }
    
    @GetMapping("/audit-logs/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        Optional<AuditLog> auditLog = auditLogService.getAuditLogById(id);
        return auditLog.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/audit-logs/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUser(userId));
    }
    
    @GetMapping("/audit-logs/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByAction(action));
    }
    
    @GetMapping("/audit-logs/table/{tableName}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByTable(@PathVariable String tableName) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByTable(tableName));
    }
    
    @GetMapping("/settings")
    public ResponseEntity<List<SystemSetting>> getAllSettings() {
        return ResponseEntity.ok(systemSettingService.getAllSettings());
    }
    
    @GetMapping("/settings/{id}")
    public ResponseEntity<SystemSetting> getSettingById(@PathVariable Long id) {
        Optional<SystemSetting> setting = systemSettingService.getSettingById(id);
        return setting.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/settings")
    public ResponseEntity<SystemSetting> createSetting(@RequestBody SystemSetting setting) {
        return ResponseEntity.ok(systemSettingService.saveSetting(setting));
    }
    
    @PutMapping("/settings/{id}")
    public ResponseEntity<SystemSetting> updateSetting(@PathVariable Long id, @RequestBody SystemSetting setting) {
        setting.setId(id);
        return ResponseEntity.ok(systemSettingService.saveSetting(setting));
    }
    
    @DeleteMapping("/settings/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        systemSettingService.deleteSetting(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/settings/key/{keyName}")
    public ResponseEntity<SystemSetting> getSettingByKey(@PathVariable String keyName) {
        Optional<SystemSetting> setting = systemSettingService.getSettingByKey(keyName);
        return setting.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/settings/value/{keyName}")
    public ResponseEntity<String> getSettingValue(@PathVariable String keyName) {
        String value = systemSettingService.getSettingValue(keyName);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }
    
    @PostMapping("/settings/set")
    public ResponseEntity<Void> setSettingValue(@RequestParam String keyName, 
                                             @RequestParam String keyValue, 
                                             @RequestParam(required = false) String description) {
        systemSettingService.setSettingValue(keyName, keyValue, description);
        return ResponseEntity.ok().build();
    }
}
