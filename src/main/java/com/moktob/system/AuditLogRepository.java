package com.moktob.system;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByClientId(Long clientId);
    
    Optional<AuditLog> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT a FROM AuditLog a WHERE a.clientId = :clientId AND a.userId = :userId")
    List<AuditLog> findByClientIdAndUserId(@Param("clientId") Long clientId, @Param("userId") Long userId);
    
    @Query("SELECT a FROM AuditLog a WHERE a.clientId = :clientId AND a.action = :action")
    List<AuditLog> findByClientIdAndAction(@Param("clientId") Long clientId, @Param("action") String action);
    
    @Query("SELECT a FROM AuditLog a WHERE a.clientId = :clientId AND a.tableName = :tableName")
    List<AuditLog> findByClientIdAndTableName(@Param("clientId") Long clientId, @Param("tableName") String tableName);
    
    @Query("SELECT a FROM AuditLog a WHERE a.clientId = :clientId AND a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByClientIdAndTimestampBetween(@Param("clientId") Long clientId, 
                                                   @Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
}
