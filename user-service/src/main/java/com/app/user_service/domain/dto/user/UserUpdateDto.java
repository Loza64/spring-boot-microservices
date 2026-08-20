package com.app.user_service.domain.dto.user;

import com.app.user_service.domain.dto.base.IdDto;

import jakarta.validation.constraints.Email;

public record UserUpdateDto(String username, String name, String surname, boolean blocked, @Email String email,
    IdDto role) {
}