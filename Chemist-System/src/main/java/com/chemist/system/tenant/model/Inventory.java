package com.chemist.system.tenant.model;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;                 // Links to Drug entity

    @Column(nullable = false)
    private int quantity;              // Current stock count

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;      // Batch expiry (e.g., "2025-12-31")

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;        // Supplier's batch ID (e.g., "BATCH-2023-XYZ")

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;         // Links to Supplier
}
