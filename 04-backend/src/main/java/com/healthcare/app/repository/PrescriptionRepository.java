package com.healthcare.app.repository;

import com.healthcare.app.entity.Prescription;
import com.healthcare.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    
    List<Prescription> findByPatient(User patient);
    
    List<Prescription> findByDoctor(User doctor);
    
    List<Prescription> findByPatientOrderByPrescriptionDateDesc(User patient);
    
    List<Prescription> findByDoctorOrderByPrescriptionDateDesc(User doctor);
    
    List<Prescription> findByPatientAndPrescriptionDateBetween(User patient, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Prescription> findByDoctorAndPrescriptionDateBetween(User doctor, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Prescription> findByPatientAndStatus(User patient, Prescription.PrescriptionStatus status);
    
    List<Prescription> findByDoctorAndStatus(User doctor, Prescription.PrescriptionStatus status);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.prescriptionDate >= :startDate AND p.prescriptionDate < :endDate ORDER BY p.prescriptionDate DESC")
    List<Prescription> findPatientPrescriptionsInDateRange(@Param("patient") User patient, 
                                                          @Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor AND p.prescriptionDate >= :startDate AND p.prescriptionDate < :endDate ORDER BY p.prescriptionDate DESC")
    List<Prescription> findDoctorPrescriptionsInDateRange(@Param("doctor") User doctor, 
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.medicationName LIKE %:medicationName% ORDER BY p.prescriptionDate DESC")
    List<Prescription> findPatientPrescriptionsByMedication(@Param("patient") User patient, @Param("medicationName") String medicationName);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor AND p.medicationName LIKE %:medicationName% ORDER BY p.prescriptionDate DESC")
    List<Prescription> findDoctorPrescriptionsByMedication(@Param("doctor") User doctor, @Param("medicationName") String medicationName);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.status = 'ACTIVE' ORDER BY p.prescriptionDate DESC")
    List<Prescription> findActivePatientPrescriptions(@Param("patient") User patient);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor AND p.status = 'ACTIVE' ORDER BY p.prescriptionDate DESC")
    List<Prescription> findActiveDoctorPrescriptions(@Param("doctor") User doctor);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.isControlledSubstance = true ORDER BY p.prescriptionDate DESC")
    List<Prescription> findControlledSubstancePrescriptions(@Param("patient") User patient);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor AND p.isControlledSubstance = true ORDER BY p.prescriptionDate DESC")
    List<Prescription> findDoctorControlledSubstancePrescriptions(@Param("doctor") User doctor);
    
    @Query("SELECT p FROM Prescription p WHERE p.appointment = :appointmentId")
    List<Prescription> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.expiryDate >= :currentDate AND p.status = 'ACTIVE' ORDER BY p.expiryDate")
    List<Prescription> findActivePrescriptionsNotExpired(@Param("patient") User patient, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.expiryDate < :currentDate AND p.status = 'ACTIVE' ORDER BY p.expiryDate DESC")
    List<Prescription> findExpiredActivePrescriptions(@Param("patient") User patient, @Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.refillsUsed < p.refillsAllowed AND p.status = 'ACTIVE' ORDER BY p.prescriptionDate DESC")
    List<Prescription> findPrescriptionsWithRefillsAvailable(@Param("patient") User patient);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND p.filledDate >= :startDate AND p.filledDate < :endDate ORDER BY p.filledDate DESC")
    List<Prescription> findFilledPrescriptionsInDateRange(@Param("patient") User patient, 
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.patient = :patient AND p.prescriptionDate >= :startDate AND p.prescriptionDate < :endDate")
    long countPatientPrescriptionsInDateRange(@Param("patient") User patient, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.doctor = :doctor AND p.prescriptionDate >= :startDate AND p.prescriptionDate < :endDate")
    long countDoctorPrescriptionsInDateRange(@Param("doctor") User doctor, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient = :patient AND (p.medicationName LIKE %:searchTerm% OR p.diagnosis LIKE %:searchTerm% OR p.instructions LIKE %:searchTerm%) ORDER BY p.prescriptionDate DESC")
    Page<Prescription> searchPatientPrescriptions(@Param("patient") User patient, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor = :doctor AND (p.medicationName LIKE %:searchTerm% OR p.diagnosis LIKE %:searchTerm% OR p.instructions LIKE %:searchTerm%) ORDER BY p.prescriptionDate DESC")
    Page<Prescription> searchDoctorPrescriptions(@Param("doctor") User doctor, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT DISTINCT p.medicationName FROM Prescription p WHERE p.patient = :patient ORDER BY p.medicationName")
    List<String> findDistinctMedicationsByPatient(@Param("patient") User patient);
    
    @Query("SELECT DISTINCT p.medicationName FROM Prescription p WHERE p.doctor = :doctor ORDER BY p.medicationName")
    List<String> findDistinctMedicationsByDoctor(@Param("doctor") User doctor);
    
    Optional<Prescription> findByIdAndPatient(Long id, User patient);
    
    Optional<Prescription> findByIdAndDoctor(Long id, User doctor);
} 