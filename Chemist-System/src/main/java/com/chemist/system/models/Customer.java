package com.chemist.system.models;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key

    @Column(nullable = false)
    private String name;               // Full name

    @Column(nullable = false, unique = true)
    private String phone;              // Contact number

    @Column(unique = true)
    private String email;              // Optional email

    @Column(columnDefinition = "JSON")
    private String allergies;          // JSON array: ["Penicillin", "Sulfa"]

    @Column(name = "loyalty_points", columnDefinition = "INT DEFAULT 0")
    private int loyaltyPoints;         // Rewards points

    @OneToMany(mappedBy = "customer")
    private List<Prescription> prescriptions; // JPA relationship
}