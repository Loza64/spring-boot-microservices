package com.app.user_service.application.dto.user.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres") String newPassword) {
}
