package com.app.auth_service.application.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.auth_service.application.service.RefreshTokenService;
import com.app.auth_service.domain.exception.UnauthorizedException;
import com.app.auth_service.domain.model.RefreshToken;
import com.app.auth_service.infrastructure.persistence.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private static final long REFRESH_TOKEN_TTL_DAYS = 7;

  private final RefreshTokenRepository refreshTokenRepository;

  private String hash(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Algoritmo SHA-256 no disponible", e);
    }
  }

  @Override
  public String issue(Long userId) {
    return issue(userId, UUID.randomUUID().toString());
  }

  @Override
  @Transactional
  public String issue(Long userId, String familyId) {
    String refreshTokenValue = UUID.randomUUID().toString();

    RefreshToken entity = RefreshToken.builder()
        .token(hash(refreshTokenValue))
        .familyId(familyId)
        .userId(userId)
        .expiresAt(Instant.now().plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS))
        .build();

    refreshTokenRepository.save(entity);
    return refreshTokenValue;
  }

  @Override
  @Transactional
  public RotationResult rotate(String incomingToken) {
    String hashed = hash(incomingToken);
    RefreshToken stored = refreshTokenRepository.findByToken(hashed)
        .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

    if (stored.isUsed() || stored.isRevoked()) {
      revokeFamily(stored.getFamilyId());
      throw new UnauthorizedException("Reuso de refresh token detectado. Todas las sesiones fueron revocadas.");
    }

    if (stored.getExpiresAt().isBefore(Instant.now())) {
      throw new UnauthorizedException("Refresh token expirado");
    }

    stored.setUsed(true);
    refreshTokenRepository.save(stored);

    String newRefreshToken = issue(stored.getUserId(), stored.getFamilyId());

    return new RotationResult(newRefreshToken, stored.getUserId());
  }

  @Override
  @Transactional
  public void revoke(String incomingToken) {
    String hashed = hash(incomingToken);
    refreshTokenRepository.findByToken(hashed)
        .ifPresent(stored -> revokeFamily(stored.getFamilyId()));
  }

  private void revokeFamily(String familyId) {
    refreshTokenRepository.revokeAllByFamilyId(familyId);
  }
}
