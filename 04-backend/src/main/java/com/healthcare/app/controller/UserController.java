package com.healthcare.app.controller;

import com.healthcare.app.dto.UserRequest;
import com.healthcare.app.dto.UserResponse;
import com.healthcare.app.dto.UserProfileRequest;
import com.healthcare.app.dto.UserProfileResponse;
import com.healthcare.app.dto.UserStatistics;
import com.healthcare.app.entity.User;
import com.healthcare.app.entity.UserProfile;
import com.healthcare.app.service.UserService;
import com.healthcare.app.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Create a new user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request,
            @RequestParam Long createdByUserId) {
        
        logger.info("Creating user by admin: {}", createdByUserId);
        
        try {
            User createdBy = userService.getUserById(createdByUserId);
            if (createdBy == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            User user = convertToEntity(request);
            user.setCreatedBy(createdBy);
            
            User savedUser = userService.createUser(user);
            UserResponse response = convertToResponse(savedUser);
            
            logger.info("User created successfully with ID: {}", savedUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long userId,
            @RequestParam Long requestingUserId) {
        
        logger.info("Fetching user: {} by user: {}", userId, requestingUserId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            // Check if user has access to this user data
            if (!authorizationService.canAccessUserData(requestingUserId, user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
            }

            UserResponse response = convertToResponse(user);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Update user
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request,
            @RequestParam Long updatedByUserId) {
        
        logger.info("Updating user: {} by user: {}", userId, updatedByUserId);
        
        try {
            User updatedUser = convertToEntity(request);
            User savedUser = userService.updateUser(userId, updatedUser, updatedByUserId);
            
            UserResponse response = convertToResponse(savedUser);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            @RequestParam Long deletedByUserId) {
        
        logger.info("Deleting user: {} by admin: {}", userId, deletedByUserId);
        
        try {
            userService.deleteUser(userId, deletedByUserId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Activate user
     */
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(
            @PathVariable Long userId,
            @RequestParam Long activatedByUserId) {
        
        logger.info("Activating user: {} by admin: {}", userId, activatedByUserId);
        
        try {
            User activatedUser = userService.activateUser(userId, activatedByUserId);
            UserResponse response = convertToResponse(activatedUser);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error activating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Deactivate user
     */
    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(
            @PathVariable Long userId,
            @RequestParam Long deactivatedByUserId) {
        
        logger.info("Deactivating user: {} by admin: {}", userId, deactivatedByUserId);
        
        try {
            User deactivatedUser = userService.deactivateUser(userId, deactivatedByUserId);
            UserResponse response = convertToResponse(deactivatedUser);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error deactivating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get users by role
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        
        logger.info("Fetching users with role: {}", role);
        
        try {
            List<User> users = userService.getUsersByRole(role);
            
            List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching users by role: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get active users
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        
        logger.info("Fetching active users");
        
        try {
            List<User> users = userService.getActiveUsers();
            
            List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching active users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get paginated users
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getPaginatedUsers(Pageable pageable) {
        
        logger.info("Fetching paginated users with page: {}, size: {}", 
            pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<User> users = userService.getPaginatedUsers(pageable);
            Page<UserResponse> responses = users.map(this::convertToResponse);
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching paginated users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Search users by keyword
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        
        logger.info("Searching users with keyword: {}", keyword);
        
        try {
            List<User> users = userService.searchUsers(keyword);
            
            List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error searching users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatistics> getUserStatistics() {
        
        logger.info("Fetching user statistics");
        
        try {
            UserStatistics statistics = userService.getUserStatistics();
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error fetching user statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // User Profile endpoints

    /**
     * Create user profile
     */
    @PostMapping("/{userId}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<UserProfileResponse> createUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileRequest request,
            @RequestParam Long createdByUserId) {
        
        logger.info("Creating profile for user: {} by user: {}", userId, createdByUserId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            UserProfile profile = convertToProfileEntity(request);
            profile.setUser(user);
            
            UserProfile savedProfile = userService.createUserProfile(profile);
            UserProfileResponse response = convertToProfileResponse(savedProfile);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get user profile
     */
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @RequestParam Long requestingUserId) {
        
        logger.info("Fetching profile for user: {} by user: {}", userId, requestingUserId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            // Check if user has access to this profile
            if (!authorizationService.canAccessUserData(requestingUserId, user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
            }

            UserProfile profile = userService.getUserProfile(user);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            UserProfileResponse response = convertToProfileResponse(profile);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileRequest request,
            @RequestParam Long updatedByUserId) {
        
        logger.info("Updating profile for user: {} by user: {}", userId, updatedByUserId);
        
        try {
            UserProfile updatedProfile = convertToProfileEntity(request);
            UserProfile savedProfile = userService.updateUserProfile(userId, updatedProfile, updatedByUserId);
            
            UserProfileResponse response = convertToProfileResponse(savedProfile);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Delete user profile
     */
    @DeleteMapping("/{userId}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Void> deleteUserProfile(
            @PathVariable Long userId,
            @RequestParam Long deletedByUserId) {
        
        logger.info("Deleting profile for user: {} by user: {}", userId, deletedByUserId);
        
        try {
            userService.deleteUserProfile(userId, deletedByUserId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting user profile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get all user profiles
     */
    @GetMapping("/profiles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles() {
        
        logger.info("Fetching all user profiles");
        
        try {
            List<UserProfile> profiles = userService.getAllUserProfiles();
            
            List<UserProfileResponse> responses = profiles.stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching all user profiles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Helper methods for conversion
    private User convertToEntity(UserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setIsActive(true);
        return user;
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getCreatedBy() != null) {
            response.setCreatedById(user.getCreatedBy().getId());
        }
        
        return response;
    }

    private UserProfile convertToProfileEntity(UserProfileRequest request) {
        UserProfile profile = new UserProfile();
        profile.setAvatar(request.getAvatar());
        profile.setBio(request.getBio());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setEmergencyPhone(request.getEmergencyPhone());
        profile.setMedicalHistory(request.getMedicalHistory());
        profile.setAllergies(request.getAllergies());
        profile.setCurrentMedications(request.getCurrentMedications());
        profile.setInsuranceProvider(request.getInsuranceProvider());
        profile.setInsuranceNumber(request.getInsuranceNumber());
        profile.setIsActive(true);
        return profile;
    }

    private UserProfileResponse convertToProfileResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setAvatar(profile.getAvatar());
        response.setBio(profile.getBio());
        response.setPhone(profile.getPhone());
        response.setAddress(profile.getAddress());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setGender(profile.getGender());
        response.setEmergencyContact(profile.getEmergencyContact());
        response.setEmergencyPhone(profile.getEmergencyPhone());
        response.setMedicalHistory(profile.getMedicalHistory());
        response.setAllergies(profile.getAllergies());
        response.setCurrentMedications(profile.getCurrentMedications());
        response.setInsuranceProvider(profile.getInsuranceProvider());
        response.setInsuranceNumber(profile.getInsuranceNumber());
        response.setIsActive(profile.getIsActive());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        
        if (profile.getUser() != null) {
            response.setUserId(profile.getUser().getId());
            response.setUserName(profile.getUser().getFirstName() + " " + profile.getUser().getLastName());
        }
        if (profile.getCreatedBy() != null) {
            response.setCreatedById(profile.getCreatedBy().getId());
        }
        
        return response;
    }
} 