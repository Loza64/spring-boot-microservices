package com.app.user_service.domain.dto.role;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

import com.app.user_service.domain.dto.base.IdDto;

public record RoleUpdateDto(
    @NotBlank String name,
    Boolean active,
    Set<IdDto> permissions) {
}