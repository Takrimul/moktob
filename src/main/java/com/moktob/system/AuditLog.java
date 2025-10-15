package com.moktob.system;

import com.moktob.common.BaseEntity;
import com.moktob.core.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "action", length = 100)
    private String action;
    
    @Column(name = "table_name", length = 100)
    private String tableName;
    
    @Column(name = "record_id")
    private Long recordId;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserAccount user;
}
