package com.healthcare.app.controller;

import com.healthcare.app.dto.DoctorAvailabilityRequest;
import com.healthcare.app.dto.DoctorAvailabilityResponse;
import com.healthcare.app.dto.AvailabilityStatistics;
import com.healthcare.app.entity.DoctorAvailability;
import com.healthcare.app.entity.User;
import com.healthcare.app.service.DoctorAvailabilityService;
import com.healthcare.app.service.AuthorizationService;
import com.healthcare.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor-availability")
@CrossOrigin(origins = "*")
public class DoctorAvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAvailabilityController.class);

    @Autowired
    private DoctorAvailabilityService availabilityService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Set doctor availability
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorAvailabilityResponse> setAvailability(
            @Valid @RequestBody DoctorAvailabilityRequest request,
            @RequestParam Long userId) {
        
        logger.info("Setting availability for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            DoctorAvailability availability = convertToEntity(request);
            availability.setDoctor(user);
            
            DoctorAvailability savedAvailability = availabilityService.setAvailability(availability);
            DoctorAvailabilityResponse response = convertToResponse(savedAvailability);
            
            logger.info("Availability set successfully with ID: {}", savedAvailability.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error setting availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Update doctor availability
     */
    @PutMapping("/{availabilityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorAvailabilityResponse> updateAvailability(
            @PathVariable Long availabilityId,
            @Valid @RequestBody DoctorAvailabilityRequest request,
            @RequestParam Long userId) {
        
        logger.info("Updating availability: {} by user: {}", availabilityId, userId);
        
        try {
            DoctorAvailability updatedAvailability = convertToEntity(request);
            DoctorAvailability savedAvailability = availabilityService.updateAvailability(
                availabilityId, updatedAvailability, userId);
            
            DoctorAvailabilityResponse response = convertToResponse(savedAvailability);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Delete doctor availability
     */
    @DeleteMapping("/{availabilityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long availabilityId,
            @RequestParam Long userId) {
        
        logger.info("Deleting availability: {} by user: {}", availabilityId, userId);
        
        try {
            availabilityService.deleteAvailability(availabilityId, userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get availability by ID
     */
    @GetMapping("/{availabilityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorAvailabilityResponse> getAvailability(
            @PathVariable Long availabilityId) {
        
        logger.info("Fetching availability: {}", availabilityId);
        
        try {
            DoctorAvailability availability = availabilityService.getAvailabilityById(availabilityId);
            if (availability == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            DoctorAvailabilityResponse response = convertToResponse(availability);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get doctor availability
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getDoctorAvailability(
            @PathVariable Long doctorId) {
        
        logger.info("Fetching availability for doctor: {}", doctorId);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<DoctorAvailability> availabilities = availabilityService.getDoctorAvailability(doctor);
            
            List<DoctorAvailabilityResponse> responses = availabilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching doctor availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get availability by day
     */
    @GetMapping("/day/{dayOfWeek}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAvailabilityByDay(
            @PathVariable DayOfWeek dayOfWeek) {
        
        logger.info("Fetching availability for day: {}", dayOfWeek);
        
        try {
            List<DoctorAvailability> availabilities = availabilityService.getAvailabilityByDay(dayOfWeek);
            
            List<DoctorAvailabilityResponse> responses = availabilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching availability by day: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get available doctors at specific time
     */
    @GetMapping("/available-doctors")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAvailableDoctors(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        
        logger.info("Fetching available doctors at: {}", dateTime);
        
        try {
            List<DoctorAvailability> availabilities = availabilityService.getAvailableDoctors(dateTime);
            
            List<DoctorAvailabilityResponse> responses = availabilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching available doctors: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Check if doctor is available at specific time
     */
    @GetMapping("/doctor/{doctorId}/check-availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Boolean> isDoctorAvailableAtTime(
            @PathVariable Long doctorId,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        
        logger.info("Checking availability for doctor: {} on {} at {}", doctorId, dayOfWeek, time);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            boolean isAvailable = availabilityService.isDoctorAvailableAtTime(doctor, dayOfWeek, time);
            return ResponseEntity.ok(isAvailable);
            
        } catch (Exception e) {
            logger.error("Error checking doctor availability: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get doctor availability for date range
     */
    @GetMapping("/doctor/{doctorId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getDoctorAvailabilityForDateRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching availability for doctor: {} from {} to {}", doctorId, startDate, endDate);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<DoctorAvailability> availabilities = availabilityService.getDoctorAvailabilityForDateRange(
                doctor, startDate, endDate);
            
            List<DoctorAvailabilityResponse> responses = availabilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching doctor availability for date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get all active availabilities
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DoctorAvailabilityResponse>> getAllActiveAvailabilities() {
        
        logger.info("Fetching all active availabilities");
        
        try {
            List<DoctorAvailability> availabilities = availabilityService.getAllActiveAvailabilities();
            
            List<DoctorAvailabilityResponse> responses = availabilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching all active availabilities: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get availability by doctor and day
     */
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorAvailabilityResponse> getAvailabilityByDoctorAndDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek) {
        
        logger.info("Fetching availability for doctor: {} on day: {}", doctorId, dayOfWeek);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            DoctorAvailability availability = availabilityService.getAvailabilityByDoctorAndDay(doctor, dayOfWeek);
            if (availability == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            DoctorAvailabilityResponse response = convertToResponse(availability);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching availability by doctor and day: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get availability statistics for doctor
     */
    @GetMapping("/doctor/{doctorId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AvailabilityStatistics> getAvailabilityStatistics(
            @PathVariable Long doctorId) {
        
        logger.info("Fetching availability statistics for doctor: {}", doctorId);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            AvailabilityStatistics statistics = availabilityService.getAvailabilityStatistics(doctor);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error fetching availability statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Set doctor as unavailable for specific period
     */
    @PostMapping("/doctor/{doctorId}/unavailable")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> setDoctorUnavailable(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam String reason,
            @RequestParam Long userId) {
        
        logger.info("Setting doctor: {} as unavailable from {} to {} with reason: {}", 
            doctorId, startDateTime, endDateTime, reason);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
            }

            availabilityService.setDoctorUnavailable(doctor, startDateTime, endDateTime, reason);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error setting doctor as unavailable: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get next available slot for doctor
     */
    @GetMapping("/doctor/{doctorId}/next-available-slot")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<LocalDateTime> getNextAvailableSlot(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDateTime) {
        
        logger.info("Finding next available slot for doctor: {} from: {}", doctorId, fromDateTime);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            LocalDateTime nextSlot = availabilityService.getNextAvailableSlot(doctor, fromDateTime);
            return ResponseEntity.ok(nextSlot);
            
        } catch (Exception e) {
            logger.error("Error finding next available slot: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Helper methods for conversion
    private DoctorAvailability convertToEntity(DoctorAvailabilityRequest request) {
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setIsActive(true);
        return availability;
    }

    private DoctorAvailabilityResponse convertToResponse(DoctorAvailability availability) {
        DoctorAvailabilityResponse response = new DoctorAvailabilityResponse();
        response.setId(availability.getId());
        response.setDayOfWeek(availability.getDayOfWeek());
        response.setStartTime(availability.getStartTime());
        response.setEndTime(availability.getEndTime());
        response.setIsActive(availability.getIsActive());
        response.setCreatedAt(availability.getCreatedAt());
        response.setUpdatedAt(availability.getUpdatedAt());
        
        if (availability.getDoctor() != null) {
            response.setDoctorId(availability.getDoctor().getId());
            response.setDoctorName(availability.getDoctor().getFirstName() + " " + availability.getDoctor().getLastName());
        }
        if (availability.getCreatedBy() != null) {
            response.setCreatedById(availability.getCreatedBy().getId());
        }
        
        return response;
    }
} 