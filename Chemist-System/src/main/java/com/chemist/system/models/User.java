package com.chemist.system.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Data
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNo;
    @Enumerated(EnumType.STRING)
    private ERole role;
    @Column(name = "chemist_id")
    private String chemistId;
    @Column(name = "chemist_name")
    private String chemistName;
    private Set<String> userTypes = new HashSet<>();
    private Set<Role> roles = new HashSet<>();
    private boolean isActive;
}