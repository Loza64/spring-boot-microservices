package com.app.auth_service.infrastructure.security.impl;

import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.app.auth_service.application.dto.auth.TokenClaims;
import com.app.auth_service.infrastructure.config.JwtProperties;
import com.app.auth_service.infrastructure.security.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

  private final JwtProperties jwtProperties;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
  }

  @SuppressWarnings("unchecked")
  private TokenClaims toTokenClaims(Claims claims) {
    return new TokenClaims(
            claims.get("id", Long.class),
            claims.get("role", String.class),
            (List<String>) claims.get("permissions", List.class));
  }

  @Override
  public Optional<TokenClaims> parseToken(String token) {
    try {
      Claims rawClaims = Jwts.parser()
              .verifyWith(key())
              .requireIssuer(jwtProperties.issuer())
              .build()
              .parseSignedClaims(token)
              .getPayload();

      return Optional.of(toTokenClaims(rawClaims));
    } catch (JwtException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
