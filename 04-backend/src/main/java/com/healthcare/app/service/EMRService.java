package com.healthcare.app.service;

import com.healthcare.app.entity.EMR;
import com.healthcare.app.entity.User;
import com.healthcare.app.repository.EMRRepository;
import com.healthcare.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EMRService {
    
    private final EMRRepository emrRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    
    /**
     * Create a new EMR record
     */
    public EMR createEMR(EMR emr) {
        log.info("Creating EMR for patient: {} by doctor: {}", 
                emr.getPatient().getId(), emr.getDoctor().getId());
        
        // Validate EMR data
        validateEMRData(emr);
        
        // Check if doctor exists and is active
        User doctor = userRepository.findById(emr.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        
        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }
        
        if (!doctor.getIsActive()) {
            throw new IllegalArgumentException("Doctor is not active");
        }
        
        // Check if patient exists and is active
        User patient = userRepository.findById(emr.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        
        if (patient.getRole() != User.UserRole.PATIENT) {
            throw new IllegalArgumentException("User is not a patient");
        }
        
        if (!patient.getIsActive()) {
            throw new IllegalArgumentException("Patient is not active");
        }
        
        // Set default values
        emr.setCreatedAt(LocalDateTime.now());
        emr.setUpdatedAt(LocalDateTime.now());
        
        EMR savedEMR = emrRepository.save(emr);
        
        // Audit the EMR creation
        auditService.logAction("EMR_CREATED", doctor.getId(), "EMR", savedEMR.getId(), 
                              "EMR created for patient: " + patient.getEmail());
        
        log.info("EMR created successfully with ID: {}", savedEMR.getId());
        return savedEMR;
    }
    
    /**
     * Update EMR record
     */
    public EMR updateEMR(Long emrId, EMR updatedEMR, Long userId) {
        log.info("Updating EMR ID: {} by user: {}", emrId, userId);
        
        EMR existingEMR = emrRepository.findById(emrId)
                .orElseThrow(() -> new IllegalArgumentException("EMR not found with ID: " + emrId));
        
        // Check if user has permission to update this EMR
        validateEMRAccess(existingEMR, userId);
        
        // Validate EMR data
        validateEMRData(updatedEMR);
        
        // Update allowed fields
        existingEMR.setChiefComplaint(updatedEMR.getChiefComplaint());
        existingEMR.setHistoryOfPresentIllness(updatedEMR.getHistoryOfPresentIllness());
        existingEMR.setPastMedicalHistory(updatedEMR.getPastMedicalHistory());
        existingEMR.setFamilyHistory(updatedEMR.getFamilyHistory());
        existingEMR.setSocialHistory(updatedEMR.getSocialHistory());
        existingEMR.setReviewOfSystems(updatedEMR.getReviewOfSystems());
        existingEMR.setPhysicalExamination(updatedEMR.getPhysicalExamination());
        existingEMR.setAssessment(updatedEMR.getAssessment());
        existingEMR.setPlan(updatedEMR.getPlan());
        existingEMR.setDiagnosis(updatedEMR.getDiagnosis());
        existingEMR.setPrescriptions(updatedEMR.getPrescriptions());
        existingEMR.setLabOrders(updatedEMR.getLabOrders());
        existingEMR.setImagingOrders(updatedEMR.getImagingOrders());
        existingEMR.setFollowUpInstructions(updatedEMR.getFollowUpInstructions());
        existingEMR.setNotes(updatedEMR.getNotes());
        existingEMR.setUpdatedAt(LocalDateTime.now());
        
        EMR savedEMR = emrRepository.save(existingEMR);
        
        // Audit the EMR update
        auditService.logAction("EMR_UPDATED", userId, "EMR", emrId, 
                              "EMR updated by user: " + userId);
        
        log.info("EMR updated successfully ID: {}", emrId);
        return savedEMR;
    }
    
    /**
     * Delete EMR record
     */
    public void deleteEMR(Long emrId, Long userId) {
        log.info("Deleting EMR ID: {} by user: {}", emrId, userId);
        
        EMR emr = emrRepository.findById(emrId)
                .orElseThrow(() -> new IllegalArgumentException("EMR not found with ID: " + emrId));
        
        // Check if user has permission to delete this EMR
        validateEMRAccess(emr, userId);
        
        emrRepository.delete(emr);
        
        // Audit the EMR deletion
        auditService.logAction("EMR_DELETED", userId, "EMR", emrId, 
                              "EMR deleted by user: " + userId);
        
        log.info("EMR deleted successfully ID: {}", emrId);
    }
    
    /**
     * Get EMR by ID
     */
    @Transactional(readOnly = true)
    public Optional<EMR> getEMRById(Long emrId) {
        return emrRepository.findById(emrId);
    }
    
    /**
     * Get EMRs by patient
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByPatient(User patient) {
        return emrRepository.findByPatientOrderByCreatedAtDesc(patient);
    }
    
    /**
     * Get EMRs by doctor
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByDoctor(User doctor) {
        return emrRepository.findByDoctorOrderByCreatedAtDesc(doctor);
    }
    
    /**
     * Get EMRs by patient and doctor
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByPatientAndDoctor(User patient, User doctor) {
        return emrRepository.findByPatientAndDoctorOrderByCreatedAtDesc(patient, doctor);
    }
    
    /**
     * Get EMRs by date range
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return emrRepository.findDoctorEMRsInDateRange(user, startDate, endDate);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return emrRepository.findPatientEMRsInDateRange(user, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Invalid user role for EMR access");
        }
    }
    
    /**
     * Get EMRs by diagnosis
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByDiagnosis(String diagnosis) {
        return emrRepository.findByDiagnosisContainingIgnoreCase(diagnosis);
    }
    
    /**
     * Get EMRs by patient and diagnosis
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByPatientAndDiagnosis(User patient, String diagnosis) {
        return emrRepository.findByPatientAndDiagnosisContainingIgnoreCase(patient, diagnosis);
    }
    
    /**
     * Get recent EMRs for patient
     */
    @Transactional(readOnly = true)
    public List<EMR> getRecentEMRsForPatient(User patient, int limit) {
        return emrRepository.findRecentEMRsForPatient(patient, limit);
    }
    
    /**
     * Get EMRs by appointment
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByAppointment(Long appointmentId) {
        return emrRepository.findByAppointmentId(appointmentId);
    }
    
    /**
     * Get paginated EMRs
     */
    @Transactional(readOnly = true)
    public Page<EMR> getPaginatedEMRs(User user, Pageable pageable) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return emrRepository.findByDoctor(user, pageable);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return emrRepository.findByPatient(user, pageable);
        } else {
            throw new IllegalArgumentException("Invalid user role for EMR access");
        }
    }
    
    /**
     * Get EMRs by date
     */
    @Transactional(readOnly = true)
    public List<EMR> getEMRsByDate(User user, LocalDateTime date) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return emrRepository.findDoctorEMRsByDate(user, date);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return emrRepository.findPatientEMRsByDate(user, date);
        } else {
            throw new IllegalArgumentException("Invalid user role for EMR access");
        }
    }
    
    /**
     * Search EMRs by keyword
     */
    @Transactional(readOnly = true)
    public List<EMR> searchEMRsByKeyword(User user, String keyword) {
        if (user.getRole() == User.UserRole.DOCTOR) {
            return emrRepository.searchDoctorEMRsByKeyword(user, keyword);
        } else if (user.getRole() == User.UserRole.PATIENT) {
            return emrRepository.searchPatientEMRsByKeyword(user, keyword);
        } else {
            throw new IllegalArgumentException("Invalid user role for EMR access");
        }
    }
    
    /**
     * Get EMR statistics
     */
    @Transactional(readOnly = true)
    public EMRStatistics getEMRStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        long totalEMRs = emrRepository.countEMRsInDateRange(startDate, endDate);
        long emrsWithDiagnosis = emrRepository.countEMRsWithDiagnosisInDateRange(startDate, endDate);
        long emrsWithPrescriptions = emrRepository.countEMRsWithPrescriptionsInDateRange(startDate, endDate);
        long emrsWithLabOrders = emrRepository.countEMRsWithLabOrdersInDateRange(startDate, endDate);
        long emrsWithImagingOrders = emrRepository.countEMRsWithImagingOrdersInDateRange(startDate, endDate);
        
        return EMRStatistics.builder()
                .totalEMRs(totalEMRs)
                .emrsWithDiagnosis(emrsWithDiagnosis)
                .emrsWithPrescriptions(emrsWithPrescriptions)
                .emrsWithLabOrders(emrsWithLabOrders)
                .emrsWithImagingOrders(emrsWithImagingOrders)
                .build();
    }
    
    /**
     * Get patient medical history
     */
    @Transactional(readOnly = true)
    public PatientMedicalHistory getPatientMedicalHistory(User patient) {
        List<EMR> emrs = emrRepository.findByPatientOrderByCreatedAtDesc(patient);
        
        return PatientMedicalHistory.builder()
                .patient(patient)
                .totalEMRs(emrs.size())
                .emrs(emrs)
                .build();
    }
    
    /**
     * Get doctor EMR summary
     */
    @Transactional(readOnly = true)
    public DoctorEMRSummary getDoctorEMRSummary(User doctor, LocalDateTime startDate, LocalDateTime endDate) {
        List<EMR> emrs = emrRepository.findDoctorEMRsInDateRange(doctor, startDate, endDate);
        
        long totalPatients = emrs.stream()
                .map(emr -> emr.getPatient().getId())
                .distinct()
                .count();
        
        return DoctorEMRSummary.builder()
                .doctor(doctor)
                .totalEMRs(emrs.size())
                .totalPatients(totalPatients)
                .emrs(emrs)
                .build();
    }
    
    /**
     * Validate EMR data
     */
    private void validateEMRData(EMR emr) {
        if (emr.getPatient() == null || emr.getPatient().getId() == null) {
            throw new IllegalArgumentException("Patient is required");
        }
        if (emr.getDoctor() == null || emr.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor is required");
        }
        if (emr.getChiefComplaint() == null || emr.getChiefComplaint().trim().isEmpty()) {
            throw new IllegalArgumentException("Chief complaint is required");
        }
        if (emr.getAssessment() == null || emr.getAssessment().trim().isEmpty()) {
            throw new IllegalArgumentException("Assessment is required");
        }
        if (emr.getPlan() == null || emr.getPlan().trim().isEmpty()) {
            throw new IllegalArgumentException("Plan is required");
        }
    }
    
    /**
     * Validate EMR access permissions
     */
    private void validateEMRAccess(EMR emr, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Admin can access all EMRs
        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }
        
        // Doctor can access EMRs they created
        if (user.getRole() == User.UserRole.DOCTOR && emr.getDoctor().getId().equals(userId)) {
            return;
        }
        
        // Patient can access their own EMRs
        if (user.getRole() == User.UserRole.PATIENT && emr.getPatient().getId().equals(userId)) {
            return;
        }
        
        throw new IllegalArgumentException("User does not have permission to access this EMR");
    }
    
    /**
     * EMR statistics DTO
     */
    public static class EMRStatistics {
        private final long totalEMRs;
        private final long emrsWithDiagnosis;
        private final long emrsWithPrescriptions;
        private final long emrsWithLabOrders;
        private final long emrsWithImagingOrders;
        
        public EMRStatistics(long totalEMRs, long emrsWithDiagnosis, long emrsWithPrescriptions,
                           long emrsWithLabOrders, long emrsWithImagingOrders) {
            this.totalEMRs = totalEMRs;
            this.emrsWithDiagnosis = emrsWithDiagnosis;
            this.emrsWithPrescriptions = emrsWithPrescriptions;
            this.emrsWithLabOrders = emrsWithLabOrders;
            this.emrsWithImagingOrders = emrsWithImagingOrders;
        }
        
        // Getters
        public long getTotalEMRs() { return totalEMRs; }
        public long getEmrsWithDiagnosis() { return emrsWithDiagnosis; }
        public long getEmrsWithPrescriptions() { return emrsWithPrescriptions; }
        public long getEmrsWithLabOrders() { return emrsWithLabOrders; }
        public long getEmrsWithImagingOrders() { return emrsWithImagingOrders; }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private long totalEMRs;
            private long emrsWithDiagnosis;
            private long emrsWithPrescriptions;
            private long emrsWithLabOrders;
            private long emrsWithImagingOrders;
            
            public Builder totalEMRs(long totalEMRs) {
                this.totalEMRs = totalEMRs;
                return this;
            }
            
            public Builder emrsWithDiagnosis(long emrsWithDiagnosis) {
                this.emrsWithDiagnosis = emrsWithDiagnosis;
                return this;
            }
            
            public Builder emrsWithPrescriptions(long emrsWithPrescriptions) {
                this.emrsWithPrescriptions = emrsWithPrescriptions;
                return this;
            }
            
            public Builder emrsWithLabOrders(long emrsWithLabOrders) {
                this.emrsWithLabOrders = emrsWithLabOrders;
                return this;
            }
            
            public Builder emrsWithImagingOrders(long emrsWithImagingOrders) {
                this.emrsWithImagingOrders = emrsWithImagingOrders;
                return this;
            }
            
            public EMRStatistics build() {
                return new EMRStatistics(totalEMRs, emrsWithDiagnosis, emrsWithPrescriptions,
                                       emrsWithLabOrders, emrsWithImagingOrders);
            }
        }
    }
    
    /**
     * Patient medical history DTO
     */
    public static class PatientMedicalHistory {
        private final User patient;
        private final long totalEMRs;
        private final List<EMR> emrs;
        
        public PatientMedicalHistory(User patient, long totalEMRs, List<EMR> emrs) {
            this.patient = patient;
            this.totalEMRs = totalEMRs;
            this.emrs = emrs;
        }
        
        // Getters
        public User getPatient() { return patient; }
        public long getTotalEMRs() { return totalEMRs; }
        public List<EMR> getEmrs() { return emrs; }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private User patient;
            private long totalEMRs;
            private List<EMR> emrs;
            
            public Builder patient(User patient) {
                this.patient = patient;
                return this;
            }
            
            public Builder totalEMRs(long totalEMRs) {
                this.totalEMRs = totalEMRs;
                return this;
            }
            
            public Builder emrs(List<EMR> emrs) {
                this.emrs = emrs;
                return this;
            }
            
            public PatientMedicalHistory build() {
                return new PatientMedicalHistory(patient, totalEMRs, emrs);
            }
        }
    }
    
    /**
     * Doctor EMR summary DTO
     */
    public static class DoctorEMRSummary {
        private final User doctor;
        private final long totalEMRs;
        private final long totalPatients;
        private final List<EMR> emrs;
        
        public DoctorEMRSummary(User doctor, long totalEMRs, long totalPatients, List<EMR> emrs) {
            this.doctor = doctor;
            this.totalEMRs = totalEMRs;
            this.totalPatients = totalPatients;
            this.emrs = emrs;
        }
        
        // Getters
        public User getDoctor() { return doctor; }
        public long getTotalEMRs() { return totalEMRs; }
        public long getTotalPatients() { return totalPatients; }
        public List<EMR> getEmrs() { return emrs; }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private User doctor;
            private long totalEMRs;
            private long totalPatients;
            private List<EMR> emrs;
            
            public Builder doctor(User doctor) {
                this.doctor = doctor;
                return this;
            }
            
            public Builder totalEMRs(long totalEMRs) {
                this.totalEMRs = totalEMRs;
                return this;
            }
            
            public Builder totalPatients(long totalPatients) {
                this.totalPatients = totalPatients;
                return this;
            }
            
            public Builder emrs(List<EMR> emrs) {
                this.emrs = emrs;
                return this;
            }
            
            public DoctorEMRSummary build() {
                return new DoctorEMRSummary(doctor, totalEMRs, totalPatients, emrs);
            }
        }
    }
} 