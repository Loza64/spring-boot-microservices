package com.app.auth_service.domain.dto.auth;

import java.util.List;

public record TokenClaims(
    String username,
    Long userId,
    String role,
    List<String> permissions) {
}