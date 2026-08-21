package com.app.user_service.domain.dto.user;

import com.app.user_service.domain.dto.base.IdDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank String username,
        @NotBlank String name,
        @NotBlank String surname,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password,
        @NotNull @Valid IdDto role) {
}