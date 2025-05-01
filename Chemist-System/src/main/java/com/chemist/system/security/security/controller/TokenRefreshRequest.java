package com.chemist.system.security.security.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
@Builder
class TokenRefreshRequest {
    private String refreshToken;

}