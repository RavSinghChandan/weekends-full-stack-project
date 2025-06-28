package com.healthcare.app.dto;

import java.time.LocalDateTime;

public class EMRResponse {
    
    private Long id;
    private String diagnosis;
    private String symptoms;
    private String treatment;
    private String prescriptions;
    private String labOrders;
    private String imagingOrders;
    private String notes;
    private LocalDateTime followUpDate;
    private String followUpNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entity IDs and names
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private Long createdById;
    
    // Constructors
    public EMRResponse() {}
    
    public EMRResponse(Long id, String diagnosis, String symptoms, String treatment, String prescriptions,
                      String labOrders, String imagingOrders, String notes, LocalDateTime followUpDate,
                      String followUpNotes, LocalDateTime createdAt, LocalDateTime updatedAt, Long doctorId,
                      String doctorName, Long patientId, String patientName, Long createdById) {
        this.id = id;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatment = treatment;
        this.prescriptions = prescriptions;
        this.labOrders = labOrders;
        this.imagingOrders = imagingOrders;
        this.notes = notes;
        this.followUpDate = followUpDate;
        this.followUpNotes = followUpNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.createdById = createdById;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public Long getCreatedById() {
        return createdById;
    }
    
    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
    
    @Override
    public String toString() {
        return "EMRResponse{" +
                "id=" + id +
                ", diagnosis='" + diagnosis + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", treatment='" + treatment + '\'' +
                ", prescriptions='" + prescriptions + '\'' +
                ", labOrders='" + labOrders + '\'' +
                ", imagingOrders='" + imagingOrders + '\'' +
                ", notes='" + notes + '\'' +
                ", followUpDate=" + followUpDate +
                ", followUpNotes='" + followUpNotes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", createdById=" + createdById +
                '}';
    }
} 