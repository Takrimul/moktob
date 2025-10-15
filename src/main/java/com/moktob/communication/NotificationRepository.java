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
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByClientId(Long clientId);
    
    Optional<Notification> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT n FROM Notification n WHERE n.clientId = :clientId AND n.recipientUserId = :userId")
    List<Notification> findByClientIdAndRecipientUserId(@Param("clientId") Long clientId, @Param("userId") Long userId);
    
    @Query("SELECT n FROM Notification n WHERE n.clientId = :clientId AND n.isRead = :isRead")
    List<Notification> findByClientIdAndIsRead(@Param("clientId") Long clientId, @Param("isRead") Boolean isRead);
    
    @Query("SELECT n FROM Notification n WHERE n.clientId = :clientId AND n.type = :type")
    List<Notification> findByClientIdAndType(@Param("clientId") Long clientId, @Param("type") String type);
    
    @Query("SELECT n FROM Notification n WHERE n.clientId = :clientId AND n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByClientIdAndCreatedAtBetween(@Param("clientId") Long clientId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
}
