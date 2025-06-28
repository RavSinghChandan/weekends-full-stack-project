# Database Models Documentation

## Overview
This document provides detailed information about the Spring Boot JPA entities, their relationships, and business logic for the Fullstack System Design App.

## Entity Models

### 1. User Entity

```java
package com.systemdesign.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"passwordHash", "sessions", "auditLogs"})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 255)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore
    @NotBlank(message = "Password hash is required")
    private String passwordHash;
    
    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Session> sessions = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AuditLog> auditLogs = new ArrayList<>();
    
    // Business Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }
    
    public boolean isModerator() {
        return UserRole.MODERATOR.equals(role) || UserRole.ADMIN.equals(role);
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    public boolean hasActiveSessions() {
        return sessions.stream().anyMatch(Session::getIsActive);
    }
    
    // JSON Properties
    @JsonProperty("fullName")
    public String getFullNameForJson() {
        return getFullName();
    }
    
    @JsonProperty("activeSessionsCount")
    public long getActiveSessionsCount() {
        return sessions.stream().filter(Session::getIsActive).count();
    }
}
```

### 2. UserProfile Entity

```java
package com.systemdesign.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
    
    @Column(length = 20)
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(columnDefinition = "JSON")
    private String address;
    
    @Column(columnDefinition = "JSON")
    private String preferences;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Business Methods
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    public boolean isAdult() {
        return getAge() >= 18;
    }
    
    public String getDisplayName() {
        return user != null ? user.getFullName() : "Unknown User";
    }
    
    public String getDisplayEmail() {
        return user != null ? user.getEmail() : "unknown@email.com";
    }
}
```

### 3. Session Entity

```java
package com.systemdesign.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"user", "tokenHash"})
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Column(name = "token_hash", nullable = false, length = 255)
    @JsonIgnore
    @NotBlank(message = "Token hash is required")
    private String tokenHash;
    
    @Column(name = "expires_at", nullable = false)
    @NotNull(message = "Expiration date is required")
    private LocalDateTime expiresAt;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Business Methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return isActive && !isExpired();
    }
    
    public long getTimeUntilExpiry() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiresAt).getSeconds();
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public String getUserId() {
        return user != null ? user.getId().toString() : null;
    }
    
    public String getUserEmail() {
        return user != null ? user.getEmail() : null;
    }
}
```

### 4. AuditLog Entity

```java
package com.systemdesign.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Action is required")
    private String action;
    
    @Column(name = "resource_type", length = 50)
    private String resourceType;
    
    @Column(name = "resource_id")
    private Long resourceId;
    
    @Column(columnDefinition = "JSON")
    private String details;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Business Methods
    public String getUserId() {
        return user != null ? user.getId().toString() : null;
    }
    
    public String getUserEmail() {
        return user != null ? user.getEmail() : "Anonymous";
    }
    
    public String getUserFullName() {
        return user != null ? user.getFullName() : "Anonymous User";
    }
    
    public boolean isAnonymous() {
        return user == null;
    }
    
    public String getFormattedAction() {
        return action.replace("_", " ").toLowerCase();
    }
    
    public String getResourceIdentifier() {
        if (resourceType != null && resourceId != null) {
            return resourceType + ":" + resourceId;
        }
        return "N/A";
    }
}
```

## Enums

### UserRole Enum

```java
package com.systemdesign.app.entity;

public enum UserRole {
    USER("User"),
    MODERATOR("Moderator"),
    ADMIN("Administrator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean hasPermission(UserRole requiredRole) {
        return this.ordinal() >= requiredRole.ordinal();
    }
    
    public static UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
```

## DTOs (Data Transfer Objects)

### UserDTO

```java
package com.systemdesign.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.systemdesign.app.entity.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private Long activeSessionsCount;
    private UserProfileDTO profile;
    
    // Include profile information
    private String avatarUrl;
    private String bio;
    private String phone;
}
```

### UserProfileDTO

