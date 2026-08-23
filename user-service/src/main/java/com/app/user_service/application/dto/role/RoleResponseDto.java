package com.app.user_service.application.dto.role;

import java.time.LocalDateTime;
import java.util.Set;

import com.app.user_service.application.dto.permission.PermissionResponseDto;

public record RoleResponseDto(
    Long id,
    String name,
    Set<PermissionResponseDto> permissions,
    LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
}

