package com.moktob.communication;

import com.moktob.common.BaseEntity;
import com.moktob.core.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "published_by")
    private Long publishedBy;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "target_role", length = 50)
    private String targetRole;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by", insertable = false, updatable = false)
    private UserAccount publisher;
}
