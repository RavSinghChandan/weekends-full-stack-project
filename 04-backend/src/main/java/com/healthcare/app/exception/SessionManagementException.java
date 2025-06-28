package com.healthcare.app.exception;

import java.time.LocalDateTime;

public class SessionManagementException extends RuntimeException {
    
    private Long sessionId;
    private Long userId;
    private String tokenHash;
    private String operation;
    private LocalDateTime sessionExpiry;
    private String reason;
    
    public SessionManagementException(String message) {
        super(message);
    }
    
    public SessionManagementException(String message, Long sessionId, Long userId, String tokenHash, 
                                    String operation, LocalDateTime sessionExpiry, String reason) {
        super(message);
        this.sessionId = sessionId;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.operation = operation;
        this.sessionExpiry = sessionExpiry;
        this.reason = reason;
    }
    
    public SessionManagementException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SessionManagementException(String message, Long userId, String operation, String reason) {
        super(message);
        this.userId = userId;
        this.operation = operation;
        this.reason = reason;
    }
    
    public SessionManagementException(String message, String tokenHash, String operation) {
        super(message);
        this.tokenHash = tokenHash;
        this.operation = operation;
    }
    
    public SessionManagementException(String message, Long sessionId, LocalDateTime sessionExpiry) {
        super(message);
        this.sessionId = sessionId;
        this.sessionExpiry = sessionExpiry;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getTokenHash() {
        return tokenHash;
    }
    
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public LocalDateTime getSessionExpiry() {
        return sessionExpiry;
    }
    
    public void setSessionExpiry(LocalDateTime sessionExpiry) {
        this.sessionExpiry = sessionExpiry;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 