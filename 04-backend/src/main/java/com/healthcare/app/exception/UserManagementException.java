package com.healthcare.app.exception;

public class UserManagementException extends RuntimeException {
    
    private Long userId;
    private String username;
    private String email;
    private String operation;
    private String reason;
    
    public UserManagementException(String message) {
        super(message);
    }
    
    public UserManagementException(String message, Long userId, String operation, String reason) {
        super(message);
        this.userId = userId;
        this.operation = operation;
        this.reason = reason;
    }
    
    public UserManagementException(String message, String username, String email, String operation) {
        super(message);
        this.username = username;
        this.email = email;
        this.operation = operation;
    }
    
    public UserManagementException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UserManagementException(String message, Long userId, String operation) {
        super(message);
        this.userId = userId;
        this.operation = operation;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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