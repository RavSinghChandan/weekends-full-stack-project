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
@Table(name = "prescriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Prescription {
    
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
    
    @Column(name = "prescription_date", nullable = false)
    private LocalDateTime prescriptionDate;
    
    @Column(name = "medication_name", nullable = false)
    private String medicationName;
    
    @Column(name = "dosage", nullable = false)
    private String dosage;
    
    @Column(name = "frequency", nullable = false)
    private String frequency;
    
    @Column(name = "duration", nullable = false)
    private String duration;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "refills_allowed")
    private Integer refillsAllowed = 0;
    
    @Column(name = "refills_used")
    private Integer refillsUsed = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;
    
    @Column(name = "is_controlled_substance", nullable = false)
    private Boolean isControlledSubstance = false;
    
    @Column(name = "side_effects", columnDefinition = "TEXT")
    private String sideEffects;
    
    @Column(name = "contraindications", columnDefinition = "TEXT")
    private String contraindications;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "filled_date")
    private LocalDateTime filledDate;
    
    @Column(name = "pharmacy_name")
    private String pharmacyName;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum PrescriptionStatus {
        ACTIVE, FILLED, EXPIRED, CANCELLED, DISCONTINUED
    }
} 