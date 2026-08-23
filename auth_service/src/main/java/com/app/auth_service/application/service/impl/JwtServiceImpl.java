package com.app.auth_service.application.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.app.auth_service.application.dto.auth.TokenClaims;
import com.app.auth_service.application.service.JwtService;
import com.app.auth_service.infrastructure.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final JwtProperties jwtProperties;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
  }

  @Override
  public String generateAccessToken(TokenClaims claims) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(String.valueOf(claims.id()))
        .issuer(jwtProperties.issuer())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(jwtProperties.accessTokenExpirationMinutes(), ChronoUnit.MINUTES)))
        .claims(Map.of("id", claims.id(), "role", claims.role(), "permissions", claims.permissions()))
        .signWith(key())
        .compact();
  }
}
