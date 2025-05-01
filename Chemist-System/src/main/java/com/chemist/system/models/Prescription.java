package com.chemist.system.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prescriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private String doctorId;

    @Column(columnDefinition = "JSON")
    private String drugs;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;
}