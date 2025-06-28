package com.healthcare.app.service;

import com.healthcare.app.entity.DoctorAvailability;
import com.healthcare.app.entity.User;
import com.healthcare.app.repository.DoctorAvailabilityRepository;
import com.healthcare.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorAvailabilityService {
    
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    
    /**
     * Set doctor availability for a specific day and time slot
     */
    public DoctorAvailability setAvailability(DoctorAvailability availability) {
        log.info("Setting availability for doctor: {} on day: {} from {} to {}", 
                availability.getDoctor().getId(), availability.getDayOfWeek(), 
                availability.getStartTime(), availability.getEndTime());
        
        // Validate availability data
        validateAvailabilityData(availability);
        
        // Check if doctor exists and is active
        User doctor = userRepository.findById(availability.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }
        
        if (!doctor.getIsActive()) {
            throw new IllegalArgumentException("Doctor is not active");
        }
        
        // Check for overlapping availability
        validateNoOverlappingAvailability(availability);
        
        // Set default values
        availability.setCreatedAt(LocalDateTime.now());
        availability.setUpdatedAt(LocalDateTime.now());
        
        DoctorAvailability savedAvailability = doctorAvailabilityRepository.save(availability);
        
        // Audit the availability setting
        auditService.logAction("AVAILABILITY_SET", doctor.getId(), "AVAILABILITY", savedAvailability.getId(), 
                              "Availability set for " + availability.getDayOfWeek() + " " + 
                              availability.getStartTime() + "-" + availability.getEndTime());
        
        log.info("Availability set successfully with ID: {}", savedAvailability.getId());
        return savedAvailability;
    }
    
    /**
     * Update doctor availability
     */
    public DoctorAvailability updateAvailability(Long availabilityId, DoctorAvailability updatedAvailability, Long userId) {
        log.info("Updating availability ID: {} by user: {}", availabilityId, userId);
        
        DoctorAvailability existingAvailability = doctorAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + availabilityId));
        
        // Check if user has permission to update this availability
        validateAvailabilityAccess(existingAvailability, userId);
        
        // Validate availability data
        validateAvailabilityData(updatedAvailability);
        
        // Check for overlapping availability (excluding current availability)
        validateNoOverlappingAvailability(updatedAvailability, availabilityId);
        
        // Update fields
        existingAvailability.setDayOfWeek(updatedAvailability.getDayOfWeek());
        existingAvailability.setStartTime(updatedAvailability.getStartTime());
        existingAvailability.setEndTime(updatedAvailability.getEndTime());
        existingAvailability.setIsAvailable(updatedAvailability.getIsAvailable());
        existingAvailability.setMaxAppointments(updatedAvailability.getMaxAppointments());
        existingAvailability.setNotes(updatedAvailability.getNotes());
        existingAvailability.setUpdatedAt(LocalDateTime.now());
        
        DoctorAvailability savedAvailability = doctorAvailabilityRepository.save(existingAvailability);
        
        // Audit the availability update
        auditService.logAction("AVAILABILITY_UPDATED", userId, "AVAILABILITY", availabilityId, 
                              "Availability updated for " + updatedAvailability.getDayOfWeek());
        
        log.info("Availability updated successfully ID: {}", availabilityId);
        return savedAvailability;
    }
    
    /**
     * Delete doctor availability
     */
    public void deleteAvailability(Long availabilityId, Long userId) {
        log.info("Deleting availability ID: {} by user: {}", availabilityId, userId);
        
        DoctorAvailability availability = doctorAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found with ID: " + availabilityId));
        
        // Check if user has permission to delete this availability
        validateAvailabilityAccess(availability, userId);
        
        doctorAvailabilityRepository.delete(availability);
        
        // Audit the availability deletion
        auditService.logAction("AVAILABILITY_DELETED", userId, "AVAILABILITY", availabilityId, 
                              "Availability deleted for " + availability.getDayOfWeek());
        
        log.info("Availability deleted successfully ID: {}", availabilityId);
    }
    
    /**
     * Get doctor availability by ID
     */
    @Transactional(readOnly = true)
    public Optional<DoctorAvailability> getAvailabilityById(Long availabilityId) {
        return doctorAvailabilityRepository.findById(availabilityId);
    }
    
    /**
     * Get all availability for a doctor
     */
    @Transactional(readOnly = true)
    public List<DoctorAvailability> getDoctorAvailability(User doctor) {
        return doctorAvailabilityRepository.findByDoctorOrderByDayOfWeekAsc(doctor);
    }
    
    /**
     * Get availability for a specific day
     */
    @Transactional(readOnly = true)
    public List<DoctorAvailability> getAvailabilityByDay(DayOfWeek dayOfWeek) {
        return doctorAvailabilityRepository.findByDayOfWeekAndIsAvailableTrue(dayOfWeek);
    }
    
    /**
     * Get available doctors for a specific date and time
     */
    @Transactional(readOnly = true)
    public List<User> getAvailableDoctors(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        
        return doctorAvailabilityRepository.findAvailableDoctors(dayOfWeek, time);
    }
    
    /**
     * Check if doctor is available at specific time
     */
    @Transactional(readOnly = true)
    public boolean isDoctorAvailableAtTime(User doctor, DayOfWeek dayOfWeek, LocalTime time) {
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                .findByDoctorAndDayOfWeekAndIsAvailableTrue(doctor, dayOfWeek);
        
        for (DoctorAvailability availability : availabilities) {
            if (time.isAfter(availability.getStartTime()) && time.isBefore(availability.getEndTime())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get doctor availability for a date range
     */
    @Transactional(readOnly = true)
    public List<DoctorAvailability> getDoctorAvailabilityForDateRange(User doctor, LocalDate startDate, LocalDate endDate) {
        return doctorAvailabilityRepository.findByDoctorAndDateRange(doctor, startDate, endDate);
    }
    
    /**
     * Get all active availabilities
     */
    @Transactional(readOnly = true)
    public List<DoctorAvailability> getAllActiveAvailabilities() {
        return doctorAvailabilityRepository.findByIsAvailableTrue();
    }
    
    /**
     * Get availability by doctor and day
     */
    @Transactional(readOnly = true)
    public List<DoctorAvailability> getAvailabilityByDoctorAndDay(User doctor, DayOfWeek dayOfWeek) {
        return doctorAvailabilityRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);
    }
    
    /**
     * Get availability statistics for a doctor
     */
    @Transactional(readOnly = true)
    public AvailabilityStatistics getAvailabilityStatistics(User doctor) {
        long totalSlots = doctorAvailabilityRepository.countByDoctor(doctor);
        long activeSlots = doctorAvailabilityRepository.countByDoctorAndIsAvailableTrue(doctor);
        long inactiveSlots = totalSlots - activeSlots;
        
        return AvailabilityStatistics.builder()
                .totalSlots(totalSlots)
                .activeSlots(activeSlots)
                .inactiveSlots(inactiveSlots)
                .build();
    }
    
    /**
     * Set doctor as unavailable for a specific time period
     */
    public void setDoctorUnavailable(User doctor, LocalDateTime startDateTime, LocalDateTime endDateTime, String reason) {
        log.info("Setting doctor {} as unavailable from {} to {}", doctor.getId(), startDateTime, endDateTime);
        
        // Create temporary unavailability record
        DoctorAvailability unavailability = new DoctorAvailability();
        unavailability.setDoctor(doctor);
        unavailability.setDayOfWeek(startDateTime.getDayOfWeek());
        unavailability.setStartTime(startDateTime.toLocalTime());
        unavailability.setEndTime(endDateTime.toLocalTime());
        unavailability.setIsAvailable(false);
        unavailability.setNotes("Temporary unavailability: " + reason);
        unavailability.setCreatedAt(LocalDateTime.now());
        unavailability.setUpdatedAt(LocalDateTime.now());
        
        doctorAvailabilityRepository.save(unavailability);
        
        // Audit the unavailability setting
        auditService.logAction("DOCTOR_UNAVAILABLE", doctor.getId(), "AVAILABILITY", unavailability.getId(), 
                              "Doctor set as unavailable: " + reason);
        
        log.info("Doctor set as unavailable successfully");
    }
    
    /**
     * Get next available slot for a doctor
     */
    @Transactional(readOnly = true)
    public Optional<LocalDateTime> getNextAvailableSlot(User doctor, LocalDateTime fromDateTime) {
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
                .findByDoctorAndIsAvailableTrueOrderByDayOfWeekAsc(doctor);
        
        LocalDateTime currentDateTime = fromDateTime;
        
        // Check next 30 days
        for (int day = 0; day < 30; day++) {
            LocalDate currentDate = currentDateTime.toLocalDate();
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
            
            for (DoctorAvailability availability : availabilities) {
                if (availability.getDayOfWeek() == currentDayOfWeek) {
                    LocalDateTime slotStart = LocalDateTime.of(currentDate, availability.getStartTime());
                    
                    if (slotStart.isAfter(currentDateTime)) {
                        return Optional.of(slotStart);
                    }
                }
            }
            
            currentDateTime = currentDateTime.plusDays(1);
        }
        
        return Optional.empty();
    }
    
    /**
     * Validate availability data
     */
    private void validateAvailabilityData(DoctorAvailability availability) {
        if (availability.getDoctor() == null || availability.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor is required");
        }
        if (availability.getDayOfWeek() == null) {
            throw new IllegalArgumentException("Day of week is required");
        }
        if (availability.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }
        if (availability.getEndTime() == null) {
            throw new IllegalArgumentException("End time is required");
        }
        if (availability.getStartTime().isAfter(availability.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        if (availability.getMaxAppointments() != null && availability.getMaxAppointments() <= 0) {
            throw new IllegalArgumentException("Max appointments must be positive");
        }
    }
    
    /**
     * Validate no overlapping availability
     */
    private void validateNoOverlappingAvailability(DoctorAvailability availability) {
        validateNoOverlappingAvailability(availability, null);
    }
    
    /**
     * Validate no overlapping availability (excluding specific availability)
     */
    private void validateNoOverlappingAvailability(DoctorAvailability availability, Long excludeAvailabilityId) {
        List<DoctorAvailability> existingAvailabilities = doctorAvailabilityRepository
                .findByDoctorAndDayOfWeek(availability.getDoctor(), availability.getDayOfWeek());
        
        for (DoctorAvailability existing : existingAvailabilities) {
            if (excludeAvailabilityId != null && existing.getId().equals(excludeAvailabilityId)) {
                continue;
            }
            
            // Check for overlap
            if (availability.getStartTime().isBefore(existing.getEndTime()) && 
                availability.getEndTime().isAfter(existing.getStartTime())) {
                throw new IllegalArgumentException("Availability overlaps with existing slot");
            }
        }
    }
    
    /**
     * Validate availability access permissions
     */
    private void validateAvailabilityAccess(DoctorAvailability availability, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Admin can access all availabilities
        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }
        
        // Doctor can access their own availability
        if (user.getRole() == User.UserRole.DOCTOR && availability.getDoctor().getId().equals(userId)) {
            return;
        }
        
        throw new IllegalArgumentException("User does not have permission to access this availability");
    }
    
    /**
     * Availability statistics DTO
     */
    public static class AvailabilityStatistics {
        private final long totalSlots;
        private final long activeSlots;
        private final long inactiveSlots;
        
        public AvailabilityStatistics(long totalSlots, long activeSlots, long inactiveSlots) {
            this.totalSlots = totalSlots;
            this.activeSlots = activeSlots;
            this.inactiveSlots = inactiveSlots;
        }
        
        // Getters
        public long getTotalSlots() { return totalSlots; }
        public long getActiveSlots() { return activeSlots; }
        public long getInactiveSlots() { return inactiveSlots; }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private long totalSlots;
            private long activeSlots;
            private long inactiveSlots;
            
            public Builder totalSlots(long totalSlots) {
                this.totalSlots = totalSlots;
                return this;
            }
            
            public Builder activeSlots(long activeSlots) {
                this.activeSlots = activeSlots;
                return this;
            }
            
            public Builder inactiveSlots(long inactiveSlots) {
                this.inactiveSlots = inactiveSlots;
                return this;
            }
            
            public AvailabilityStatistics build() {
                return new AvailabilityStatistics(totalSlots, activeSlots, inactiveSlots);
            }
        }
    }
} 