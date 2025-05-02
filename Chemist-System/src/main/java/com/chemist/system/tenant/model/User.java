package com.chemist.system.tenant.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "chemist_id")
    private Long chemistId;

    @Column(name = "chemist_name")
    private String chemistName;

    @ElementCollection
    @CollectionTable(name = "user_types", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "type")
    @Builder.Default
    private Set<String> userTypes = new HashSet<>();

    private boolean isActive;
}