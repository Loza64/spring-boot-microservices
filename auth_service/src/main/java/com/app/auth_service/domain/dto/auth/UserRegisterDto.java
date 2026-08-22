package com.app.auth_service.domain.dto.auth;

public record UserRegisterDto(String username, String name, String surname, String email, String password) {
}