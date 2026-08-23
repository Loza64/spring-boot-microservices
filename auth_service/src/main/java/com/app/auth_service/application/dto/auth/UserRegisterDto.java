package com.app.auth_service.application.dto.auth;

public record UserRegisterDto(String username, String name, String surname, String email, String password) {
}
