package com.healthcare.app.repository;

import com.healthcare.app.entity.Appointment;
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
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByDoctor(User doctor);
    
    List<Appointment> findByPatient(User patient);
    
    List<Appointment> findByDoctorAndAppointmentDateBetween(User doctor, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Appointment> findByPatientAndAppointmentDateBetween(User patient, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Appointment> findByDoctorAndStatus(User doctor, Appointment.AppointmentStatus status);
    
    List<Appointment> findByPatientAndStatus(User patient, Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate >= :startDate AND a.appointmentDate < :endDate")
    List<Appointment> findDoctorAppointmentsInDateRange(@Param("doctor") User doctor, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.appointmentDate >= :startDate AND a.appointmentDate < :endDate")
    List<Appointment> findPatientAppointmentsInDateRange(@Param("patient") User patient, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate >= :today AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingDoctorAppointments(@Param("doctor") User doctor, @Param("today") LocalDateTime today);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.appointmentDate >= :today AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingPatientAppointments(@Param("patient") User patient, @Param("today") LocalDateTime today);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND DATE(a.appointmentDate) = DATE(:date)")
    List<Appointment> findDoctorAppointmentsByDate(@Param("doctor") User doctor, @Param("date") LocalDateTime date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND DATE(a.appointmentDate) = DATE(:date)")
    List<Appointment> findPatientAppointmentsByDate(@Param("patient") User patient, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate BETWEEN :startTime AND :endTime AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    long countConflictingAppointments(@Param("doctor") User doctor, 
                                     @Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate BETWEEN :startTime AND :endTime AND a.id != :excludeAppointmentId AND a.status IN ('SCHEDULED', 'CONFIRMED')")
    List<Appointment> findConflictingAppointmentsExcluding(@Param("doctor") User doctor, 
                                                          @Param("startTime") LocalDateTime startTime, 
                                                          @Param("endTime") LocalDateTime endTime,
                                                          @Param("excludeAppointmentId") Long excludeAppointmentId);
    
    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND a.appointmentDate >= :startDate AND a.appointmentDate < :endDate")
    Page<Appointment> findAppointmentsByStatusAndDateRange(@Param("status") Appointment.AppointmentStatus status,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate,
                                                          Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'NO_SHOW' AND a.appointmentDate >= :startDate AND a.appointmentDate < :endDate")
    long countNoShowsByDoctor(@Param("doctor") User doctor, 
                             @Param("startDate") LocalDateTime startDate, 
                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status AND a.appointmentDate >= :startDate AND a.appointmentDate < :endDate")
    long countAppointmentsByStatus(@Param("status") Appointment.AppointmentStatus status,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.isUrgent = true AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.appointmentDate")
    List<Appointment> findUrgentAppointments();
    
    @Query("SELECT a FROM Appointment a WHERE a.isFollowUp = true AND a.followUpAppointmentId = :originalAppointmentId")
    List<Appointment> findFollowUpAppointments(@Param("originalAppointmentId") Long originalAppointmentId);
    
    Optional<Appointment> findByIdAndDoctor(Long id, User doctor);
    
    Optional<Appointment> findByIdAndPatient(Long id, User patient);
} 