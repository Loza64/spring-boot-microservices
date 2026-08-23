package com.app.auth_service.application.dto.auth;

import java.util.List;

public record TokenClaims(
    Long id,
    String role,
    List<String> permissions) {
}
