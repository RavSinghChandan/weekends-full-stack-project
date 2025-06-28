package com.healthcare.app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PatientMedicalHistory {
    
    private Long patientId;
    private String patientName;
    private Long totalEMRs;
    private LocalDateTime firstEMRDate;
    private LocalDateTime lastEMRDate;
    
    private List<String> allDiagnoses;
    private List<String> allTreatments;
    private List<String> allPrescriptions;
    private List<String> allAllergies;
    private List<String> allMedications;
    
    private Long totalAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Long noShowAppointments;
    
    private Double averageAppointmentDuration;
    private Long totalAppointmentHours;
    
    private Long urgentAppointments;
    private Long followUpAppointments;
    
    private String insuranceProvider;
    private String insuranceNumber;
    private String emergencyContact;
    private String emergencyPhone;
    
    private String medicalHistory;
    private String currentMedications;
    private String allergies;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PatientMedicalHistory() {}
    
    public PatientMedicalHistory(Long patientId, String patientName, Long totalEMRs, LocalDateTime firstEMRDate,
                               LocalDateTime lastEMRDate, List<String> allDiagnoses, List<String> allTreatments,
                               List<String> allPrescriptions, List<String> allAllergies, List<String> allMedications,
                               Long totalAppointments, Long completedAppointments, Long cancelledAppointments,
                               Long noShowAppointments, Double averageAppointmentDuration, Long totalAppointmentHours,
                               Long urgentAppointments, Long followUpAppointments, String insuranceProvider,
                               String insuranceNumber, String emergencyContact, String emergencyPhone,
                               String medicalHistory, String currentMedications, String allergies,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.totalEMRs = totalEMRs;
        this.firstEMRDate = firstEMRDate;
        this.lastEMRDate = lastEMRDate;
        this.allDiagnoses = allDiagnoses;
        this.allTreatments = allTreatments;
        this.allPrescriptions = allPrescriptions;
        this.allAllergies = allAllergies;
        this.allMedications = allMedications;
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.noShowAppointments = noShowAppointments;
        this.averageAppointmentDuration = averageAppointmentDuration;
        this.totalAppointmentHours = totalAppointmentHours;
        this.urgentAppointments = urgentAppointments;
        this.followUpAppointments = followUpAppointments;
        this.insuranceProvider = insuranceProvider;
        this.insuranceNumber = insuranceNumber;
        this.emergencyContact = emergencyContact;
        this.emergencyPhone = emergencyPhone;
        this.medicalHistory = medicalHistory;
        this.currentMedications = currentMedications;
        this.allergies = allergies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
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
    
    public Long getTotalEMRs() {
        return totalEMRs;
    }
    
    public void setTotalEMRs(Long totalEMRs) {
        this.totalEMRs = totalEMRs;
    }
    
    public LocalDateTime getFirstEMRDate() {
        return firstEMRDate;
    }
    
    public void setFirstEMRDate(LocalDateTime firstEMRDate) {
        this.firstEMRDate = firstEMRDate;
    }
    
    public LocalDateTime getLastEMRDate() {
        return lastEMRDate;
    }
    
    public void setLastEMRDate(LocalDateTime lastEMRDate) {
        this.lastEMRDate = lastEMRDate;
    }
    
    public List<String> getAllDiagnoses() {
        return allDiagnoses;
    }
    
    public void setAllDiagnoses(List<String> allDiagnoses) {
        this.allDiagnoses = allDiagnoses;
    }
    
    public List<String> getAllTreatments() {
        return allTreatments;
    }
    
    public void setAllTreatments(List<String> allTreatments) {
        this.allTreatments = allTreatments;
    }
    
    public List<String> getAllPrescriptions() {
        return allPrescriptions;
    }
    
    public void setAllPrescriptions(List<String> allPrescriptions) {
        this.allPrescriptions = allPrescriptions;
    }
    
    public List<String> getAllAllergies() {
        return allAllergies;
    }
    
    public void setAllAllergies(List<String> allAllergies) {
        this.allAllergies = allAllergies;
    }
    
    public List<String> getAllMedications() {
        return allMedications;
    }
    
    public void setAllMedications(List<String> allMedications) {
        this.allMedications = allMedications;
    }
    
    public Long getTotalAppointments() {
        return totalAppointments;
    }
    
    public void setTotalAppointments(Long totalAppointments) {
        this.totalAppointments = totalAppointments;
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
    
    public String getInsuranceProvider() {
        return insuranceProvider;
    }
    
    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }
    
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getEmergencyPhone() {
        return emergencyPhone;
    }
    
    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getCurrentMedications() {
        return currentMedications;
    }
    
    public void setCurrentMedications(String currentMedications) {
        this.currentMedications = currentMedications;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
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
    
    @Override
    public String toString() {
        return "PatientMedicalHistory{" +
                "patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", totalEMRs=" + totalEMRs +
                ", firstEMRDate=" + firstEMRDate +
                ", lastEMRDate=" + lastEMRDate +
                ", allDiagnoses=" + allDiagnoses +
                ", allTreatments=" + allTreatments +
                ", allPrescriptions=" + allPrescriptions +
                ", allAllergies=" + allAllergies +
                ", allMedications=" + allMedications +
                ", totalAppointments=" + totalAppointments +
                ", completedAppointments=" + completedAppointments +
                ", cancelledAppointments=" + cancelledAppointments +
                ", noShowAppointments=" + noShowAppointments +
                ", averageAppointmentDuration=" + averageAppointmentDuration +
                ", totalAppointmentHours=" + totalAppointmentHours +
                ", urgentAppointments=" + urgentAppointments +
                ", followUpAppointments=" + followUpAppointments +
                ", insuranceProvider='" + insuranceProvider + '\'' +
                ", insuranceNumber='" + insuranceNumber + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", emergencyPhone='" + emergencyPhone + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", currentMedications='" + currentMedications + '\'' +
                ", allergies='" + allergies + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 