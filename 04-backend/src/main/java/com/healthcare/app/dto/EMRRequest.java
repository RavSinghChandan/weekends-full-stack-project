package com.healthcare.app.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class EMRRequest {
    
    @NotBlank(message = "Diagnosis is required")
    @Size(max = 500, message = "Diagnosis cannot exceed 500 characters")
    private String diagnosis;
    
    @NotBlank(message = "Symptoms are required")
    @Size(max = 1000, message = "Symptoms cannot exceed 1000 characters")
    private String symptoms;
    
    @NotBlank(message = "Treatment is required")
    @Size(max = 1000, message = "Treatment cannot exceed 1000 characters")
    private String treatment;
    
    @Size(max = 1000, message = "Prescriptions cannot exceed 1000 characters")
    private String prescriptions;
    
    @Size(max = 500, message = "Lab orders cannot exceed 500 characters")
    private String labOrders;
    
    @Size(max = 500, message = "Imaging orders cannot exceed 500 characters")
    private String imagingOrders;
    
    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;
    
    private LocalDateTime followUpDate;
    
    @Size(max = 1000, message = "Follow-up notes cannot exceed 1000 characters")
    private String followUpNotes;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    // Constructors
    public EMRRequest() {}
    
    public EMRRequest(String diagnosis, String symptoms, String treatment, String prescriptions,
                     String labOrders, String imagingOrders, String notes, LocalDateTime followUpDate,
                     String followUpNotes, Long doctorId, Long patientId) {
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatment = treatment;
        this.prescriptions = prescriptions;
        this.labOrders = labOrders;
        this.imagingOrders = imagingOrders;
        this.notes = notes;
        this.followUpDate = followUpDate;
        this.followUpNotes = followUpNotes;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }
    
    // Getters and Setters
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public String getTreatment() {
        return treatment;
    }
    
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    
    public String getPrescriptions() {
        return prescriptions;
    }
    
    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }
    
    public String getLabOrders() {
        return labOrders;
    }
    
    public void setLabOrders(String labOrders) {
        this.labOrders = labOrders;
    }
    
    public String getImagingOrders() {
        return imagingOrders;
    }
    
    public void setImagingOrders(String imagingOrders) {
        this.imagingOrders = imagingOrders;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    public String getFollowUpNotes() {
        return followUpNotes;
    }
    
    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    @Override
    public String toString() {
        return "EMRRequest{" +
                "diagnosis='" + diagnosis + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", treatment='" + treatment + '\'' +
                ", prescriptions='" + prescriptions + '\'' +
                ", labOrders='" + labOrders + '\'' +
                ", imagingOrders='" + imagingOrders + '\'' +
                ", notes='" + notes + '\'' +
                ", followUpDate=" + followUpDate +
                ", followUpNotes='" + followUpNotes + '\'' +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                '}';
    }
} 