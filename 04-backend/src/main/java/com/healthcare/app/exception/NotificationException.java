package com.healthcare.app.exception;

public class NotificationException extends RuntimeException {
    
    private Long notificationId;
    private Long userId;
    private String notificationType;
    private String operation;
    private String reason;
    
    public NotificationException(String message) {
        super(message);
    }
    
    public NotificationException(String message, Long notificationId, Long userId, 
                               String notificationType, String operation, String reason) {
        super(message);
        this.notificationId = notificationId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.operation = operation;
        this.reason = reason;
    }
    
    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NotificationException(String message, Long userId, String notificationType, String operation) {
        super(message);
        this.userId = userId;
        this.notificationType = notificationType;
        this.operation = operation;
    }
    
    public NotificationException(String message, Long notificationId, String operation) {
        super(message);
        this.notificationId = notificationId;
        this.operation = operation;
    }
    
    public Long getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 