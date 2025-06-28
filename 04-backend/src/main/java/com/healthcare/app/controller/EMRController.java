package com.healthcare.app.controller;

import com.healthcare.app.dto.EMRRequest;
import com.healthcare.app.dto.EMRResponse;
import com.healthcare.app.dto.EMRStatistics;
import com.healthcare.app.dto.PatientMedicalHistory;
import com.healthcare.app.dto.DoctorEMRSummary;
import com.healthcare.app.entity.EMR;
import com.healthcare.app.entity.User;
import com.healthcare.app.service.EMRService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emrs")
@CrossOrigin(origins = "*")
public class EMRController {

    private static final Logger logger = LoggerFactory.getLogger(EMRController.class);

    @Autowired
    private EMRService emrService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * Create a new EMR record
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<EMRResponse> createEMR(
            @Valid @RequestBody EMRRequest request,
            @RequestParam Long userId) {
        
        logger.info("Creating EMR for user: {}", userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            EMR emr = convertToEntity(request);
            emr.setCreatedBy(user);
            
            EMR savedEMR = emrService.createEMR(emr);
            EMRResponse response = convertToResponse(savedEMR);
            
            logger.info("EMR created successfully with ID: {}", savedEMR.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating EMR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get EMR by ID
     */
    @GetMapping("/{emrId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<EMRResponse> getEMR(
            @PathVariable Long emrId,
            @RequestParam Long userId) {
        
        logger.info("Fetching EMR: {} for user: {}", emrId, userId);
        
        try {
            EMR emr = emrService.getEMRById(emrId);
            if (emr == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            // Check if user has access to this EMR
            if (!authorizationService.canAccessEMR(userId, emr)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
            }

            EMRResponse response = convertToResponse(emr);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching EMR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Update EMR
     */
    @PutMapping("/{emrId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<EMRResponse> updateEMR(
            @PathVariable Long emrId,
            @Valid @RequestBody EMRRequest request,
            @RequestParam Long userId) {
        
        logger.info("Updating EMR: {} by user: {}", emrId, userId);
        
        try {
            EMR updatedEMR = convertToEntity(request);
            EMR savedEMR = emrService.updateEMR(emrId, updatedEMR, userId);
            
            EMRResponse response = convertToResponse(savedEMR);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating EMR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Delete EMR
     */
    @DeleteMapping("/{emrId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> deleteEMR(
            @PathVariable Long emrId,
            @RequestParam Long userId) {
        
        logger.info("Deleting EMR: {} by user: {}", emrId, userId);
        
        try {
            emrService.deleteEMR(emrId, userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting EMR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    /**
     * Get EMRs by patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<EMRResponse>> getEMRsByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) Integer limit) {
        
        logger.info("Fetching EMRs for patient: {} with limit: {}", patientId, limit);
        
        try {
            User patient = userService.getUserById(patientId);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<EMR> emrs;
            if (limit != null) {
                emrs = emrService.getRecentEMRsForPatient(patient, limit);
            } else {
                emrs = emrService.getEMRsByPatient(patient);
            }

            List<EMRResponse> responses = emrs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching EMRs by patient: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get EMRs by doctor
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<EMRResponse>> getEMRsByDoctor(
            @PathVariable Long doctorId) {
        
        logger.info("Fetching EMRs for doctor: {}", doctorId);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<EMR> emrs = emrService.getEMRsByDoctor(doctor);
            
            List<EMRResponse> responses = emrs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching EMRs by doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get EMRs by patient and doctor
     */
    @GetMapping("/patient/{patientId}/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<EMRResponse>> getEMRsByPatientAndDoctor(
            @PathVariable Long patientId,
            @PathVariable Long doctorId) {
        
        logger.info("Fetching EMRs for patient: {} and doctor: {}", patientId, doctorId);
        
        try {
            User patient = userService.getUserById(patientId);
            User doctor = userService.getUserById(doctorId);
            
            if (patient == null || doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<EMR> emrs = emrService.getEMRsByPatientAndDoctor(patient, doctor);
            
            List<EMRResponse> responses = emrs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching EMRs by patient and doctor: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get EMRs by date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<EMRResponse>> getEMRsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam Long userId) {
        
        logger.info("Fetching EMRs between {} and {} for user: {}", startDate, endDate, userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<EMR> emrs = emrService.getEMRsByDateRange(user, startDate, endDate);
            
            List<EMRResponse> responses = emrs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching EMRs by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get EMRs by diagnosis
     */
    @GetMapping("/diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<EMRResponse>> getEMRsByDiagnosis(
            @RequestParam String diagnosis) {
        
        logger.info("Fetching EMRs with diagnosis: {}", diagnosis);
        
        try {
            List<EMR> emrs = emrService.getEMRsByDiagnosis(diagnosis);
            
            List<EMRResponse> responses = emrs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching EMRs by diagnosis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Search EMRs by keyword
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<EMRResponse>> searchEMRsByKeyword(
            @RequestParam String keyword,
            @RequestParam Long userId) {
        
        logger.info("Searching EMRs with keyword: {} for user: {}", keyword, userId);
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            List<EMR> emrs = emrService.searchEMRsByKeyword(user, keyword);
            
            List<EMRResponse> responses = emrs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error searching EMRs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get paginated EMRs
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Page<EMRResponse>> getPaginatedEMRs(
            Pageable pageable,
            @RequestParam Long userId) {
        
        logger.info("Fetching paginated EMRs for user: {} with page: {}, size: {}", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            Page<EMR> emrs = emrService.getPaginatedEMRs(user, pageable);
            Page<EMRResponse> responses = emrs.map(this::convertToResponse);
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            logger.error("Error fetching paginated EMRs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get EMR statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<EMRStatistics> getEMRStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Fetching EMR statistics from {} to {}", startDate, endDate);
        
        try {
            EMRStatistics statistics = emrService.getEMRStatistics(startDate, endDate);
            return ResponseEntity.ok(statistics);
            
        } catch (Exception e) {
            logger.error("Error fetching EMR statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get patient medical history
     */
    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<PatientMedicalHistory> getPatientMedicalHistory(
            @PathVariable Long patientId) {
        
        logger.info("Fetching medical history for patient: {}", patientId);
        
        try {
            User patient = userService.getUserById(patientId);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            PatientMedicalHistory history = emrService.getPatientMedicalHistory(patient);
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("Error fetching patient medical history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get doctor EMR summary
     */
    @GetMapping("/doctor/{doctorId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorEMRSummary> getDoctorEMRSummary(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("Fetching EMR summary for doctor: {} from {} to {}", doctorId, startDate, endDate);
        
        try {
            User doctor = userService.getUserById(doctorId);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }

            DoctorEMRSummary summary = emrService.getDoctorEMRSummary(doctor, startDate, endDate);
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            logger.error("Error fetching doctor EMR summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // Helper methods for conversion
    private EMR convertToEntity(EMRRequest request) {
        EMR emr = new EMR();
        emr.setDiagnosis(request.getDiagnosis());
        emr.setSymptoms(request.getSymptoms());
        emr.setTreatment(request.getTreatment());
        emr.setPrescriptions(request.getPrescriptions());
        emr.setLabOrders(request.getLabOrders());
        emr.setImagingOrders(request.getImagingOrders());
        emr.setNotes(request.getNotes());
        emr.setFollowUpDate(request.getFollowUpDate());
        emr.setFollowUpNotes(request.getFollowUpNotes());
        
        if (request.getDoctorId() != null) {
            emr.setDoctor(userService.getUserById(request.getDoctorId()));
        }
        if (request.getPatientId() != null) {
            emr.setPatient(userService.getUserById(request.getPatientId()));
        }
        
        return emr;
    }

    private EMRResponse convertToResponse(EMR emr) {
        EMRResponse response = new EMRResponse();
        response.setId(emr.getId());
        response.setDiagnosis(emr.getDiagnosis());
        response.setSymptoms(emr.getSymptoms());
        response.setTreatment(emr.getTreatment());
        response.setPrescriptions(emr.getPrescriptions());
        response.setLabOrders(emr.getLabOrders());
        response.setImagingOrders(emr.getImagingOrders());
        response.setNotes(emr.getNotes());
        response.setFollowUpDate(emr.getFollowUpDate());
        response.setFollowUpNotes(emr.getFollowUpNotes());
        response.setCreatedAt(emr.getCreatedAt());
        response.setUpdatedAt(emr.getUpdatedAt());
        
        if (emr.getDoctor() != null) {
            response.setDoctorId(emr.getDoctor().getId());
            response.setDoctorName(emr.getDoctor().getFirstName() + " " + emr.getDoctor().getLastName());
        }
        if (emr.getPatient() != null) {
            response.setPatientId(emr.getPatient().getId());
            response.setPatientName(emr.getPatient().getFirstName() + " " + emr.getPatient().getLastName());
        }
        if (emr.getCreatedBy() != null) {
            response.setCreatedById(emr.getCreatedBy().getId());
        }
        
        return response;
    }
} 