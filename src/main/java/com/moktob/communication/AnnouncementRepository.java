package com.moktob.communication;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByClientId(Long clientId);
    
    Optional<Announcement> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT a FROM Announcement a WHERE a.clientId = :clientId AND a.publishedBy = :publishedBy")
    List<Announcement> findByClientIdAndPublishedBy(@Param("clientId") Long clientId, @Param("publishedBy") Long publishedBy);
    
    @Query("SELECT a FROM Announcement a WHERE a.clientId = :clientId AND a.targetRole = :targetRole")
    List<Announcement> findByClientIdAndTargetRole(@Param("clientId") Long clientId, @Param("targetRole") String targetRole);
    
    @Query("SELECT a FROM Announcement a WHERE a.clientId = :clientId AND a.publishedAt BETWEEN :startDate AND :endDate")
    List<Announcement> findByClientIdAndPublishedAtBetween(@Param("clientId") Long clientId, 
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
}