```java
package com.systemdesign.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {
    private Long id;
    private String avatarUrl;
    private String bio;
    private String phone;
    private LocalDate dateOfBirth;
    private Integer age;
    private String address;
    private String preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### SessionDTO

```java
package com.systemdesign.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionDTO {
    private Long id;
    private String userId;
    private String userEmail;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private Boolean isValid;
    private Long timeUntilExpiry;
    private LocalDateTime createdAt;
}
```

### AuditLogDTO

```java
package com.systemdesign.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogDTO {
    private Long id;
    private String userId;
    private String userEmail;
    private String userFullName;
    private String action;
    private String formattedAction;
    private String resourceType;
    private Long resourceId;
    private String resourceIdentifier;
    private String details;
    private String ipAddress;
    private String userAgent;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
}
```

## Repository Interfaces

### UserRepository

```java
package com.systemdesign.app.repository;

import com.systemdesign.app.entity.User;
import com.systemdesign.app.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByEmailVerifiedTrue();
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm%")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
    Page<User> findRecentlyActiveUsers(Pageable pageable);
}
```

### UserProfileRepository

```java
package com.systemdesign.app.repository;

import com.systemdesign.app.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    Optional<UserProfile> findByUserId(Long userId);
    
    @Query("SELECT up FROM UserProfile up WHERE up.user.id = :userId")
    Optional<UserProfile> findByUserIdWithUser(@Param("userId") Long userId);
    
    @Query("SELECT up FROM UserProfile up WHERE up.user.email = :email")
    Optional<UserProfile> findByUserEmail(@Param("email") String email);
}
```

### SessionRepository

```java
package com.systemdesign.app.repository;

import com.systemdesign.app.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    List<Session> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<Session> findByTokenHashAndIsActiveTrue(String tokenHash);
    
    List<Session> findByExpiresAtBefore(LocalDateTime date);
    
    List<Session> findByUserId(Long userId);
    
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.isActive = true AND s.expiresAt > :now")
    List<Session> findActiveSessionsByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("DELETE FROM Session s WHERE s.expiresAt < :date")
    @Modifying
    void deleteExpiredSessions(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = :userId AND s.isActive = true")
    long countActiveSessionsByUserId(@Param("userId") Long userId);
}
```

### AuditLogRepository

```java
package com.systemdesign.app.repository;

import com.systemdesign.app.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByResourceTypeAndResourceId(String resourceType, Long resourceId);
    
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId ORDER BY al.createdAt DESC")
    Page<AuditLog> findByUserIdWithPagination(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.action = :action ORDER BY al.createdAt DESC")
    Page<AuditLog> findByActionWithPagination(@Param("action") String action, Pageable pageable);
    
    @Query("SELECT al FROM AuditLog al WHERE al.ipAddress = :ipAddress ORDER BY al.createdAt DESC")
    List<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress);
    
    @Query("SELECT DISTINCT al.action FROM AuditLog al ORDER BY al.action")
    List<String> findAllDistinctActions();
    
    @Query("SELECT al FROM AuditLog al WHERE al.user IS NULL ORDER BY al.createdAt DESC")
    Page<AuditLog> findAnonymousLogs(Pageable pageable);
}
```

## Entity Lifecycle Events

### User Entity Events

```java
@EntityListeners(UserEntityListener.class)
public class User {
    // ... existing code ...
}

@Component
public class UserEntityListener {
    
    @PrePersist
    public void prePersist(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    @PreUpdate
    public void preUpdate(User user) {
        user.setUpdatedAt(LocalDateTime.now());
    }
}
```

## Validation Annotations

### Custom Validation

```java
@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {
    String message() default "Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        
        // At least 8 characters
        if (password.length() < 8) {
            return false;
        }
        
        // Contains uppercase, lowercase, number, and special character
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        
        return hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar;
    }
}
```

This comprehensive database models documentation provides all the necessary JPA entities, DTOs, repositories, and validation logic for the Spring Boot backend with MySQL database. 