package com.app.auth_service.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequestDto(
        @NotBlank String name,
        @NotBlank String surname,
        @Email @NotBlank String email) {
}
