package com.moktob.service;

import com.moktob.dto.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class EmailQueueService {
    
    private final BlockingQueue<EmailTask> emailQueue;
    private final AtomicInteger taskCounter;
    
    public EmailQueueService() {
        this.emailQueue = new LinkedBlockingQueue<>();
        this.taskCounter = new AtomicInteger(0);
        log.info("EmailQueueService initialized with capacity: {}", Integer.MAX_VALUE);
    }
    
    /**
     * Add email task to the queue
     */
    public boolean enqueueEmailTask(EmailTask emailTask) {
        try {
            if (emailTask.getTaskId() == null) {
                emailTask.setTaskId("EMAIL_" + taskCounter.incrementAndGet() + "_" + System.currentTimeMillis());
            }
            emailTask.setStatus(EmailTask.EmailStatus.PENDING);
            emailTask.setCreatedAt(java.time.LocalDateTime.now());
            
            boolean added = emailQueue.offer(emailTask);
            if (added) {
                log.debug("Email task enqueued successfully: {} for {}", 
                         emailTask.getTaskId(), emailTask.getToEmail());
            } else {
                log.warn("Failed to enqueue email task: {} - Queue might be full", emailTask.getTaskId());
            }
            return added;
        } catch (Exception e) {
            log.error("Error enqueuing email task: {}", emailTask.getTaskId(), e);
            return false;
        }
    }
    
    /**
     * Get next email task from queue (blocking)
     */
    public EmailTask dequeueEmailTask() throws InterruptedException {
        EmailTask task = emailQueue.take();
        task.setStatus(EmailTask.EmailStatus.PROCESSING);
        log.debug("Email task dequeued: {} for {}", task.getTaskId(), task.getToEmail());
        return task;
    }
    
    /**
     * Get next email task from queue (non-blocking)
     */
    public EmailTask pollEmailTask() {
        EmailTask task = emailQueue.poll();
        if (task != null) {
            task.setStatus(EmailTask.EmailStatus.PROCESSING);
            log.debug("Email task polled: {} for {}", task.getTaskId(), task.getToEmail());
        }
        return task;
    }
    
    /**
     * Get queue size
     */
    public int getQueueSize() {
        return emailQueue.size();
    }
    
    /**
     * Check if queue is empty
     */
    public boolean isQueueEmpty() {
        return emailQueue.isEmpty();
    }
    
    /**
     * Get queue capacity (approximate)
     */
    public int getQueueCapacity() {
        return emailQueue.remainingCapacity() + emailQueue.size();
    }
    
    /**
     * Clear all pending tasks
     */
    public void clearQueue() {
        int clearedCount = emailQueue.size();
        emailQueue.clear();
        log.warn("Cleared {} pending email tasks from queue", clearedCount);
    }
    
    /**
     * Get queue statistics
     */
    public String getQueueStats() {
        return String.format("Queue Stats - Size: %d, Capacity: %d, Empty: %s", 
                           getQueueSize(), getQueueCapacity(), isQueueEmpty());
    }
}
