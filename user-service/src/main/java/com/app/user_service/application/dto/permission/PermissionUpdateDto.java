package com.app.user_service.application.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionUpdateDto(
    @NotBlank(message = "El título es obligatorio") @Size(min = 3, max = 150) String title) {
}
