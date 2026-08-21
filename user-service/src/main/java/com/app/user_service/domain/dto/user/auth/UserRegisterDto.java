package com.app.user_service.domain.dto.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterDto(
        @NotBlank String username,
        @NotBlank String name,
        @NotBlank String surname,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password) {
}