package com.app.auth_service.domain.dto.user;

public record ProfileResponseDto(
        Long id,
        String username,
        String name,
        String surname,
        String email,
        RoleResponseDto role) {
}