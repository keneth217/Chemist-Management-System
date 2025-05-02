package com.chemist.system.master.controller;

import com.chemist.system.master.dto.AuthenticationResponse;
import com.chemist.system.master.dto.RegisterMasterUserRequest;
import com.chemist.system.master.service.MasterUserService;
import com.chemist.system.security.security.jwt.JwtUtils;
import com.chemist.system.tenant.dto.ChemistProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterUserController {

    private final MasterUserService masterUserService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register-chemist")
    public ResponseEntity<Void> registerNewChemist(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChemistProfileDTO profileDTO) {
        String token = authHeader.substring(7);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        masterUserService.registerNewChemist(username, profileDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(
            @RequestBody RegisterMasterUserRequest request) {
        return ResponseEntity.ok(masterUserService.registerAdmin(request));
    }
}