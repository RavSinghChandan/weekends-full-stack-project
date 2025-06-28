package com.healthcare.app.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorAvailabilityRequest {
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    @NotNull(message = "Is available flag is required")
    private Boolean isAvailable;
    
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 120, message = "Slot duration must not exceed 120 minutes")
    private Integer slotDuration = 30;
    
    @Min(value = 0, message = "Max appointments per day must be non-negative")
    @Max(value = 50, message = "Max appointments per day must not exceed 50")
    private Integer maxAppointmentsPerDay = 20;
    
    private String notes;
    private Boolean isRecurring = true;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    
    // Constructors
    public DoctorAvailabilityRequest() {}
    
    public DoctorAvailabilityRequest(Long doctorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
                                   Boolean isAvailable, Integer slotDuration, Integer maxAppointmentsPerDay,
                                   String notes, Boolean isRecurring, LocalTime breakStartTime, LocalTime breakEndTime) {
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
        this.slotDuration = slotDuration;
        this.maxAppointmentsPerDay = maxAppointmentsPerDay;
        this.notes = notes;
        this.isRecurring = isRecurring;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
    
    // Getters and Setters
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public Integer getSlotDuration() {
        return slotDuration;
    }
    
    public void setSlotDuration(Integer slotDuration) {
        this.slotDuration = slotDuration;
    }
    
    public Integer getMaxAppointmentsPerDay() {
        return maxAppointmentsPerDay;
    }
    
    public void setMaxAppointmentsPerDay(Integer maxAppointmentsPerDay) {
        this.maxAppointmentsPerDay = maxAppointmentsPerDay;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsRecurring() {
        return isRecurring;
    }
    
    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
    
    public LocalTime getBreakStartTime() {
        return breakStartTime;
    }
    
    public void setBreakStartTime(LocalTime breakStartTime) {
        this.breakStartTime = breakStartTime;
    }
    
    public LocalTime getBreakEndTime() {
        return breakEndTime;
    }
    
    public void setBreakEndTime(LocalTime breakEndTime) {
        this.breakEndTime = breakEndTime;
    }
    
    @Override
    public String toString() {
        return "DoctorAvailabilityRequest{" +
                "doctorId=" + doctorId +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isAvailable=" + isAvailable +
                ", slotDuration=" + slotDuration +
                ", maxAppointmentsPerDay=" + maxAppointmentsPerDay +
                ", notes='" + notes + '\'' +
                ", isRecurring=" + isRecurring +
                ", breakStartTime=" + breakStartTime +
                ", breakEndTime=" + breakEndTime +
                '}';
    }
} 