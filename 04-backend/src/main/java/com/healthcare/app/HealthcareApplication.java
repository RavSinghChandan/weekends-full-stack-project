package com.healthcare.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application for Healthcare Appointment & EMR System
 * 
 * Features:
 * - Appointment booking and management
 * - Electronic Medical Records (EMR)
 * - Prescription management
 * - Doctor availability calendar
 * - Role-based access control (Admin, Doctor, Patient)
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableScheduling
public class HealthcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcareApplication.class, args);
    }
} 