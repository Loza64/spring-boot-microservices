package com.app.user_service.domain.dto.user.auth;

import com.app.user_service.domain.dto.role.RoleResponseDto;

import java.time.LocalDateTime;

public record AuthResponseDto(
        Long id,
        String username,
        String password,
        boolean blocked,
        LocalDateTime deletedAt,
        RoleResponseDto role) {
}