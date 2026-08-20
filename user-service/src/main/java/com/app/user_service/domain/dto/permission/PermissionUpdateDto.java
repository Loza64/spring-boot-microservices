package com.app.user_service.domain.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionUpdateDto(
    @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 100) String name) {
}