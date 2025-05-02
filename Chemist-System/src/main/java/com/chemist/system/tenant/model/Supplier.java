package com.chemist.system.tenant.model;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key

    @Column(nullable = false)
    private String name;               // Supplier company name

    @Column(nullable = false)
    private String contact;            // Contact person

    @Column(nullable = false, unique = true)
    private String email;              // Business email

    @Column(columnDefinition = "JSON")
    private String drugsSupplied;      // JSON array of drug IDs they supply
}
