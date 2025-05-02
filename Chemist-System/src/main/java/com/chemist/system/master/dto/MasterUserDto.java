package com.chemist.system.master.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Data
@Builder
public class MasterUserDto {


    private Long id;
    private String phoneNo;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String username;
    private boolean active;
    private Set<String> userTypes = new HashSet<>();
}
