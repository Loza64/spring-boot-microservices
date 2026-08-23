package com.app.user_service.application.dto.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfileUpdateDto(
        @NotBlank String name,
        @NotBlank String surname,
        @Email @NotBlank String email) {
}
