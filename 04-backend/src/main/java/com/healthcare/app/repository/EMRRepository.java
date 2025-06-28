package com.healthcare.app.repository;

import com.healthcare.app.entity.EMR;
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
public interface EMRRepository extends JpaRepository<EMR, Long> {
    
    List<EMR> findByPatient(User patient);
    
    List<EMR> findByDoctor(User doctor);
    
    List<EMR> findByPatientOrderByVisitDateDesc(User patient);
    
    List<EMR> findByDoctorOrderByVisitDateDesc(User doctor);
    
    List<EMR> findByPatientAndVisitDateBetween(User patient, LocalDateTime startDate, LocalDateTime endDate);
    
    List<EMR> findByDoctorAndVisitDateBetween(User doctor, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND e.visitDate >= :startDate AND e.visitDate < :endDate ORDER BY e.visitDate DESC")
    List<EMR> findPatientEMRInDateRange(@Param("patient") User patient, 
                                       @Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM EMR e WHERE e.doctor = :doctor AND e.visitDate >= :startDate AND e.visitDate < :endDate ORDER BY e.visitDate DESC")
    List<EMR> findDoctorEMRInDateRange(@Param("doctor") User doctor, 
                                      @Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND e.diagnosis LIKE %:diagnosis% ORDER BY e.visitDate DESC")
    List<EMR> findPatientEMRByDiagnosis(@Param("patient") User patient, @Param("diagnosis") String diagnosis);
    
    @Query("SELECT e FROM EMR e WHERE e.doctor = :doctor AND e.diagnosis LIKE %:diagnosis% ORDER BY e.visitDate DESC")
    List<EMR> findDoctorEMRByDiagnosis(@Param("doctor") User doctor, @Param("diagnosis") String diagnosis);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND e.isConfidential = false ORDER BY e.visitDate DESC")
    List<EMR> findNonConfidentialPatientEMR(@Param("patient") User patient);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND e.doctor = :doctor ORDER BY e.visitDate DESC")
    List<EMR> findPatientEMRByDoctor(@Param("patient") User patient, @Param("doctor") User doctor);
    
    @Query("SELECT e FROM EMR e WHERE e.appointment = :appointmentId")
    Optional<EMR> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND e.visitDate = (SELECT MAX(e2.visitDate) FROM EMR e2 WHERE e2.patient = :patient)")
    Optional<EMR> findLatestPatientEMR(@Param("patient") User patient);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND e.visitDate >= :date ORDER BY e.visitDate DESC")
    List<EMR> findPatientEMRFromDate(@Param("patient") User patient, @Param("date") LocalDateTime date);
    
    @Query("SELECT e FROM EMR e WHERE e.doctor = :doctor AND e.visitDate >= :date ORDER BY e.visitDate DESC")
    List<EMR> findDoctorEMRFromDate(@Param("doctor") User doctor, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(e) FROM EMR e WHERE e.patient = :patient AND e.visitDate >= :startDate AND e.visitDate < :endDate")
    long countPatientEMRInDateRange(@Param("patient") User patient, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM EMR e WHERE e.doctor = :doctor AND e.visitDate >= :startDate AND e.visitDate < :endDate")
    long countDoctorEMRInDateRange(@Param("doctor") User doctor, 
                                  @Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM EMR e WHERE e.patient = :patient AND (e.chiefComplaint LIKE %:searchTerm% OR e.diagnosis LIKE %:searchTerm% OR e.treatmentPlan LIKE %:searchTerm%) ORDER BY e.visitDate DESC")
    Page<EMR> searchPatientEMR(@Param("patient") User patient, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT e FROM EMR e WHERE e.doctor = :doctor AND (e.chiefComplaint LIKE %:searchTerm% OR e.diagnosis LIKE %:searchTerm% OR e.treatmentPlan LIKE %:searchTerm%) ORDER BY e.visitDate DESC")
    Page<EMR> searchDoctorEMR(@Param("doctor") User doctor, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT DISTINCT e.diagnosis FROM EMR e WHERE e.patient = :patient ORDER BY e.diagnosis")
    List<String> findDistinctDiagnosesByPatient(@Param("patient") User patient);
    
    @Query("SELECT DISTINCT e.diagnosis FROM EMR e WHERE e.doctor = :doctor ORDER BY e.diagnosis")
    List<String> findDistinctDiagnosesByDoctor(@Param("doctor") User doctor);
    
    Optional<EMR> findByIdAndPatient(Long id, User patient);
    
    Optional<EMR> findByIdAndDoctor(Long id, User doctor);
} 