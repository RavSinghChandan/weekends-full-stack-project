package com.healthcare.app.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AppointmentRequest {
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    private Integer duration;
    
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    private Boolean isUrgent = false;
    
    private Boolean followUpRequired = false;
    
    // Constructors
    public AppointmentRequest() {}
    
    public AppointmentRequest(LocalDateTime appointmentDate, Integer duration, String reason, 
                            String notes, Long doctorId, Long patientId, Boolean isUrgent, 
                            Boolean followUpRequired) {
        this.appointmentDate = appointmentDate;
        this.duration = duration;
        this.reason = reason;
        this.notes = notes;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.isUrgent = isUrgent;
        this.followUpRequired = followUpRequired;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "appointmentDate=" + appointmentDate +
                ", duration=" + duration +
                ", reason='" + reason + '\'' +
                ", notes='" + notes + '\'' +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", isUrgent=" + isUrgent +
                ", followUpRequired=" + followUpRequired +
                '}';
    }
} 