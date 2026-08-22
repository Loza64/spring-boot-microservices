// RefreshRequestDto.java
package com.app.auth_service.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(@NotBlank String refreshToken) {
}