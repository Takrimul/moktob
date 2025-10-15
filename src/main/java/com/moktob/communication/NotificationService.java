package com.moktob.communication;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public List<Notification> getAllNotifications() {
        Long clientId = TenantContextHolder.getTenantId();
        return notificationRepository.findByClientId(clientId);
    }
    
    public Optional<Notification> getNotificationById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return notificationRepository.findByClientIdAndId(clientId, id);
    }
    
    public Notification saveNotification(Notification notification) {
        Long clientId = TenantContextHolder.getTenantId();
        notification.setClientId(clientId);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
    
    public List<Notification> getNotificationsByUser(Long userId) {
        Long clientId = TenantContextHolder.getTenantId();
        return notificationRepository.findByClientIdAndRecipientUserId(clientId, userId);
    }
    
    public List<Notification> getUnreadNotifications() {
        Long clientId = TenantContextHolder.getTenantId();
        return notificationRepository.findByClientIdAndIsRead(clientId, false);
    }
    
    public List<Notification> getNotificationsByType(String type) {
        Long clientId = TenantContextHolder.getTenantId();
        return notificationRepository.findByClientIdAndType(clientId, type);
    }
    
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return notificationRepository.findByClientIdAndCreatedAtBetween(clientId, startDate, endDate);
    }
    
    public Notification markAsRead(Long id) {
        Optional<Notification> notification = getNotificationById(id);
        if (notification.isPresent()) {
            notification.get().setIsRead(true);
            return notificationRepository.save(notification.get());
        }
        return null;
    }
}
