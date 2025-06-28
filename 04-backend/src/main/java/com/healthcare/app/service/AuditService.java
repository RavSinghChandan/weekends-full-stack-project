package com.healthcare.app.service;

import com.healthcare.app.entity.AuditLog;
import com.healthcare.app.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * Log an action in the audit trail
     */
    public void logAction(String action, Long userId, String resourceType, Long resourceId, String details) {
        logAction(action, userId, resourceType, resourceId, details, null, null);
    }
    
    /**
     * Log an action with IP address and user agent
     */
    public void logAction(String action, Long userId, String resourceType, Long resourceId, 
                         String details, String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .details(details)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", action, resourceType, resourceId);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
            // Don't throw exception to avoid breaking main functionality
        }
    }
    
    /**
     * Get audit logs by user ID
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByUserId(Long userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get audit logs by action
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }
    
    /**
     * Get audit logs by resource type and ID
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByResource(String resourceType, Long resourceId) {
        return auditLogRepository.findByResourceTypeAndResourceId(resourceType, resourceId);
    }
    
    /**
     * Get audit logs by date range
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get audit logs by IP address
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByIpAddress(String ipAddress) {
        return auditLogRepository.findByIpAddress(ipAddress);
    }
    
    /**
     * Get paginated audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    /**
     * Get paginated audit logs by user ID
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * Get paginated audit logs by action
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }
    
    /**
     * Get recent audit logs
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs(int limit) {
        return auditLogRepository.findRecentLogs(limit);
    }
    
    /**
     * Get audit logs by resource type
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByResourceType(String resourceType) {
        return auditLogRepository.findByResourceType(resourceType);
    }
    
    /**
     * Count audit logs by user ID
     */
    @Transactional(readOnly = true)
    public long countAuditLogsByUserId(Long userId) {
        return auditLogRepository.countByUserId(userId);
    }
    
    /**
     * Count audit logs by action
     */
    @Transactional(readOnly = true)
    public long countAuditLogsByAction(String action) {
        return auditLogRepository.countByAction(action);
    }
    
    /**
     * Count audit logs by IP address
     */
    @Transactional(readOnly = true)
    public long countAuditLogsByIpAddress(String ipAddress) {
        return auditLogRepository.countByIpAddress(ipAddress);
    }
    
    /**
     * Get audit log by ID
     */
    @Transactional(readOnly = true)
    public Optional<AuditLog> getAuditLogById(Long id) {
        return auditLogRepository.findById(id);
    }
    
    /**
     * Clean up old audit logs (older than specified days)
     */
    public void cleanupOldAuditLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        log.info("Cleaning up audit logs older than {} days", daysToKeep);
        
        try {
            long deletedCount = auditLogRepository.deleteByCreatedAtBefore(cutoffDate);
            log.info("Deleted {} old audit logs", deletedCount);
        } catch (Exception e) {
            log.error("Failed to cleanup old audit logs: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get audit statistics
     */
    @Transactional(readOnly = true)
    public AuditStatistics getAuditStatistics() {
        long totalLogs = auditLogRepository.count();
        long todayLogs = auditLogRepository.countByCreatedAtAfter(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));
        long thisWeekLogs = auditLogRepository.countByCreatedAtAfter(LocalDateTime.now().minusWeeks(1));
        long thisMonthLogs = auditLogRepository.countByCreatedAtAfter(LocalDateTime.now().minusMonths(1));
        
        return AuditStatistics.builder()
                .totalLogs(totalLogs)
                .todayLogs(todayLogs)
                .thisWeekLogs(thisWeekLogs)
                .thisMonthLogs(thisMonthLogs)
                .build();
    }
    
    /**
     * Get audit logs for security analysis
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getSecurityAuditLogs(LocalDateTime since) {
        return auditLogRepository.findByActionInAndCreatedAtAfter(
                List.of("USER_LOGIN", "USER_LOGIN_FAILED", "PASSWORD_CHANGED", "USER_STATUS_CHANGED"), 
                since);
    }
    
    /**
     * Get audit logs for healthcare compliance
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getComplianceAuditLogs(LocalDateTime since) {
        return auditLogRepository.findByActionInAndCreatedAtAfter(
                List.of("EMR_CREATED", "EMR_UPDATED", "EMR_VIEWED", "PRESCRIPTION_CREATED", 
                       "PRESCRIPTION_UPDATED", "APPOINTMENT_CREATED", "APPOINTMENT_UPDATED"), 
                since);
    }
    
    /**
     * Audit statistics DTO
     */
    public static class AuditStatistics {
        private final long totalLogs;
        private final long todayLogs;
        private final long thisWeekLogs;
        private final long thisMonthLogs;
        
        public AuditStatistics(long totalLogs, long todayLogs, long thisWeekLogs, long thisMonthLogs) {
            this.totalLogs = totalLogs;
            this.todayLogs = todayLogs;
            this.thisWeekLogs = thisWeekLogs;
            this.thisMonthLogs = thisMonthLogs;
        }
        
        // Getters
        public long getTotalLogs() { return totalLogs; }
        public long getTodayLogs() { return todayLogs; }
        public long getThisWeekLogs() { return thisWeekLogs; }
        public long getThisMonthLogs() { return thisMonthLogs; }
    }
} 