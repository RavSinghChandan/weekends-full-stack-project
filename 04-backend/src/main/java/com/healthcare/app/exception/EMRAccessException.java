package com.healthcare.app.exception;

public class EMRAccessException extends RuntimeException {
    
    private Long emrId;
    private Long userId;
    private String userRole;
    private String accessType;
    private String reason;
    
    public EMRAccessException(String message) {
        super(message);
    }
    
    public EMRAccessException(String message, Long emrId, Long userId, String userRole, 
                            String accessType, String reason) {
        super(message);
        this.emrId = emrId;
        this.userId = userId;
        this.userRole = userRole;
        this.accessType = accessType;
        this.reason = reason;
    }
    
    public EMRAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EMRAccessException(String message, Long emrId, Long userId, String reason) {
        super(message);
        this.emrId = emrId;
        this.userId = userId;
        this.reason = reason;
    }
    
    public Long getEmrId() {
        return emrId;
    }
    
    public void setEmrId(Long emrId) {
        this.emrId = emrId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public String getAccessType() {
        return accessType;
    }
    
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 