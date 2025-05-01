package com.chemist.system.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Data
@Entity
@Table(name = "master_users")
public class MasterUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNo;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean active;
    private Set<String> userTypes = new HashSet<>();
}
