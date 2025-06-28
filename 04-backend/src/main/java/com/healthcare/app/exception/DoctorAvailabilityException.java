package com.healthcare.app.exception;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DoctorAvailabilityException extends RuntimeException {
    
    private Long doctorId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String availabilityType;
    
    public DoctorAvailabilityException(String message) {
        super(message);
    }
    
    public DoctorAvailabilityException(String message, Long doctorId, DayOfWeek dayOfWeek, 
                                     LocalTime startTime, LocalTime endTime, String availabilityType) {
        super(message);
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.availabilityType = availabilityType;
    }
    
    public DoctorAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DoctorAvailabilityException(String message, Long doctorId, String availabilityType) {
        super(message);
        this.doctorId = doctorId;
        this.availabilityType = availabilityType;
    }
    
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
    
    public String getAvailabilityType() {
        return availabilityType;
    }
    
    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }
} 