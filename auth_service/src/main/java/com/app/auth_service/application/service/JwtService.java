package com.app.auth_service.application.service;

import com.app.auth_service.application.dto.auth.TokenClaims;

public interface JwtService {

  String generateAccessToken(TokenClaims claims);
}
