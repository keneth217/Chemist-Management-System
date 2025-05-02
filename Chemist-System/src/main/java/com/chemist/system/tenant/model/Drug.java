package com.chemist.system.tenant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "drugs")
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key

    @Column(nullable = false, unique = true)
    private String name;               // Brand name (e.g., "Paracetamol")

    @Enumerated(EnumType.STRING)
    private DrugType type;             // Enum: OTC, PRESCRIPTION, CONTROLLED

    @Column(nullable = false)
    private double price;              // Retail price per unit

    @Column(nullable = false)
    private String manufacturer;       // Company name (e.g., "Pfizer")

    @Column(name = "requires_prescription", nullable = false)
    private boolean requiresPrescription; // True for prescription-only drugs

    @Column(columnDefinition = "TEXT")
    private String sideEffects;        // Common side effects (e.g., "Drowsiness")
}