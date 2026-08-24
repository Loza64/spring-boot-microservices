package com.app.auth_service.infrastructure.security;

import java.util.Optional;

import com.app.auth_service.application.dto.auth.TokenClaims;

public interface TokenService {

  Optional<TokenClaims> parseToken(String token);
}
