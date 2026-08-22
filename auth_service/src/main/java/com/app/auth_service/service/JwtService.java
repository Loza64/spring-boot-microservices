package com.app.auth_service.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.app.auth_service.domain.dto.auth.TokenClaims;
import org.springframework.stereotype.Service;

import com.app.auth_service.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    public String generateAccessToken(TokenClaims claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(claims.username())
                .issuer(jwtProperties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.accessTokenExpirationMinutes(), ChronoUnit.MINUTES)))
                .claims(Map.of("userId", claims.userId(), "role", claims.role(), "permissions", claims.permissions()))
                .signWith(key())
                .compact();
    }
}