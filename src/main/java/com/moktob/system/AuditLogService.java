package com.moktob.system;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    public List<AuditLog> getAllAuditLogs() {
        Long clientId = TenantContextHolder.getTenantId();
        return auditLogRepository.findByClientId(clientId);
    }
    
    public Optional<AuditLog> getAuditLogById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return auditLogRepository.findByClientIdAndId(clientId, id);
    }
    
    public AuditLog saveAuditLog(AuditLog auditLog) {
        Long clientId = TenantContextHolder.getTenantId();
        auditLog.setClientId(clientId);
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(auditLog);
    }
    
    public void deleteAuditLog(Long id) {
        auditLogRepository.deleteById(id);
    }
    
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        Long clientId = TenantContextHolder.getTenantId();
        return auditLogRepository.findByClientIdAndUserId(clientId, userId);
    }
    
    public List<AuditLog> getAuditLogsByAction(String action) {
        Long clientId = TenantContextHolder.getTenantId();
        return auditLogRepository.findByClientIdAndAction(clientId, action);
    }
    
    public List<AuditLog> getAuditLogsByTable(String tableName) {
        Long clientId = TenantContextHolder.getTenantId();
        return auditLogRepository.findByClientIdAndTableName(clientId, tableName);
    }
    
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return auditLogRepository.findByClientIdAndTimestampBetween(clientId, startDate, endDate);
    }
    
    public void logAction(Long userId, String action, String tableName, Long recordId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setTableName(tableName);
        auditLog.setRecordId(recordId);
        auditLog.setDetails(details);
        saveAuditLog(auditLog);
    }
}
