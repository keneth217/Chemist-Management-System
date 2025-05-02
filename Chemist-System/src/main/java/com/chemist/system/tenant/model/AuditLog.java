package com.chemist.system.tenant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key

    @Column(nullable = false)
    private String action;             // E.g., "CONTROLLED_SUBSTANCE_SALE"

    @ManyToOne
    @JoinColumn(name = "drug_id")
    private Drug drug;                // Links to Drug

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;                // Staff who performed the action

    @Column(nullable = false)
    private LocalDateTime timestamp;  // When the action occurred

    @Column(columnDefinition = "TEXT")
    private String details;           // Additional context (e.g., "Sold 2 units of Codeine")
}
