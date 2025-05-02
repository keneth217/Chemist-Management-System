package com.chemist.system.master.dto;

import lombok.Builder;
import lombok.Data;


import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String userId;
    private String username;
    private String role;
}
