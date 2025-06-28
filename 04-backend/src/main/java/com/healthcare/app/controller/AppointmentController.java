package com.healthcare.app.controller;

import com.healthcare.app.dto.AppointmentRequest;
import com.healthcare.app.dto.AppointmentResponse;
import com.healthcare.app.dto.AppointmentStatistics;
import com.healthcare.app.entity.Appointment;
import com.healthcare.app.entity.User;
import com.healthcare.app.service.AppointmentService;
import com.healthcare.app.service.AuthorizationService;
import com.healthcare.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Create a new appointment
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            @RequestParam Long userId) {
        
        logger.info("Creating appointment for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Appointment appointment = convertToEntity(request);
            appointment.setCreatedBy(user);
            
            Appointment savedAppointment = appointmentService.createAppointment(appointment);
            AppointmentResponse response = convertToResponse(savedAppointment);
            
            logger.info("Appointment created successfully with ID: {}", savedAppointment.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get appointment by ID
     */
    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentResponse> getAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long userId) {
        
        logger.info("Fetching appointment: {} for user: {}", appointmentId, userId);
        
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            // Check if user has access to this appointment
            if (!authorizationService.canAccessAppointment(userId, appointment)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
            }

            AppointmentResponse response = convertToResponse(appointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Update appointment
     */
    @PutMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRequest request,
            @RequestParam Long userId) {
        
        logger.info("Updating appointment: {} by user: {}", appointmentId, userId);
        
        try {
            Appointment updatedAppointment = convertToEntity(request);
            Appointment savedAppointment = appointmentService.updateAppointment(
                appointmentId, updatedAppointment, userId);
            
            AppointmentResponse response = convertToResponse(savedAppointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Cancel appointment
     */
    @PostMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestParam String reason,
            @RequestParam Long userId) {
        
        logger.info("Cancelling appointment: {} by user: {} with reason: {}", 
            appointmentId, userId, reason);
        
        try {
            Appointment cancelledAppointment = appointmentService.cancelAppointment(
                appointmentId, reason, userId);
            
            AppointmentResponse response = convertToResponse(cancelledAppointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelling appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Confirm appointment
     */
    @PostMapping("/{appointmentId}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long userId) {
        
        logger.info("Confirming appointment: {} by user: {}", appointmentId, userId);
        
        try {
            Appointment confirmedAppointment = appointmentService.confirmAppointment(
                appointmentId, userId);
            
            AppointmentResponse response = convertToResponse(confirmedAppointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error confirming appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Complete appointment
     */
    @PostMapping("/{appointmentId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long userId) {
        
        logger.info("Completing appointment: {} by user: {}", appointmentId, userId);
        
        try {
            Appointment completedAppointment = appointmentService.completeAppointment(
                appointmentId, userId);
            
            AppointmentResponse response = convertToResponse(completedAppointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error completing appointment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Mark appointment as no-show
     */
    @PostMapping("/{appointmentId}/no-show")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> markNoShow(
            @PathVariable Long appointmentId,
            @RequestParam Long userId) {
        
        logger.info("Marking appointment: {} as no-show by user: {}", appointmentId, userId);
        
        try {
            Appointment noShowAppointment = appointmentService.markNoShow(
                appointmentId, userId);
            
            AppointmentResponse response = convertToResponse(noShowAppointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error marking appointment as no-show: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get appointments by doctor
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("Fetching appointments for doctor: {} on date: {}", doctorId, date);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Appointment> appointments;
            if (date != null) {
                appointments = appointmentService.getAppointmentsByDoctorAndDate(doctor, date);
            } else {
                appointments = appointmentService.getAppointmentsByDoctor(doctor);
            }

            List<AppointmentResponse> responses = appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching appointments by doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get appointments by patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("Fetching appointments for patient: {} on date: {}", patientId, date);
        
        try {
            User patient = userService.getUserById(patientId);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<Appointment> appointments;
            if (date != null) {
                appointments = appointmentService.getAppointmentsByPatientAndDate(patient, date);
            } else {
                appointments = appointmentService.getAppointmentsByPatient(patient);
            }

            List<AppointmentResponse> responses = appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching appointments by patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get appointments by date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Fetching appointments between {} and {}", startDate, endDate);
        
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
            
            List<AppointmentResponse> responses = appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching appointments by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get paginated appointments
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Page<AppointmentResponse>> getPaginatedAppointments(Pageable pageable) {
        
        logger.info("Fetching paginated appointments with page: {}, size: {}", 
            pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Appointment> appointments = appointmentService.getPaginatedAppointments(pageable);
            
            Page<AppointmentResponse> responses = appointments.map(this::convertToResponse);
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching paginated appointments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get appointment statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentStatistics> getAppointmentStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Fetching appointment statistics from {} to {}", startDate, endDate);
        
        try {
            AppointmentStatistics statistics = appointmentService.getAppointmentStatistics(startDate, endDate);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error fetching appointment statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get urgent appointments
     */
    @GetMapping("/urgent")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getUrgentAppointments() {
        
        logger.info("Fetching urgent appointments");
        
        try {
            List<Appointment> urgentAppointments = appointmentService.getUrgentAppointments();
            
            List<AppointmentResponse> responses = urgentAppointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching urgent appointments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Helper methods for conversion
    private Appointment convertToEntity(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setDuration(request.getDuration());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setIsUrgent(request.getIsUrgent());
        appointment.setFollowUpRequired(request.getFollowUpRequired());
        
        if (request.getDoctorId() != null) {
            appointment.setDoctor(userService.getUserById(request.getDoctorId()));
        }
        if (request.getPatientId() != null) {
            appointment.setPatient(userService.getUserById(request.getPatientId()));
        }
        
        return appointment;
    }

    private AppointmentResponse convertToResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setDuration(appointment.getDuration());
        response.setReason(appointment.getReason());
        response.setNotes(appointment.getNotes());
        response.setStatus(appointment.getStatus());
        response.setIsUrgent(appointment.getIsUrgent());
        response.setFollowUpRequired(appointment.getFollowUpRequired());
        response.setCancellationReason(appointment.getCancellationReason());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        
        if (appointment.getDoctor() != null) {
            response.setDoctorId(appointment.getDoctor().getId());
            response.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
        }
        if (appointment.getPatient() != null) {
            response.setPatientId(appointment.getPatient().getId());
            response.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        }
        if (appointment.getCreatedBy() != null) {
            response.setCreatedById(appointment.getCreatedBy().getId());
        }
        
        return response;
    }
} 