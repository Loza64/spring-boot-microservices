package com.app.auth_service.application.service;

public interface RefreshTokenService {

  String issue(Long userId);

  String issue(Long userId, String familyId);

  RotationResult rotate(String incomingToken);

  void revoke(String incomingToken);

  record RotationResult(String refreshToken, Long userId) {
  }
}
