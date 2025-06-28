package com.healthcare.app.service;

import com.healthcare.app.entity.User;
import com.healthcare.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    
    /**
     * Register a new user in the healthcare system
     */
    public User registerUser(User user, String rawPassword) {
        log.info("Registering new user with email: {}", user.getEmail());
        
        // Validate user data
        validateUserRegistration(user);
        
        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        // Hash password
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Set role-specific defaults
        if (user.getRole() == User.UserRole.DOCTOR) {
            user.setIsAvailable(true);
            user.setConsultationFee(100.0); // Default consultation fee
        }
        
        User savedUser = userRepository.save(user);
        
        // Audit the registration
        auditService.logAction("USER_REGISTERED", savedUser.getId(), "USER", savedUser.getId(), 
                              "User registered with role: " + savedUser.getRole());
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    /**
     * Authenticate user login
     */
    public Optional<User> authenticateUser(String email, String rawPassword) {
        log.info("Authenticating user with email: {}", email);
        
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                // Update last login
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                
                // Audit successful login
                auditService.logAction("USER_LOGIN", user.getId(), "USER", user.getId(), 
                                      "Successful login for user: " + user.getEmail());
                
                log.info("User authenticated successfully: {}", user.getEmail());
                return Optional.of(user);
            }
        }
        
        log.warn("Authentication failed for email: {}", email);
        return Optional.empty();
    }
    
    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email);
    }
    
    /**
     * Update user profile
     */
    public User updateUserProfile(Long userId, User updatedUser) {
        log.info("Updating user profile for ID: {}", userId);
        
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Update allowed fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setEmergencyContact(updatedUser.getEmergencyContact());
        existingUser.setBloodType(updatedUser.getBloodType());
        existingUser.setAllergies(updatedUser.getAllergies());
        existingUser.setMedicalConditions(updatedUser.getMedicalConditions());
        existingUser.setInsuranceProvider(updatedUser.getInsuranceProvider());
        existingUser.setInsuranceNumber(updatedUser.getInsuranceNumber());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        // Update role-specific fields
        if (existingUser.getRole() == User.UserRole.DOCTOR) {
            existingUser.setLicenseNumber(updatedUser.getLicenseNumber());
            existingUser.setSpecialization(updatedUser.getSpecialization());
            existingUser.setYearsOfExperience(updatedUser.getYearsOfExperience());
            existingUser.setConsultationFee(updatedUser.getConsultationFee());
            existingUser.setIsAvailable(updatedUser.getIsAvailable());
        } else if (existingUser.getRole() == User.UserRole.PATIENT) {
            existingUser.setEmergencyContactName(updatedUser.getEmergencyContactName());
            existingUser.setEmergencyContactPhone(updatedUser.getEmergencyContactPhone());
            existingUser.setEmergencyContactRelationship(updatedUser.getEmergencyContactRelationship());
        }
        
        User savedUser = userRepository.save(existingUser);
        
        // Audit the update
        auditService.logAction("USER_PROFILE_UPDATED", userId, "USER", userId, 
                              "Profile updated for user: " + savedUser.getEmail());
        
        log.info("User profile updated successfully for ID: {}", userId);
        return savedUser;
    }
    
    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Changing password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password
        validatePassword(newPassword);
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Audit password change
        auditService.logAction("PASSWORD_CHANGED", userId, "USER", userId, 
                              "Password changed for user: " + user.getEmail());
        
        log.info("Password changed successfully for user ID: {}", userId);
    }
    
    /**
     * Get all available doctors
     */
    @Transactional(readOnly = true)
    public List<User> getAvailableDoctors() {
        return userRepository.findAvailableDoctors();
    }
    
    /**
     * Get doctors by specialization
     */
    @Transactional(readOnly = true)
    public List<User> getDoctorsBySpecialization(String specialization) {
        return userRepository.findAvailableDoctorsBySpecialization(specialization);
    }
    
    /**
     * Get active patients with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> getActivePatients(Pageable pageable) {
        return userRepository.findActivePatients(pageable);
    }
    
    /**
     * Get active doctors with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> getActiveDoctors(Pageable pageable) {
        return userRepository.findActiveDoctors(pageable);
    }
    
    /**
     * Search patients
     */
    @Transactional(readOnly = true)
    public Page<User> searchPatients(String searchTerm, Pageable pageable) {
        return userRepository.searchPatients(searchTerm, pageable);
    }
    
    /**
     * Search doctors
     */
    @Transactional(readOnly = true)
    public Page<User> searchDoctors(String searchTerm, Pageable pageable) {
        return userRepository.searchDoctors(searchTerm, pageable);
    }
    
    /**
     * Activate/deactivate user
     */
    public User toggleUserStatus(Long userId, boolean isActive) {
        log.info("Toggling user status for ID: {} to active: {}", userId, isActive);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setIsActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());
        
        // If deactivating a doctor, also set as unavailable
        if (user.getRole() == User.UserRole.DOCTOR && !isActive) {
            user.setIsAvailable(false);
        }
        
        User savedUser = userRepository.save(user);
        
        // Audit the status change
        auditService.logAction("USER_STATUS_CHANGED", userId, "USER", userId, 
                              "User status changed to active: " + isActive);
        
        log.info("User status toggled successfully for ID: {}", userId);
        return savedUser;
    }
    
    /**
     * Verify user email
     */
    public void verifyUserEmail(Long userId) {
        log.info("Verifying email for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Audit email verification
        auditService.logAction("EMAIL_VERIFIED", userId, "USER", userId, 
                              "Email verified for user: " + user.getEmail());
        
        log.info("Email verified successfully for user ID: {}", userId);
    }
    
    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsersByRole(User.UserRole.PATIENT) + 
                          userRepository.countActiveUsersByRole(User.UserRole.DOCTOR) + 
                          userRepository.countActiveUsersByRole(User.UserRole.ADMIN);
        long totalDoctors = userRepository.countActiveUsersByRole(User.UserRole.DOCTOR);
        long totalPatients = userRepository.countActiveUsersByRole(User.UserRole.PATIENT);
        long totalAdmins = userRepository.countActiveUsersByRole(User.UserRole.ADMIN);
        
        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalDoctors(totalDoctors)
                .totalPatients(totalPatients)
                .totalAdmins(totalAdmins)
                .build();
    }
    
    /**
     * Validate user registration data
     */
    private void validateUserRegistration(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role is required");
        }
        
        // Role-specific validations
        if (user.getRole() == User.UserRole.DOCTOR) {
            if (user.getLicenseNumber() == null || user.getLicenseNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("License number is required for doctors");
            }
            if (user.getSpecialization() == null || user.getSpecialization().trim().isEmpty()) {
                throw new IllegalArgumentException("Specialization is required for doctors");
            }
        }
    }
    
    /**
     * Validate password strength
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
    }
    
    /**
     * User statistics DTO
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long totalDoctors;
        private final long totalPatients;
        private final long totalAdmins;
        
        public UserStatistics(long totalUsers, long activeUsers, long totalDoctors, 
                            long totalPatients, long totalAdmins) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.totalDoctors = totalDoctors;
            this.totalPatients = totalPatients;
            this.totalAdmins = totalAdmins;
        }
        
        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getTotalDoctors() { return totalDoctors; }
        public long getTotalPatients() { return totalPatients; }
        public long getTotalAdmins() { return totalAdmins; }
    }
} 