package com.app.auth_service.domain.dto.auth;

import com.app.auth_service.domain.dto.user.ProfileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private ProfileResponseDto data;
}