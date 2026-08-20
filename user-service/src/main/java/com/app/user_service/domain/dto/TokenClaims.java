package com.app.user_service.domain.dto;

import java.util.List;

public record TokenClaims(
    String username,
    Long userId,
    String role,
    List<String> permissions) {
}