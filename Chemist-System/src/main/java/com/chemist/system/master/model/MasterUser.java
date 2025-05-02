package com.chemist.system.master.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "master_users")
public class MasterUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNo;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean active;
    private Set<String> userTypes = new HashSet<>();
}
