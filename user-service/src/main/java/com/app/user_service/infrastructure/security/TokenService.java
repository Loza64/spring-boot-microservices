package com.app.user_service.infrastructure.security;

import java.util.Optional;

public interface TokenService {

  Optional<TokenClaims> parseToken(String token);
}
