package com.app.user_service.infrastructure.security;

import java.util.List;

public record TokenClaims(
    Long id,
    String role,
    List<String> permissions) {
}
