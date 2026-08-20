package com.app.user_service.domain.dto.role;

import java.util.Set;

import com.app.user_service.domain.dto.base.IdDto;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateDto(@NotBlank String name, Boolean active, Set<IdDto> permissions) {
}