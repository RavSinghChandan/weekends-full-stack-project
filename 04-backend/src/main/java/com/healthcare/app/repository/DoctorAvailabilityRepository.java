package com.healthcare.app.repository;

import com.healthcare.app.entity.DoctorAvailability;
import com.healthcare.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    
    List<DoctorAvailability> findByDoctor(User doctor);
    
    List<DoctorAvailability> findByDoctorAndIsAvailableTrue(User doctor);
    
    List<DoctorAvailability> findByDoctorAndDayOfWeek(User doctor, DayOfWeek dayOfWeek);
    
    List<DoctorAvailability> findByDoctorAndDayOfWeekAndIsAvailableTrue(User doctor, DayOfWeek dayOfWeek);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.isAvailable = true ORDER BY da.startTime")
    List<DoctorAvailability> findAvailableSlotsByDoctorAndDay(@Param("doctor") User doctor, @Param("dayOfWeek") DayOfWeek dayOfWeek);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :time AND da.endTime > :time AND da.isAvailable = true")
    Optional<DoctorAvailability> findAvailabilityAtTime(@Param("doctor") User doctor, 
                                                       @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                       @Param("time") LocalTime time);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :startTime AND da.endTime >= :endTime AND da.isAvailable = true")
    List<DoctorAvailability> findAvailabilityForTimeRange(@Param("doctor") User doctor, 
                                                         @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                         @Param("startTime") LocalTime startTime, 
                                                         @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :startTime AND da.endTime > :startTime AND da.isAvailable = true")
    List<DoctorAvailability> findAvailabilityStartingAt(@Param("doctor") User doctor, 
                                                       @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                       @Param("startTime") LocalTime startTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime < :endTime AND da.endTime >= :endTime AND da.isAvailable = true")
    List<DoctorAvailability> findAvailabilityEndingAt(@Param("doctor") User doctor, 
                                                     @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                     @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime >= :startTime AND da.endTime <= :endTime AND da.isAvailable = true")
    List<DoctorAvailability> findAvailabilityWithinTimeRange(@Param("doctor") User doctor, 
                                                            @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                            @Param("startTime") LocalTime startTime, 
                                                            @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :appointmentTime AND da.endTime > :appointmentTime AND da.isAvailable = true")
    Optional<DoctorAvailability> findAvailabilityForAppointmentTime(@Param("doctor") User doctor, 
                                                                   @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                   @Param("appointmentTime") LocalTime appointmentTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :startTime AND da.endTime >= :endTime AND da.isAvailable = true AND da.id != :excludeId")
    List<DoctorAvailability> findAvailabilityForTimeRangeExcluding(@Param("doctor") User doctor, 
                                                                  @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                  @Param("startTime") LocalTime startTime, 
                                                                  @Param("endTime") LocalTime endTime,
                                                                  @Param("excludeId") Long excludeId);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :startTime AND da.endTime > :startTime AND da.isAvailable = true ORDER BY da.startTime")
    List<DoctorAvailability> findAvailableSlotsStartingFrom(@Param("doctor") User doctor, 
                                                           @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                           @Param("startTime") LocalTime startTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime < :endTime AND da.endTime >= :endTime AND da.isAvailable = true ORDER BY da.startTime")
    List<DoctorAvailability> findAvailableSlotsEndingAt(@Param("doctor") User doctor, 
                                                       @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                       @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime >= :startTime AND da.endTime <= :endTime AND da.isAvailable = true ORDER BY da.startTime")
    List<DoctorAvailability> findAvailableSlotsWithinRange(@Param("doctor") User doctor, 
                                                          @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                          @Param("startTime") LocalTime startTime, 
                                                          @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :time AND da.endTime > :time AND da.isAvailable = true AND da.breakStartTime IS NULL OR da.breakStartTime > :time OR da.breakEndTime <= :time")
    Optional<DoctorAvailability> findAvailabilityAtTimeExcludingBreaks(@Param("doctor") User doctor, 
                                                                      @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                      @Param("time") LocalTime time);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :startTime AND da.endTime >= :endTime AND da.isAvailable = true AND (da.breakStartTime IS NULL OR da.breakStartTime >= :endTime OR da.breakEndTime <= :startTime)")
    List<DoctorAvailability> findAvailabilityForTimeRangeExcludingBreaks(@Param("doctor") User doctor, 
                                                                        @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                        @Param("startTime") LocalTime startTime, 
                                                                        @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime <= :startTime AND da.endTime > :startTime AND da.isAvailable = true AND (da.breakStartTime IS NULL OR da.breakStartTime > :startTime)")
    List<DoctorAvailability> findAvailableSlotsStartingFromExcludingBreaks(@Param("doctor") User doctor, 
                                                                          @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                          @Param("startTime") LocalTime startTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime < :endTime AND da.endTime >= :endTime AND da.isAvailable = true AND (da.breakEndTime IS NULL OR da.breakEndTime < :endTime)")
    List<DoctorAvailability> findAvailableSlotsEndingAtExcludingBreaks(@Param("doctor") User doctor, 
                                                                      @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                      @Param("endTime") LocalTime endTime);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor AND da.dayOfWeek = :dayOfWeek AND da.startTime >= :startTime AND da.endTime <= :endTime AND da.isAvailable = true AND (da.breakStartTime IS NULL OR da.breakStartTime >= :endTime OR da.breakEndTime <= :startTime)")
    List<DoctorAvailability> findAvailableSlotsWithinRangeExcludingBreaks(@Param("doctor") User doctor, 
                                                                         @Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                                         @Param("startTime") LocalTime startTime, 
                                                                         @Param("endTime") LocalTime endTime);
    
    Optional<DoctorAvailability> findByIdAndDoctor(Long id, User doctor);
} 