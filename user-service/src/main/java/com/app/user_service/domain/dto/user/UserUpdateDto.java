package com.app.user_service.domain.dto.user;

import com.app.user_service.domain.dto.base.IdDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

public record UserUpdateDto(
        String username,
        String name,
        String surname,
        Boolean blocked,
        @Email String email,
        @Valid IdDto role) {
}