package com.healthcare.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "emr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EMR {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;
    
    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;
    
    @Column(name = "present_illness", columnDefinition = "TEXT")
    private String presentIllness;
    
    @Column(name = "past_medical_history", columnDefinition = "TEXT")
    private String pastMedicalHistory;
    
    @Column(name = "family_history", columnDefinition = "TEXT")
    private String familyHistory;
    
    @Column(name = "social_history", columnDefinition = "TEXT")
    private String socialHistory;
    
    @Column(name = "vital_signs", columnDefinition = "JSON")
    private String vitalSigns; // JSON format: {"bloodPressure": "120/80", "temperature": "98.6", "pulse": "72", "weight": "70kg"}
    
    @Column(name = "physical_examination", columnDefinition = "TEXT")
    private String physicalExamination;
    
    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;
    
    @Column(name = "lab_results", columnDefinition = "TEXT")
    private String labResults;
    
    @Column(name = "imaging_results", columnDefinition = "TEXT")
    private String imagingResults;
    
    @Column(name = "follow_up_instructions", columnDefinition = "TEXT")
    private String followUpInstructions;
    
    @Column(name = "is_confidential", nullable = false)
    private Boolean isConfidential = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
} 