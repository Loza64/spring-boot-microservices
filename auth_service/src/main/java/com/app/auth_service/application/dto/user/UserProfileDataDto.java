package com.app.auth_service.application.dto.user;

import java.time.LocalDateTime;

public record UserProfileDataDto(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        boolean blocked,
        RoleResponseDto role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt) {
}
