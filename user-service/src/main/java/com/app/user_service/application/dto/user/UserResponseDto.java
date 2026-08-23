package com.app.user_service.application.dto.user;

import java.time.LocalDateTime;

import com.app.user_service.application.dto.role.RoleResponseDto;

public record UserResponseDto(
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
