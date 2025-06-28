package com.healthcare.app.service;

import com.healthcare.app.entity.Appointment;
import com.healthcare.app.entity.User;
import com.healthcare.app.repository.AppointmentRepository;
import com.healthcare.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorAvailabilityService doctorAvailabilityService;
    private final AuditService auditService;
    
    /**
     * Create a new appointment
     */
    public Appointment createAppointment(Appointment appointment) {
        log.info("Creating appointment for patient: {} with doctor: {} at: {}", 
                appointment.getPatient().getId(), appointment.getDoctor().getId(), appointment.getAppointmentDate());
        
        // Validate appointment data
        validateAppointmentData(appointment);
        
        // Check if doctor is available
        validateDoctorAvailability(appointment);
        
        // Check for scheduling conflicts
        validateNoConflicts(appointment);
        
        // Set default values
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Audit the appointment creation
        auditService.logAction("APPOINTMENT_CREATED", appointment.getPatient().getId(), 
                              "APPOINTMENT", savedAppointment.getId(), 
                              "Appointment created with doctor: " + appointment.getDoctor().getEmail());
        
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        return savedAppointment;
    }
    
    /**
     * Update appointment
     */
    public Appointment updateAppointment(Long appointmentId, Appointment updatedAppointment, Long userId) {
        log.info("Updating appointment ID: {} by user: {}", appointmentId, userId);
        
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        
        // Check if user has permission to update this appointment
        validateAppointmentAccess(existingAppointment, userId);
        
        // Validate appointment data if date/time is being changed
        if (!existingAppointment.getAppointmentDate().equals(updatedAppointment.getAppointmentDate()) ||
            !existingAppointment.getDoctor().getId().equals(updatedAppointment.getDoctor().getId())) {
            validateAppointmentData(updatedAppointment);
            validateDoctorAvailability(updatedAppointment);
            validateNoConflicts(updatedAppointment, appointmentId);
        }
        
        // Update allowed fields
        existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        existingAppointment.setDurationMinutes(updatedAppointment.getDurationMinutes());
        existingAppointment.setType(updatedAppointment.getType());
        existingAppointment.setReason(updatedAppointment.getReason());
        existingAppointment.setNotes(updatedAppointment.getNotes());
        existingAppointment.setIsUrgent(updatedAppointment.getIsUrgent());
        existingAppointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        
        // Audit the appointment update
        auditService.logAction("APPOINTMENT_UPDATED", userId, "APPOINTMENT", appointmentId, 
                              "Appointment updated by user: " + userId);
        
        log.info("Appointment updated successfully ID: {}", appointmentId);
        return savedAppointment;
    }
    
    /**
     * Cancel appointment
     */
    public Appointment cancelAppointment(Long appointmentId, String reason, Long userId) {
        log.info("Cancelling appointment ID: {} by user: {}", appointmentId, userId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        
        // Check if appointment can be cancelled
        if (appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED && 
            appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new IllegalArgumentException("Appointment cannot be cancelled in current status: " + appointment.getStatus());
        }
        
        // Check if user has permission to cancel this appointment
        validateAppointmentAccess(appointment, userId);
        
        // Update appointment status
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledBy(userId);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Audit the appointment cancellation
        auditService.logAction("APPOINTMENT_CANCELLED", userId, "APPOINTMENT", appointmentId, 
                              "Appointment cancelled. Reason: " + reason);
        
        log.info("Appointment cancelled successfully ID: {}", appointmentId);
        return savedAppointment;
    }
    
    /**
     * Confirm appointment
     */
    public Appointment confirmAppointment(Long appointmentId, Long userId) {
        log.info("Confirming appointment ID: {} by user: {}", appointmentId, userId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        
        // Check if appointment can be confirmed
        if (appointment.getStatus() != Appointment.AppointmentStatus.SCHEDULED) {
            throw new IllegalArgumentException("Appointment cannot be confirmed in current status: " + appointment.getStatus());
        }
        
        // Check if user has permission to confirm this appointment
        validateAppointmentAccess(appointment, userId);
        
        // Update appointment status
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Audit the appointment confirmation
        auditService.logAction("APPOINTMENT_CONFIRMED", userId, "APPOINTMENT", appointmentId, 
                              "Appointment confirmed by user: " + userId);
        
        log.info("Appointment confirmed successfully ID: {}", appointmentId);
        return savedAppointment;
    }
    
    /**
     * Mark appointment as completed
     */
    public Appointment completeAppointment(Long appointmentId, Long userId) {
        log.info("Completing appointment ID: {} by user: {}", appointmentId, userId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        
        // Check if appointment can be completed
        if (appointment.getStatus() != Appointment.AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Appointment cannot be completed in current status: " + appointment.getStatus());
        }
        
        // Check if user has permission to complete this appointment
        validateAppointmentAccess(appointment, userId);
        
        // Update appointment status
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Audit the appointment completion
        auditService.logAction("APPOINTMENT_COMPLETED", userId, "APPOINTMENT", appointmentId, 
                              "Appointment completed by user: " + userId);
        
        log.info("Appointment completed successfully ID: {}", appointmentId);
        return savedAppointment;
    }
    
    /**
     * Mark appointment as no-show
     */
    public Appointment markNoShow(Long appointmentId, Long userId) {
        log.info("Marking appointment ID: {} as no-show by user: {}", appointmentId, userId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));
        
        // Check if appointment can be marked as no-show
        if (appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new IllegalArgumentException("Appointment cannot be marked as no-show in current status: " + appointment.getStatus());
        }
        
        // Check if user has permission to mark this appointment as no-show
        validateAppointmentAccess(appointment, userId);
        
        // Update appointment status
        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Audit the no-show marking
        auditService.logAction("APPOINTMENT_NO_SHOW", userId, "APPOINTMENT", appointmentId, 
                              "Appointment marked as no-show by user: " + userId);
        
        log.info("Appointment marked as no-show successfully ID: {}", appointmentId);
        return savedAppointment;
    }
    
    /**
     * Get appointment by ID
     */
    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }
    
    /**
     * Get appointments by doctor
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDoctor(User doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }
    
    /**
     * Get appointments by patient
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPatient(User patient) {
        return appointmentRepository.findByPatient(patient);
    }
    
    /**
     * Get upcoming appointments for doctor
     */
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingDoctorAppointments(User doctor) {
        return appointmentRepository.findUpcomingDoctorAppointments(doctor, LocalDateTime.now());
    }
    
    /**
     * Get upcoming appointments for patient
     */
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingPatientAppointments(User patient) {
        return appointmentRepository.findUpcomingPatientAppointments(patient, LocalDateTime.now());
    }
    
    /**
     * Get appointments by date range
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return appointmentRepository.findDoctorAppointmentsInDateRange(user, startDate, endDate);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return appointmentRepository.findPatientAppointmentsInDateRange(user, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Invalid user role for appointment access");
        }
    }
    
    /**
     * Get appointments by status
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatus(User user, Appointment.AppointmentStatus status) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return appointmentRepository.findByDoctorAndStatus(user, status);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return appointmentRepository.findByPatientAndStatus(user, status);
        } else {
            throw new IllegalArgumentException("Invalid user role for appointment access");
        }
    }
    
    /**
     * Get urgent appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getUrgentAppointments() {
        return appointmentRepository.findUrgentAppointments();
    }
    
    /**
     * Get appointments by date
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDate(User user, LocalDateTime date) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return appointmentRepository.findDoctorAppointmentsByDate(user, date);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return appointmentRepository.findPatientAppointmentsByDate(user, date);
        } else {
            throw new IllegalArgumentException("Invalid user role for appointment access");
        }
    }
    
    /**
     * Get paginated appointments by status and date range
     */
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByStatusAndDateRange(Appointment.AppointmentStatus status,
                                                                LocalDateTime startDate,
                                                                LocalDateTime endDate,
                                                                Pageable pageable) {
        return appointmentRepository.findAppointmentsByStatusAndDateRange(status, startDate, endDate, pageable);
    }
    
    /**
     * Get follow-up appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getFollowUpAppointments(Long originalAppointmentId) {
        return appointmentRepository.findFollowUpAppointments(originalAppointmentId);
    }
    
    /**
     * Get appointment statistics
     */
    @Transactional(readOnly = true)
    public AppointmentStatistics getAppointmentStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        long totalAppointments = appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.SCHEDULED, startDate, endDate) +
                                appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.CONFIRMED, startDate, endDate) +
                                appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED, startDate, endDate) +
                                appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.CANCELLED, startDate, endDate) +
                                appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.NO_SHOW, startDate, endDate);
        
        long scheduledAppointments = appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.SCHEDULED, startDate, endDate);
        long confirmedAppointments = appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.CONFIRMED, startDate, endDate);
        long completedAppointments = appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.COMPLETED, startDate, endDate);
        long cancelledAppointments = appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.CANCELLED, startDate, endDate);
        long noShowAppointments = appointmentRepository.countAppointmentsByStatus(Appointment.AppointmentStatus.NO_SHOW, startDate, endDate);
        
        return AppointmentStatistics.builder()
                .totalAppointments(totalAppointments)
                .scheduledAppointments(scheduledAppointments)
                .confirmedAppointments(confirmedAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .noShowAppointments(noShowAppointments)
                .build();
    }
    
    /**
     * Validate appointment data
     */
    private void validateAppointmentData(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor is required");
        }
        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            throw new IllegalArgumentException("Patient is required");
        }
        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Appointment date is required");
        }
        if (appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        if (appointment.getDurationMinutes() == null || appointment.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Valid appointment duration is required");
        }
        if (appointment.getType() == null) {
            throw new IllegalArgumentException("Appointment type is required");
        }
    }
    
    /**
     * Validate doctor availability
     */
    private void validateDoctorAvailability(Appointment appointment) {
        User doctor = userRepository.findById(appointment.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }
        
        if (!doctor.getIsActive()) {
            throw new IllegalArgumentException("Doctor is not active");
        }
        
        if (!doctor.getIsAvailable()) {
            throw new IllegalArgumentException("Doctor is not available");
        }
        
        // Check if doctor is available at the appointment time
        LocalDateTime appointmentDate = appointment.getAppointmentDate();
        LocalTime appointmentTime = appointmentDate.toLocalTime();
        
        boolean isAvailable = doctorAvailabilityService.isDoctorAvailableAtTime(
                doctor, appointmentDate.getDayOfWeek(), appointmentTime);
        
        if (!isAvailable) {
            throw new IllegalArgumentException("Doctor is not available at the specified time");
        }
    }
    
    /**
     * Validate no scheduling conflicts
     */
    private void validateNoConflicts(Appointment appointment) {
        validateNoConflicts(appointment, null);
    }
    
    /**
     * Validate no scheduling conflicts (excluding specific appointment)
     */
    private void validateNoConflicts(Appointment appointment, Long excludeAppointmentId) {
        LocalDateTime startTime = appointment.getAppointmentDate();
        LocalDateTime endTime = startTime.plusMinutes(appointment.getDurationMinutes());
        
        long conflictingCount;
        if (excludeAppointmentId != null) {
            conflictingCount = appointmentRepository.countConflictingAppointments(
                    appointment.getDoctor(), startTime, endTime);
        } else {
            conflictingCount = appointmentRepository.countConflictingAppointments(
                    appointment.getDoctor(), startTime, endTime);
        }
        
        if (conflictingCount > 0) {
            throw new IllegalArgumentException("Appointment conflicts with existing appointment");
        }
    }
    
    /**
     * Validate appointment access permissions
     */
    private void validateAppointmentAccess(Appointment appointment, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Admin can access all appointments
        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }
        
        // Doctor can access their own appointments
        if (user.getRole() == User.UserRole.DOCTOR && appointment.getDoctor().getId().equals(userId)) {
            return;
        }
        
        // Patient can access their own appointments
        if (user.getRole() == User.UserRole.PATIENT && appointment.getPatient().getId().equals(userId)) {
            return;
        }
        
        throw new IllegalArgumentException("User does not have permission to access this appointment");
    }
    
    /**
     * Appointment statistics DTO
     */
    public static class AppointmentStatistics {
        private final long totalAppointments;
        private final long scheduledAppointments;
        private final long confirmedAppointments;
        private final long completedAppointments;
        private final long cancelledAppointments;
        private final long noShowAppointments;
        
        public AppointmentStatistics(long totalAppointments, long scheduledAppointments, 
                                   long confirmedAppointments, long completedAppointments,
                                   long cancelledAppointments, long noShowAppointments) {
            this.totalAppointments = totalAppointments;
            this.scheduledAppointments = scheduledAppointments;
            this.confirmedAppointments = confirmedAppointments;
            this.completedAppointments = completedAppointments;
            this.cancelledAppointments = cancelledAppointments;
            this.noShowAppointments = noShowAppointments;
        }
        
        // Getters
        public long getTotalAppointments() { return totalAppointments; }
        public long getScheduledAppointments() { return scheduledAppointments; }
        public long getConfirmedAppointments() { return confirmedAppointments; }
        public long getCompletedAppointments() { return completedAppointments; }
        public long getCancelledAppointments() { return cancelledAppointments; }
        public long getNoShowAppointments() { return noShowAppointments; }
    }
} 