package com.healthcare.app.service;

import com.healthcare.app.entity.Notification;
import com.healthcare.app.entity.User;
import com.healthcare.app.repository.NotificationRepository;
import com.healthcare.app.repository.UserRepository;
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
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    
    /**
     * Create a new notification
     */
    public Notification createNotification(Notification notification) {
        log.info("Creating notification for user: {} with type: {}", 
                notification.getUser().getId(), notification.getType());
        
        // Validate notification data
        validateNotificationData(notification);
        
        // Check if user exists and is active
        User user = userRepository.findById(notification.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("User is not active");
        }
        
        // Set default values
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Audit the notification creation
        auditService.logAction("NOTIFICATION_CREATED", user.getId(), "NOTIFICATION", savedNotification.getId(), 
                              "Notification created: " + notification.getType());
        
        log.info("Notification created successfully with ID: {}", savedNotification.getId());
        return savedNotification;
    }
    
    /**
     * Create appointment reminder notification
     */
    public Notification createAppointmentReminder(User user, String appointmentDetails, LocalDateTime appointmentTime) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.APPOINTMENT_REMINDER);
        notification.setTitle("Appointment Reminder");
        notification.setMessage("You have an upcoming appointment: " + appointmentDetails + " at " + appointmentTime);
        notification.setPriority(Notification.NotificationPriority.MEDIUM);
        
        return createNotification(notification);
    }
    
    /**
     * Create appointment confirmation notification
     */
    public Notification createAppointmentConfirmation(User user, String appointmentDetails) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.APPOINTMENT_CONFIRMED);
        notification.setTitle("Appointment Confirmed");
        notification.setMessage("Your appointment has been confirmed: " + appointmentDetails);
        notification.setPriority(Notification.NotificationPriority.LOW);
        
        return createNotification(notification);
    }
    
    /**
     * Create appointment cancellation notification
     */
    public Notification createAppointmentCancellation(User user, String appointmentDetails, String reason) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.APPOINTMENT_CANCELLED);
        notification.setTitle("Appointment Cancelled");
        notification.setMessage("Your appointment has been cancelled: " + appointmentDetails + ". Reason: " + reason);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        
        return createNotification(notification);
    }
    
    /**
     * Create EMR update notification
     */
    public Notification createEMRUpdateNotification(User user, String emrDetails) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.EMR_UPDATED);
        notification.setTitle("Medical Record Updated");
        notification.setMessage("Your medical record has been updated: " + emrDetails);
        notification.setPriority(Notification.NotificationPriority.MEDIUM);
        
        return createNotification(notification);
    }
    
    /**
     * Create system notification
     */
    public Notification createSystemNotification(User user, String title, String message, Notification.NotificationPriority priority) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.SYSTEM);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(priority);
        
        return createNotification(notification);
    }
    
    /**
     * Mark notification as read
     */
    public Notification markAsRead(Long notificationId, Long userId) {
        log.info("Marking notification ID: {} as read by user: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));
        
        // Check if user has permission to mark this notification as read
        validateNotificationAccess(notification, userId);
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Audit the notification read
        auditService.logAction("NOTIFICATION_READ", userId, "NOTIFICATION", notificationId, 
                              "Notification marked as read");
        
        log.info("Notification marked as read successfully ID: {}", notificationId);
        return savedNotification;
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
        }
        
        notificationRepository.saveAll(unreadNotifications);
        
        // Audit the bulk read action
        auditService.logAction("NOTIFICATIONS_BULK_READ", userId, "NOTIFICATION", null, 
                              "All notifications marked as read");
        
        log.info("All notifications marked as read for user: {}", userId);
    }
    
    /**
     * Delete notification
     */
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("Deleting notification ID: {} by user: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));
        
        // Check if user has permission to delete this notification
        validateNotificationAccess(notification, userId);
        
        notificationRepository.delete(notification);
        
        // Audit the notification deletion
        auditService.logAction("NOTIFICATION_DELETED", userId, "NOTIFICATION", notificationId, 
                              "Notification deleted");
        
        log.info("Notification deleted successfully ID: {}", notificationId);
    }
    
    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public Optional<Notification> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }
    
    /**
     * Get notifications by user
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Get unread notifications by user
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    /**
     * Get notifications by type
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(User user, Notification.NotificationType type) {
        return notificationRepository.findByUserAndTypeOrderByCreatedAtDesc(user, type);
    }
    
    /**
     * Get notifications by priority
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByPriority(User user, Notification.NotificationPriority priority) {
        return notificationRepository.findByUserAndPriorityOrderByCreatedAtDesc(user, priority);
    }
    
    /**
     * Get notifications by date range
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, startDate, endDate);
    }
    
    /**
     * Get paginated notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getPaginatedNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    /**
     * Get notification count for user
     */
    @Transactional(readOnly = true)
    public long getNotificationCount(User user) {
        return notificationRepository.countByUser(user);
    }
    
    /**
     * Get unread notification count for user
     */
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
    
    /**
     * Get high priority notification count for user
     */
    @Transactional(readOnly = true)
    public long getHighPriorityNotificationCount(User user) {
        return notificationRepository.countByUserAndPriority(user, Notification.NotificationPriority.HIGH);
    }
    
    /**
     * Get notifications by type and date range
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByTypeAndDateRange(User user, Notification.NotificationType type, 
                                                                LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByUserAndTypeAndCreatedAtBetweenOrderByCreatedAtDesc(user, type, startDate, endDate);
    }
    
    /**
     * Get recent notifications
     */
    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications(User user, int limit) {
        return notificationRepository.findRecentNotificationsByUser(user, limit);
    }
    
    /**
     * Get notification statistics
     */
    @Transactional(readOnly = true)
    public NotificationStatistics getNotificationStatistics(User user, LocalDateTime startDate, LocalDateTime endDate) {
        long totalNotifications = notificationRepository.countByUserAndCreatedAtBetween(user, startDate, endDate);
        long readNotifications = notificationRepository.countByUserAndIsReadTrueAndCreatedAtBetween(user, startDate, endDate);
        long unreadNotifications = notificationRepository.countByUserAndIsReadFalseAndCreatedAtBetween(user, startDate, endDate);
        long highPriorityNotifications = notificationRepository.countByUserAndPriorityAndCreatedAtBetween(user, Notification.NotificationPriority.HIGH, startDate, endDate);
        
        return NotificationStatistics.builder()
                .totalNotifications(totalNotifications)
                .readNotifications(readNotifications)
                .unreadNotifications(unreadNotifications)
                .highPriorityNotifications(highPriorityNotifications)
                .build();
    }
    
    /**
     * Clean up old notifications
     */
    public void cleanupOldNotifications(int daysToKeep) {
        log.info("Cleaning up notifications older than {} days", daysToKeep);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(cutoffDate);
        
        notificationRepository.deleteAll(oldNotifications);
        
        log.info("Cleaned up {} old notifications", oldNotifications.size());
    }
    
    /**
     * Validate notification data
     */
    private void validateNotificationData(Notification notification) {
        if (notification.getUser() == null || notification.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (notification.getType() == null) {
            throw new IllegalArgumentException("Notification type is required");
        }
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification title is required");
        }
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message is required");
        }
        if (notification.getPriority() == null) {
            throw new IllegalArgumentException("Notification priority is required");
        }
    }
    
    /**
     * Validate notification access permissions
     */
    private void validateNotificationAccess(Notification notification, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Admin can access all notifications
        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }
        
        // User can only access their own notifications
        if (notification.getUser().getId().equals(userId)) {
            return;
        }
        
        throw new IllegalArgumentException("User does not have permission to access this notification");
    }
    
    /**
     * Notification statistics DTO
     */
    public static class NotificationStatistics {
        private final long totalNotifications;
        private final long readNotifications;
        private final long unreadNotifications;
        private final long highPriorityNotifications;
        
        public NotificationStatistics(long totalNotifications, long readNotifications, 
                                    long unreadNotifications, long highPriorityNotifications) {
            this.totalNotifications = totalNotifications;
            this.readNotifications = readNotifications;
            this.unreadNotifications = unreadNotifications;
            this.highPriorityNotifications = highPriorityNotifications;
        }
        
        // Getters
        public long getTotalNotifications() { return totalNotifications; }
        public long getReadNotifications() { return readNotifications; }
        public long getUnreadNotifications() { return unreadNotifications; }
        public long getHighPriorityNotifications() { return highPriorityNotifications; }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private long totalNotifications;
            private long readNotifications;
            private long unreadNotifications;
            private long highPriorityNotifications;
            
            public Builder totalNotifications(long totalNotifications) {
                this.totalNotifications = totalNotifications;
                return this;
            }
            
            public Builder readNotifications(long readNotifications) {
                this.readNotifications = readNotifications;
                return this;
            }
            
            public Builder unreadNotifications(long unreadNotifications) {
                this.unreadNotifications = unreadNotifications;
                return this;
            }
            
            public Builder highPriorityNotifications(long highPriorityNotifications) {
                this.highPriorityNotifications = highPriorityNotifications;
                return this;
            }
            
            public NotificationStatistics build() {
                return new NotificationStatistics(totalNotifications, readNotifications,
                                                unreadNotifications, highPriorityNotifications);
            }
        }
    }
} 