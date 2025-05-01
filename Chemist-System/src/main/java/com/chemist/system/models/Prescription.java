package com.chemist.system.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key (auto-increment)

    @Column(nullable = false)
    private String customerId;         // Links to Customer (e.g., "cust-123")

    @Column(nullable = false)
    private String doctorId;           // Doctor's license number

    @Column(columnDefinition = "JSON")
    private String drugs;              // JSON array: [{ drugId: 1, dosage: "500mg", duration: "7 days" }]

    @Column(nullable = false)
    private LocalDate date;            // Prescription issue date

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status; // Enum: PENDING, FULFILLED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String notes;              // Doctor's instructions (e.g., "Take after meals")
}
