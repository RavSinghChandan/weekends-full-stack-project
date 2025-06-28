package com.healthcare.app.dto;

import com.healthcare.app.entity.Appointment;
import java.time.LocalDateTime;

public class AppointmentResponse {
    
    private Long id;
    private LocalDateTime appointmentDate;
    private Integer duration;
    private String reason;
    private String notes;
    private Appointment.AppointmentStatus status;
    private Boolean isUrgent;
    private Boolean followUpRequired;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entity IDs and names
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private Long createdById;
    
    // Constructors
    public AppointmentResponse() {}
    
    public AppointmentResponse(Long id, LocalDateTime appointmentDate, Integer duration, String reason,
                             String notes, Appointment.AppointmentStatus status, Boolean isUrgent,
                             Boolean followUpRequired, String cancellationReason, LocalDateTime createdAt,
                             LocalDateTime updatedAt, Long doctorId, String doctorName, Long patientId,
                             String patientName, Long createdById) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.duration = duration;
        this.reason = reason;
        this.notes = notes;
        this.status = status;
        this.isUrgent = isUrgent;
        this.followUpRequired = followUpRequired;
        this.cancellationReason = cancellationReason;
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
    
    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Appointment.AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(Appointment.AppointmentStatus status) {
        this.status = status;
    }
    
    public Boolean getIsUrgent() {
        return isUrgent;
    }
    
    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }
    
    public Boolean getFollowUpRequired() {
        return followUpRequired;
    }
    
    public void setFollowUpRequired(Boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
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
        return "AppointmentResponse{" +
                "id=" + id +
                ", appointmentDate=" + appointmentDate +
                ", duration=" + duration +
                ", reason='" + reason + '\'' +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                ", isUrgent=" + isUrgent +
                ", followUpRequired=" + followUpRequired +
                ", cancellationReason='" + cancellationReason + '\'' +
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