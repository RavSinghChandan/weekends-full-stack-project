package com.healthcare.app.exception;

public class AuditLogException extends RuntimeException {
    
    private Long auditLogId;
    private Long userId;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String reason;
    
    public AuditLogException(String message) {
        super(message);
    }
    
    public AuditLogException(String message, Long auditLogId, Long userId, String action, 
                           String resourceType, Long resourceId, String reason) {
        super(message);
        this.auditLogId = auditLogId;
        this.userId = userId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.reason = reason;
    }
    
    public AuditLogException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AuditLogException(String message, Long userId, String action, String resourceType, Long resourceId) {
        super(message);
        this.userId = userId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public AuditLogException(String message, Long auditLogId, String reason) {
        super(message);
        this.auditLogId = auditLogId;
        this.reason = reason;
    }
    
    public Long getAuditLogId() {
        return auditLogId;
    }
    
    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 