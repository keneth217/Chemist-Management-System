package com.chemist.system.master.dto;

import com.chemist.system.tenant.model.ERole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterMasterUserRequest {

    private String username;
    private String password;
    private String role;
    private String email;
    private String phoneNo;
    private  boolean active;
}
