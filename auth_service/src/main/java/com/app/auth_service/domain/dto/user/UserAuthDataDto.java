package com.app.auth_service.domain.dto.user;

import java.time.LocalDateTime;

public record UserAuthDataDto(
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