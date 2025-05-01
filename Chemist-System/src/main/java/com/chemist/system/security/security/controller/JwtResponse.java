package com.chemist.system.security.security.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String newToken;
    private String newRefreshToken;
    private String bearer;
    private Long id;
    private String username;
    private String phoneNo;
    private String accountNo;
    private Object chemist;
    private List<String> roles;
    private String chemistId;
}