package com.app.auth_service.domain.dto.user;

import java.time.LocalDateTime;
import java.util.Set;

public record RoleResponseDto(
        Long id,
        String name,
        Set<PermissionResponseDto> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt) {
}