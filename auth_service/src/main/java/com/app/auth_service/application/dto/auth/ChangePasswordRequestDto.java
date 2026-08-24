package com.app.auth_service.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres") String newPassword) {
}
