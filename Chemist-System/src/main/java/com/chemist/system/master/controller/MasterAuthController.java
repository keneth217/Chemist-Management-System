package com.chemist.system.master.controller;

import com.chemist.system.master.dto.AuthenticationRequest;
import com.chemist.system.master.dto.AuthenticationResponse;

import com.chemist.system.master.service.MasterAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterAuthController {

    private final MasterAuthenticationService masterAuthenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> masterLogin(
            @RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(
                masterAuthenticationService.authenticate(authenticationRequest)
        );
    }
}