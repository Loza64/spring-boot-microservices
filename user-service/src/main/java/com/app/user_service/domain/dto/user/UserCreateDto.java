package com.app.user_service.domain.dto.user;

import com.app.user_service.domain.dto.base.IdDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDto(
    @NotBlank String username,
    @NotBlank String name,
    @NotBlank String surname,
    @Email @NotBlank String email,
    @NotBlank String password,
    @NotBlank IdDto role) {
}