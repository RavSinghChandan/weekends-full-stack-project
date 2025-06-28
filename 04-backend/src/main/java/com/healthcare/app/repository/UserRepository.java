package com.healthcare.app.repository;

import com.healthcare.app.entity.User;
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
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByRoleAndIsActiveTrue(User.UserRole role);
    
    List<User> findBySpecialization(String specialization);
    
    List<User> findBySpecializationAndIsAvailableTrue(String specialization);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.isAvailable = true AND u.isActive = true")
    List<User> findAvailableDoctors();
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.specialization = :specialization AND u.isAvailable = true AND u.isActive = true")
    List<User> findAvailableDoctorsBySpecialization(@Param("specialization") String specialization);
    
    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT' AND u.isActive = true")
    Page<User> findActivePatients(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.isActive = true")
    Page<User> findActiveDoctors(Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveUsersByRole(@Param("role") User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date AND u.isActive = true")
    List<User> findInactiveUsersSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.licenseNumber = :licenseNumber")
    Optional<User> findByLicenseNumber(@Param("licenseNumber") String licenseNumber);
    
    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT' AND u.insuranceNumber = :insuranceNumber")
    Optional<User> findByInsuranceNumber(@Param("insuranceNumber") String insuranceNumber);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.specialization LIKE %:specialization% AND u.isActive = true")
    List<User> findDoctorsBySpecializationContaining(@Param("specialization") String specialization);
    
    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT' AND (u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%) AND u.isActive = true")
    Page<User> searchPatients(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND (u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.specialization LIKE %:searchTerm%) AND u.isActive = true")
    Page<User> searchDoctors(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndRole(String email, User.UserRole role);
} 