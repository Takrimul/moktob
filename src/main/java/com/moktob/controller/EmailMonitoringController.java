package com.moktob.controller;

import com.moktob.service.AsyncEmailProcessorService;
import com.moktob.service.EmailQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/email")
@RequiredArgsConstructor
@Slf4j
public class EmailMonitoringController {

    private final AsyncEmailProcessorService asyncEmailProcessorService;
    private final EmailQueueService emailQueueService;

    /**
     * Get email processor status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getEmailProcessorStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("processorRunning", asyncEmailProcessorService.isProcessorRunning());
        status.put("processorStatus", asyncEmailProcessorService.getProcessorStatus());
        status.put("queueStatistics", asyncEmailProcessorService.getQueueStatistics());
        status.put("queueSize", emailQueueService.getQueueSize());
        status.put("queueEmpty", emailQueueService.isQueueEmpty());
        
        return ResponseEntity.ok(status);
    }

    /**
     * Start email processor
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startEmailProcessor() {
        try {
            asyncEmailProcessorService.startEmailProcessor();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email processor started successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to start email processor", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to start email processor: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Stop email processor
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stopEmailProcessor() {
        try {
            asyncEmailProcessorService.stopEmailProcessor();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email processor stopped successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to stop email processor", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to stop email processor: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Clear email queue
     */
    @PostMapping("/clear-queue")
    public ResponseEntity<Map<String, String>> clearEmailQueue() {
        try {
            int clearedCount = emailQueueService.getQueueSize();
            emailQueueService.clearQueue();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cleared " + clearedCount + " pending email tasks");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to clear email queue", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to clear email queue: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get queue statistics
     */
    @GetMapping("/queue-stats")
    public ResponseEntity<Map<String, Object>> getQueueStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queueSize", emailQueueService.getQueueSize());
        stats.put("queueEmpty", emailQueueService.isQueueEmpty());
        stats.put("queueCapacity", emailQueueService.getQueueCapacity());
        stats.put("queueStats", emailQueueService.getQueueStats());
        
        return ResponseEntity.ok(stats);
    }
}
