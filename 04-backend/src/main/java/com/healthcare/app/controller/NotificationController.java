package com.healthcare.app.controller;

import com.healthcare.app.dto.NotificationRequest;
import com.healthcare.app.dto.NotificationResponse;
import com.healthcare.app.dto.NotificationStatistics;
import com.healthcare.app.entity.Notification;
import com.healthcare.app.entity.User;
import com.healthcare.app.service.NotificationService;
import com.healthcare.app.service.AuthorizationService;
import com.healthcare.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Create a new notification
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request,
            @RequestParam Long userId) {
        
        logger.info("Creating notification for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Notification notification = convertToEntity(request);
            notification.setCreatedBy(user);
            
            Notification savedNotification = notificationService.createNotification(notification);
            NotificationResponse response = convertToResponse(savedNotification);
            
            logger.info("Notification created successfully with ID: {}", savedNotification.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Create appointment reminder notification
     */
    @PostMapping("/appointment-reminder")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<NotificationResponse> createAppointmentReminder(
            @RequestParam Long userId,
            @RequestParam String appointmentDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentTime) {
        
        logger.info("Creating appointment reminder for user: {} with appointment: {}", userId, appointmentDetails);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Notification notification = notificationService.createAppointmentReminder(
                user, appointmentDetails, appointmentTime);
            NotificationResponse response = convertToResponse(notification);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating appointment reminder: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Create appointment confirmation notification
     */
    @PostMapping("/appointment-confirmation")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<NotificationResponse> createAppointmentConfirmation(
            @RequestParam Long userId,
            @RequestParam String appointmentDetails) {
        
        logger.info("Creating appointment confirmation for user: {} with appointment: {}", userId, appointmentDetails);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Notification notification = notificationService.createAppointmentConfirmation(
                user, appointmentDetails);
            NotificationResponse response = convertToResponse(notification);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating appointment confirmation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Create appointment cancellation notification
     */
    @PostMapping("/appointment-cancellation")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<NotificationResponse> createAppointmentCancellation(
            @RequestParam Long userId,
            @RequestParam String appointmentDetails,
            @RequestParam String reason) {
        
        logger.info("Creating appointment cancellation for user: {} with appointment: {} and reason: {}", 
            userId, appointmentDetails, reason);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Notification notification = notificationService.createAppointmentCancellation(
                user, appointmentDetails, reason);
            NotificationResponse response = convertToResponse(notification);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating appointment cancellation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Create EMR update notification
     */
    @PostMapping("/emr-update")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<NotificationResponse> createEMRUpdateNotification(
            @RequestParam Long userId,
            @RequestParam String emrDetails) {
        
        logger.info("Creating EMR update notification for user: {} with EMR: {}", userId, emrDetails);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Notification notification = notificationService.createEMRUpdateNotification(
                user, emrDetails);
            NotificationResponse response = convertToResponse(notification);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating EMR update notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Create system notification
     */
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponse> createSystemNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam Notification.NotificationPriority priority) {
        
        logger.info("Creating system notification for user: {} with title: {}", userId, title);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Notification notification = notificationService.createSystemNotification(
                user, title, message, priority);
            NotificationResponse response = convertToResponse(notification);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating system notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<NotificationResponse> getNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        
        logger.info("Fetching notification: {} for user: {}", notificationId, userId);
        
        try {
            Notification notification = notificationService.getNotificationById(notificationId);
            if (notification == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            // Check if user has access to this notification
            if (!authorizationService.canAccessNotification(userId, notification)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
            }

            NotificationResponse response = convertToResponse(notification);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        
        logger.info("Marking notification: {} as read by user: {}", notificationId, userId);
        
        try {
            Notification notification = notificationService.markAsRead(notificationId, userId);
            NotificationResponse response = convertToResponse(notification);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error marking notification as read: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    @PostMapping("/mark-all-read")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Void> markAllAsRead(@RequestParam Long userId) {
        
        logger.info("Marking all notifications as read for user: {}", userId);
        
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error marking all notifications as read: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        
        logger.info("Deleting notification: {} by user: {}", notificationId, userId);
        
        try {
            notificationService.deleteNotification(notificationId, userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get notifications by user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) Boolean unreadOnly) {
        
        logger.info("Fetching notifications for user: {} (unread only: {})", userId, unreadOnly);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Notification> notifications;
            if (unreadOnly != null && unreadOnly) {
                notifications = notificationService.getUnreadNotificationsByUser(user);
            } else {
                notifications = notificationService.getNotificationsByUser(user);
            }

            List<NotificationResponse> responses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching notifications by user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get notifications by type
     */
    @GetMapping("/user/{userId}/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable Long userId,
            @PathVariable Notification.NotificationType type) {
        
        logger.info("Fetching notifications of type: {} for user: {}", type, userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Notification> notifications = notificationService.getNotificationsByType(user, type);
            
            List<NotificationResponse> responses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching notifications by type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get notifications by priority
     */
    @GetMapping("/user/{userId}/priority/{priority}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByPriority(
            @PathVariable Long userId,
            @PathVariable Notification.NotificationPriority priority) {
        
        logger.info("Fetching notifications of priority: {} for user: {}", priority, userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Notification> notifications = notificationService.getNotificationsByPriority(user, priority);
            
            List<NotificationResponse> responses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching notifications by priority: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get notifications by date range
     */
    @GetMapping("/user/{userId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Fetching notifications between {} and {} for user: {}", startDate, endDate, userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Notification> notifications = notificationService.getNotificationsByDateRange(user, startDate, endDate);
            
            List<NotificationResponse> responses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching notifications by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get paginated notifications
     */
    @GetMapping("/user/{userId}/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Page<NotificationResponse>> getPaginatedNotifications(
            @PathVariable Long userId,
            Pageable pageable) {
        
        logger.info("Fetching paginated notifications for user: {} with page: {}, size: {}", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Page<Notification> notifications = notificationService.getPaginatedNotifications(user, pageable);
            Page<NotificationResponse> responses = notifications.map(this::convertToResponse);
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching paginated notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get notification count for user
     */
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Long> getNotificationCount(@PathVariable Long userId) {
        
        logger.info("Fetching notification count for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Long count = notificationService.getNotificationCount(user);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            logger.error("Error fetching notification count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get unread notification count for user
     */
    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Long userId) {
        
        logger.info("Fetching unread notification count for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Long count = notificationService.getUnreadNotificationCount(user);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            logger.error("Error fetching unread notification count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get high priority notification count for user
     */
    @GetMapping("/user/{userId}/high-priority-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Long> getHighPriorityNotificationCount(@PathVariable Long userId) {
        
        logger.info("Fetching high priority notification count for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Long count = notificationService.getHighPriorityNotificationCount(user);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            logger.error("Error fetching high priority notification count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get recent notifications
     */
    @GetMapping("/user/{userId}/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<NotificationResponse>> getRecentNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Fetching recent notifications for user: {} with limit: {}", userId, limit);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Notification> notifications = notificationService.getRecentNotifications(user, limit);
            
            List<NotificationResponse> responses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching recent notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get notification statistics
     */
    @GetMapping("/user/{userId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<NotificationStatistics> getNotificationStatistics(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Fetching notification statistics for user: {} from {} to {}", userId, startDate, endDate);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            NotificationStatistics statistics = notificationService.getNotificationStatistics(user, startDate, endDate);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error fetching notification statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Helper methods for conversion
    private Notification convertToEntity(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setPriority(request.getPriority());
        notification.setIsRead(false);
        
        if (request.getUserId() != null) {
            notification.setUser(userService.getUserById(request.getUserId()));
        }
        
        return notification;
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setPriority(notification.getPriority());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());
        response.setUpdatedAt(notification.getUpdatedAt());
        
        if (notification.getUser() != null) {
            response.setUserId(notification.getUser().getId());
            response.setUserName(notification.getUser().getFirstName() + " " + notification.getUser().getLastName());
        }
        if (notification.getCreatedBy() != null) {
            response.setCreatedById(notification.getCreatedBy().getId());
        }
        
        return response;
    }
} 