package com.app.auth_service.application.port.out;

import java.util.Optional;

import com.app.auth_service.domain.model.RefreshToken;

public interface RefreshTokenPersistencePort {
  RefreshToken save(RefreshToken refreshToken);

  Optional<RefreshToken> findByToken(String token);

  void revokeAllByFamilyId(String familyId);
}
