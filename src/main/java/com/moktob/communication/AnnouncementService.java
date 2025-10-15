package com.moktob.communication;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    
    public List<Announcement> getAllAnnouncements() {
        Long clientId = TenantContextHolder.getTenantId();
        return announcementRepository.findByClientId(clientId);
    }
    
    public Optional<Announcement> getAnnouncementById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return announcementRepository.findByClientIdAndId(clientId, id);
    }
    
    public Announcement saveAnnouncement(Announcement announcement) {
        Long clientId = TenantContextHolder.getTenantId();
        announcement.setClientId(clientId);
        announcement.setPublishedAt(LocalDateTime.now());
        return announcementRepository.save(announcement);
    }
    
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
    
    public List<Announcement> getAnnouncementsByPublisher(Long publishedBy) {
        Long clientId = TenantContextHolder.getTenantId();
        return announcementRepository.findByClientIdAndPublishedBy(clientId, publishedBy);
    }
    
    public List<Announcement> getAnnouncementsByTargetRole(String targetRole) {
        Long clientId = TenantContextHolder.getTenantId();
        return announcementRepository.findByClientIdAndTargetRole(clientId, targetRole);
    }
    
    public List<Announcement> getAnnouncementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return announcementRepository.findByClientIdAndPublishedAtBetween(clientId, startDate, endDate);
    }
}
