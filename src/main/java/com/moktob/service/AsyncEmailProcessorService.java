package com.moktob.service;

import com.moktob.dto.EmailTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailProcessorService {
    
    private final EmailQueueService emailQueueService;
    private final EmailService emailService;
    
    @Value("${email.async.enabled:true}")
    private boolean asyncEmailEnabled;
    
    @Value("${email.async.thread-pool-size:1}")
    private int threadPoolSize;
    
    @Value("${email.async.poll-interval:1000}")
    private long pollIntervalMs;
    
    @Value("${email.async.max-retries:3}")
    private int maxRetries;
    
    private ExecutorService executorService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    
    @PostConstruct
    public void initialize() {
        if (asyncEmailEnabled) {
            startEmailProcessor();
        } else {
            log.info("Async email processing is disabled");
        }
    }
    
    @PreDestroy
    public void shutdown() {
        stopEmailProcessor();
    }
    
    /**
     * Start the singleton email processor thread
     */
    public synchronized void startEmailProcessor() {
        if (isRunning.get()) {
            log.warn("Email processor is already running");
            return;
        }
        
        if (isShutdown.get()) {
            log.warn("Email processor has been shutdown and cannot be restarted");
            return;
        }
        
        executorService = Executors.newFixedThreadPool(threadPoolSize, r -> {
            Thread thread = new Thread(r, "EmailProcessor-" + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        });
        
        isRunning.set(true);
        
        // Start the main email processing thread
        executorService.submit(this::processEmailQueue);
        
        log.info("Async email processor started with {} thread(s), poll interval: {}ms", 
                threadPoolSize, pollIntervalMs);
    }
    
    /**
     * Stop the email processor
     */
    public synchronized void stopEmailProcessor() {
        if (!isRunning.get()) {
            log.warn("Email processor is not running");
            return;
        }
        
        isRunning.set(false);
        isShutdown.set(true);
        
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    log.warn("Email processor forced to shutdown after 30 seconds");
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        log.info("Async email processor stopped");
    }
    
    /**
     * Main email processing loop
     */
    private void processEmailQueue() {
        log.info("Email processor thread started");
        
        while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
            try {
                EmailTask task = emailQueueService.pollEmailTask();
                
                if (task != null) {
                    processEmailTask(task);
                } else {
                    // Queue is empty, wait before polling again
                    Thread.sleep(pollIntervalMs);
                }
                
            } catch (InterruptedException e) {
                log.info("Email processor thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Unexpected error in email processor thread", e);
                try {
                    Thread.sleep(pollIntervalMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        log.info("Email processor thread stopped");
    }
    
    /**
     * Process individual email task
     */
    private void processEmailTask(EmailTask task) {
        log.info("Processing email task: {} for {}", task.getTaskId(), task.getToEmail());
        
        try {
            boolean success = sendEmailByType(task);
            
            if (success) {
                task.setStatus(EmailTask.EmailStatus.SENT);
                log.info("Email sent successfully: {} to {}", task.getTaskId(), task.getToEmail());
            } else {
                handleEmailFailure(task);
            }
            
        } catch (Exception e) {
            log.error("Error processing email task: {} for {}", task.getTaskId(), task.getToEmail(), e);
            handleEmailFailure(task);
        }
    }
    
    /**
     * Send email based on task type
     */
    private boolean sendEmailByType(EmailTask task) {
        try {
            switch (task.getEmailType()) {
                case STUDENT_CREDENTIALS:
                    emailService.sendStudentCredentialsEmail(
                        task.getToEmail(), 
                        task.getToName(), 
                        task.getUsername(), 
                        task.getTemporaryPassword()
                    );
                    return true;
                    
                case TEACHER_CREDENTIALS:
                    emailService.sendTeacherCredentialsEmail(
                        task.getToEmail(), 
                        task.getToName(), 
                        task.getUsername(), 
                        task.getTemporaryPassword()
                    );
                    return true;
                    
                case WELCOME_EMAIL:
                    emailService.sendWelcomeEmail(
                        task.getToEmail(), 
                        task.getUsername(), 
                        task.getTemporaryPassword()
                    );
                    return true;
                    
                default:
                    log.warn("Unknown email type: {} for task: {}", task.getEmailType(), task.getTaskId());
                    return false;
            }
        } catch (Exception e) {
            log.error("Failed to send email for task: {}", task.getTaskId(), e);
            return false;
        }
    }
    
    /**
     * Handle email sending failure
     */
    private void handleEmailFailure(EmailTask task) {
        task.setRetryCount(task.getRetryCount() + 1);
        
        if (task.getRetryCount() <= maxRetries) {
            task.setStatus(EmailTask.EmailStatus.RETRY);
            log.warn("Email task failed, will retry (attempt {}/{}): {} for {}", 
                    task.getRetryCount(), maxRetries, task.getTaskId(), task.getToEmail());
            
            // Re-queue the task for retry
            emailQueueService.enqueueEmailTask(task);
        } else {
            task.setStatus(EmailTask.EmailStatus.FAILED);
            log.error("Email task failed permanently after {} retries: {} for {}", 
                     maxRetries, task.getTaskId(), task.getToEmail());
        }
    }
    
    /**
     * Submit email task to queue
     */
    public boolean submitEmailTask(EmailTask task) {
        if (!asyncEmailEnabled) {
            log.warn("Async email is disabled, sending synchronously");
            return sendEmailByType(task);
        }
        
        if (!isRunning.get()) {
            log.warn("Email processor is not running, sending synchronously");
            return sendEmailByType(task);
        }
        
        return emailQueueService.enqueueEmailTask(task);
    }
    
    /**
     * Get processor status
     */
    public String getProcessorStatus() {
        return String.format("Email Processor Status - Running: %s, Queue Size: %d, Async Enabled: %s", 
                           isRunning.get(), emailQueueService.getQueueSize(), asyncEmailEnabled);
    }
    
    /**
     * Check if processor is running
     */
    public boolean isProcessorRunning() {
        return isRunning.get();
    }
    
    /**
     * Get queue statistics
     */
    public String getQueueStatistics() {
        return emailQueueService.getQueueStats();
    }
}
