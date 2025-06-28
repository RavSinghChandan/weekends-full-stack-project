package com.healthcare.app.repository;

import com.healthcare.app.entity.AuditLog;
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
    
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<AuditLog> findByAction(String action);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    List<AuditLog> findByResourceTypeAndResourceId(String resourceType, Long resourceId);
    
    List<AuditLog> findByResourceType(String resourceType);
    
    List<AuditLog> findByIpAddress(String ipAddress);
    
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt >= :since ORDER BY al.createdAt DESC")
    List<AuditLog> findByCreatedAtAfter(@Param("since") LocalDateTime since);
    
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt < :before")
    List<AuditLog> findByCreatedAtBefore(@Param("before") LocalDateTime before);
    
    @Query("SELECT al FROM AuditLog al ORDER BY al.createdAt DESC LIMIT :limit")
    List<AuditLog> findRecentLogs(@Param("limit") int limit);
    
    @Query("SELECT al FROM AuditLog al WHERE al.action IN :actions AND al.createdAt >= :since ORDER BY al.createdAt DESC")
    List<AuditLog> findByActionInAndCreatedAtAfter(@Param("actions") List<String> actions, @Param("since") LocalDateTime since);
    
    long countByUserId(Long userId);
    
    long countByAction(String action);
    
    long countByIpAddress(String ipAddress);
    
    long countByCreatedAtAfter(LocalDateTime since);
    
    @Query("DELETE FROM AuditLog al WHERE al.createdAt < :before")
    long deleteByCreatedAtBefore(@Param("before") LocalDateTime before);
} 