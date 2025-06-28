package com.healthcare.app.dto;

import java.time.LocalDateTime;

public class AppointmentStatistics {
    
    private Long totalAppointments;
    private Long scheduledAppointments;
    private Long confirmedAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Long noShowAppointments;
    private Long urgentAppointments;
    private Long followUpAppointments;
    
    private Double averageAppointmentDuration;
    private Long totalAppointmentHours;
    
    private Long appointmentsToday;
    private Long appointmentsThisWeek;
    private Long appointmentsThisMonth;
    
    private Double completionRate;
    private Double cancellationRate;
    private Double noShowRate;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Constructors
    public AppointmentStatistics() {}
    
    public AppointmentStatistics(Long totalAppointments, Long scheduledAppointments, Long confirmedAppointments,
                               Long completedAppointments, Long cancelledAppointments, Long noShowAppointments,
                               Long urgentAppointments, Long followUpAppointments, Double averageAppointmentDuration,
                               Long totalAppointmentHours, Long appointmentsToday, Long appointmentsThisWeek,
                               Long appointmentsThisMonth, Double completionRate, Double cancellationRate,
                               Double noShowRate, LocalDateTime startDate, LocalDateTime endDate) {
        this.totalAppointments = totalAppointments;
        this.scheduledAppointments = scheduledAppointments;
        this.confirmedAppointments = confirmedAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.noShowAppointments = noShowAppointments;
        this.urgentAppointments = urgentAppointments;
        this.followUpAppointments = followUpAppointments;
        this.averageAppointmentDuration = averageAppointmentDuration;
        this.totalAppointmentHours = totalAppointmentHours;
        this.appointmentsToday = appointmentsToday;
        this.appointmentsThisWeek = appointmentsThisWeek;
        this.appointmentsThisMonth = appointmentsThisMonth;
        this.completionRate = completionRate;
        this.cancellationRate = cancellationRate;
        this.noShowRate = noShowRate;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public Long getTotalAppointments() {
        return totalAppointments;
    }
    
    public void setTotalAppointments(Long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }
    
    public Long getScheduledAppointments() {
        return scheduledAppointments;
    }
    
    public void setScheduledAppointments(Long scheduledAppointments) {
        this.scheduledAppointments = scheduledAppointments;
    }
    
    public Long getConfirmedAppointments() {
        return confirmedAppointments;
    }
    
    public void setConfirmedAppointments(Long confirmedAppointments) {
        this.confirmedAppointments = confirmedAppointments;
    }
    
    public Long getCompletedAppointments() {
        return completedAppointments;
    }
    
    public void setCompletedAppointments(Long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }
    
    public Long getCancelledAppointments() {
        return cancelledAppointments;
    }
    
    public void setCancelledAppointments(Long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }
    
    public Long getNoShowAppointments() {
        return noShowAppointments;
    }
    
    public void setNoShowAppointments(Long noShowAppointments) {
        this.noShowAppointments = noShowAppointments;
    }
    
    public Long getUrgentAppointments() {
        return urgentAppointments;
    }
    
    public void setUrgentAppointments(Long urgentAppointments) {
        this.urgentAppointments = urgentAppointments;
    }
    
    public Long getFollowUpAppointments() {
        return followUpAppointments;
    }
    
    public void setFollowUpAppointments(Long followUpAppointments) {
        this.followUpAppointments = followUpAppointments;
    }
    
    public Double getAverageAppointmentDuration() {
        return averageAppointmentDuration;
    }
    
    public void setAverageAppointmentDuration(Double averageAppointmentDuration) {
        this.averageAppointmentDuration = averageAppointmentDuration;
    }
    
    public Long getTotalAppointmentHours() {
        return totalAppointmentHours;
    }
    
    public void setTotalAppointmentHours(Long totalAppointmentHours) {
        this.totalAppointmentHours = totalAppointmentHours;
    }
    
    public Long getAppointmentsToday() {
        return appointmentsToday;
    }
    
    public void setAppointmentsToday(Long appointmentsToday) {
        this.appointmentsToday = appointmentsToday;
    }
    
    public Long getAppointmentsThisWeek() {
        return appointmentsThisWeek;
    }
    
    public void setAppointmentsThisWeek(Long appointmentsThisWeek) {
        this.appointmentsThisWeek = appointmentsThisWeek;
    }
    
    public Long getAppointmentsThisMonth() {
        return appointmentsThisMonth;
    }
    
    public void setAppointmentsThisMonth(Long appointmentsThisMonth) {
        this.appointmentsThisMonth = appointmentsThisMonth;
    }
    
    public Double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }
    
    public Double getCancellationRate() {
        return cancellationRate;
    }
    
    public void setCancellationRate(Double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }
    
    public Double getNoShowRate() {
        return noShowRate;
    }
    
    public void setNoShowRate(Double noShowRate) {
        this.noShowRate = noShowRate;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    @Override
    public String toString() {
        return "AppointmentStatistics{" +
                "totalAppointments=" + totalAppointments +
                ", scheduledAppointments=" + scheduledAppointments +
                ", confirmedAppointments=" + confirmedAppointments +
                ", completedAppointments=" + completedAppointments +
                ", cancelledAppointments=" + cancelledAppointments +
                ", noShowAppointments=" + noShowAppointments +
                ", urgentAppointments=" + urgentAppointments +
                ", followUpAppointments=" + followUpAppointments +
                ", averageAppointmentDuration=" + averageAppointmentDuration +
                ", totalAppointmentHours=" + totalAppointmentHours +
                ", appointmentsToday=" + appointmentsToday +
                ", appointmentsThisWeek=" + appointmentsThisWeek +
                ", appointmentsThisMonth=" + appointmentsThisMonth +
                ", completionRate=" + completionRate +
                ", cancellationRate=" + cancellationRate +
                ", noShowRate=" + noShowRate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
} 