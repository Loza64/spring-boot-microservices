package com.app.auth_service.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.app.auth_service.application.port.out.RefreshTokenPersistencePort;
import com.app.auth_service.domain.model.RefreshToken;
import com.app.auth_service.infrastructure.persistence.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {
  private final RefreshTokenRepository repository;

  @Override
  public RefreshToken save(RefreshToken refreshToken) {
    return repository.save(refreshToken);
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return repository.findByToken(token);
  }

  @Override
  public void revokeAllByFamilyId(String familyId) {
    repository.revokeAllByFamilyId(familyId);
  }
}
