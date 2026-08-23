package com.app.user_service.application.dto.user.auth;

import com.app.user_service.application.dto.role.RoleResponseDto;

import java.time.LocalDateTime;

public record AuthResponseDto(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        String password,
        boolean blocked,
        LocalDateTime deletedAt,
        RoleResponseDto role) {
}
