package com.chemist.system.tenant.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chemists")
public class Chemist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chemistId;                   // Primary key

    @Column(nullable = false, unique = true)
    private String chemistName;
    @Column(nullable = false, unique = true)
    private String chemistOwnerName;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String address;
    @Column(nullable = false, unique = true)
    private String chemistCode;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false)
    private String Location;
    private boolean activated;

    private  String dbUrl;

    public Boolean getActivated() {

        return activated;
    }
}
