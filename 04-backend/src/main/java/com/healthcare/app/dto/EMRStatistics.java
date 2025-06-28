package com.healthcare.app.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class EMRStatistics {
    
    private Long totalEMRs;
    private Long emrsThisWeek;
    private Long emrsThisMonth;
    private Long emrsThisYear;
    
    private Long emrsWithPrescriptions;
    private Long emrsWithLabOrders;
    private Long emrsWithImagingOrders;
    private Long emrsWithFollowUp;
    
    private Double averageEMRsPerDay;
    private Double averageEMRsPerWeek;
    private Double averageEMRsPerMonth;
    
    private Map<String, Long> topDiagnoses;
    private Map<String, Long> topTreatments;
    private Map<String, Long> emrsByDoctor;
    private Map<String, Long> emrsByPatient;
    
    private Long recentEMRs;
    private Long pendingFollowUps;
    private Long completedFollowUps;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Constructors
    public EMRStatistics() {}
    
    public EMRStatistics(Long totalEMRs, Long emrsThisWeek, Long emrsThisMonth, Long emrsThisYear,
                        Long emrsWithPrescriptions, Long emrsWithLabOrders, Long emrsWithImagingOrders,
                        Long emrsWithFollowUp, Double averageEMRsPerDay, Double averageEMRsPerWeek,
                        Double averageEMRsPerMonth, Map<String, Long> topDiagnoses, Map<String, Long> topTreatments,
                        Map<String, Long> emrsByDoctor, Map<String, Long> emrsByPatient, Long recentEMRs,
                        Long pendingFollowUps, Long completedFollowUps, LocalDateTime startDate, LocalDateTime endDate) {
        this.totalEMRs = totalEMRs;
        this.emrsThisWeek = emrsThisWeek;
        this.emrsThisMonth = emrsThisMonth;
        this.emrsThisYear = emrsThisYear;
        this.emrsWithPrescriptions = emrsWithPrescriptions;
        this.emrsWithLabOrders = emrsWithLabOrders;
        this.emrsWithImagingOrders = emrsWithImagingOrders;
        this.emrsWithFollowUp = emrsWithFollowUp;
        this.averageEMRsPerDay = averageEMRsPerDay;
        this.averageEMRsPerWeek = averageEMRsPerWeek;
        this.averageEMRsPerMonth = averageEMRsPerMonth;
        this.topDiagnoses = topDiagnoses;
        this.topTreatments = topTreatments;
        this.emrsByDoctor = emrsByDoctor;
        this.emrsByPatient = emrsByPatient;
        this.recentEMRs = recentEMRs;
        this.pendingFollowUps = pendingFollowUps;
        this.completedFollowUps = completedFollowUps;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public Long getTotalEMRs() {
        return totalEMRs;
    }
    
    public void setTotalEMRs(Long totalEMRs) {
        this.totalEMRs = totalEMRs;
    }
    
    public Long getEmrsThisWeek() {
        return emrsThisWeek;
    }
    
    public void setEmrsThisWeek(Long emrsThisWeek) {
        this.emrsThisWeek = emrsThisWeek;
    }
    
    public Long getEmrsThisMonth() {
        return emrsThisMonth;
    }
    
    public void setEmrsThisMonth(Long emrsThisMonth) {
        this.emrsThisMonth = emrsThisMonth;
    }
    
    public Long getEmrsThisYear() {
        return emrsThisYear;
    }
    
    public void setEmrsThisYear(Long emrsThisYear) {
        this.emrsThisYear = emrsThisYear;
    }
    
    public Long getEmrsWithPrescriptions() {
        return emrsWithPrescriptions;
    }
    
    public void setEmrsWithPrescriptions(Long emrsWithPrescriptions) {
        this.emrsWithPrescriptions = emrsWithPrescriptions;
    }
    
    public Long getEmrsWithLabOrders() {
        return emrsWithLabOrders;
    }
    
    public void setEmrsWithLabOrders(Long emrsWithLabOrders) {
        this.emrsWithLabOrders = emrsWithLabOrders;
    }
    
    public Long getEmrsWithImagingOrders() {
        return emrsWithImagingOrders;
    }
    
    public void setEmrsWithImagingOrders(Long emrsWithImagingOrders) {
        this.emrsWithImagingOrders = emrsWithImagingOrders;
    }
    
    public Long getEmrsWithFollowUp() {
        return emrsWithFollowUp;
    }
    
    public void setEmrsWithFollowUp(Long emrsWithFollowUp) {
        this.emrsWithFollowUp = emrsWithFollowUp;
    }
    
    public Double getAverageEMRsPerDay() {
        return averageEMRsPerDay;
    }
    
    public void setAverageEMRsPerDay(Double averageEMRsPerDay) {
        this.averageEMRsPerDay = averageEMRsPerDay;
    }
    
    public Double getAverageEMRsPerWeek() {
        return averageEMRsPerWeek;
    }
    
    public void setAverageEMRsPerWeek(Double averageEMRsPerWeek) {
        this.averageEMRsPerWeek = averageEMRsPerWeek;
    }
    
    public Double getAverageEMRsPerMonth() {
        return averageEMRsPerMonth;
    }
    
    public void setAverageEMRsPerMonth(Double averageEMRsPerMonth) {
        this.averageEMRsPerMonth = averageEMRsPerMonth;
    }
    
    public Map<String, Long> getTopDiagnoses() {
        return topDiagnoses;
    }
    
    public void setTopDiagnoses(Map<String, Long> topDiagnoses) {
        this.topDiagnoses = topDiagnoses;
    }
    
    public Map<String, Long> getTopTreatments() {
        return topTreatments;
    }
    
    public void setTopTreatments(Map<String, Long> topTreatments) {
        this.topTreatments = topTreatments;
    }
    
    public Map<String, Long> getEmrsByDoctor() {
        return emrsByDoctor;
    }
    
    public void setEmrsByDoctor(Map<String, Long> emrsByDoctor) {
        this.emrsByDoctor = emrsByDoctor;
    }
    
    public Map<String, Long> getEmrsByPatient() {
        return emrsByPatient;
    }
    
    public void setEmrsByPatient(Map<String, Long> emrsByPatient) {
        this.emrsByPatient = emrsByPatient;
    }
    
    public Long getRecentEMRs() {
        return recentEMRs;
    }
    
    public void setRecentEMRs(Long recentEMRs) {
        this.recentEMRs = recentEMRs;
    }
    
    public Long getPendingFollowUps() {
        return pendingFollowUps;
    }
    
    public void setPendingFollowUps(Long pendingFollowUps) {
        this.pendingFollowUps = pendingFollowUps;
    }
    
    public Long getCompletedFollowUps() {
        return completedFollowUps;
    }
    
    public void setCompletedFollowUps(Long completedFollowUps) {
        this.completedFollowUps = completedFollowUps;
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
        return "EMRStatistics{" +
                "totalEMRs=" + totalEMRs +
                ", emrsThisWeek=" + emrsThisWeek +
                ", emrsThisMonth=" + emrsThisMonth +
                ", emrsThisYear=" + emrsThisYear +
                ", emrsWithPrescriptions=" + emrsWithPrescriptions +
                ", emrsWithLabOrders=" + emrsWithLabOrders +
                ", emrsWithImagingOrders=" + emrsWithImagingOrders +
                ", emrsWithFollowUp=" + emrsWithFollowUp +
                ", averageEMRsPerDay=" + averageEMRsPerDay +
                ", averageEMRsPerWeek=" + averageEMRsPerWeek +
                ", averageEMRsPerMonth=" + averageEMRsPerMonth +
                ", topDiagnoses=" + topDiagnoses +
                ", topTreatments=" + topTreatments +
                ", emrsByDoctor=" + emrsByDoctor +
                ", emrsByPatient=" + emrsByPatient +
                ", recentEMRs=" + recentEMRs +
                ", pendingFollowUps=" + pendingFollowUps +
                ", completedFollowUps=" + completedFollowUps +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
} 