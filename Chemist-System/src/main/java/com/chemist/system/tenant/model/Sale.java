package com.chemist.system.tenant.model;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                   // Primary key

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;        // Optional (anonymous sales allowed)

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;       // Final amount after discounts

    @Column(columnDefinition = "JSON")
    private String items;             // JSON array: [{ drugId: 1, quantity: 2, price: 10.99 }]

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // Enum: CASH, CARD, UPI

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;   // Timestamp of transaction
}
