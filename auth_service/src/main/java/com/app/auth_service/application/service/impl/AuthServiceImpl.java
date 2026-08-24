package com.app.auth_service.application.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.auth_service.application.dto.auth.AuthResponseDto;
import com.app.auth_service.application.dto.auth.ChangePasswordRequestDto;
import com.app.auth_service.application.dto.auth.LoginRequestDto;
import com.app.auth_service.application.dto.auth.ProfileUpdateRequestDto;
import com.app.auth_service.application.dto.auth.RefreshRequestDto;
import com.app.auth_service.application.dto.auth.SignUpRequestDto;
import com.app.auth_service.application.dto.auth.TokenClaims;
import com.app.auth_service.application.dto.auth.UserRegisterDto;
import com.app.auth_service.application.dto.user.ProfileResponseDto;
import com.app.auth_service.application.dto.user.UserAuthDataDto;
import com.app.auth_service.application.dto.user.UserProfileDataDto;
import com.app.auth_service.application.mapper.AuthMapper;
import com.app.auth_service.application.service.AuthService;
import com.app.auth_service.application.service.JwtService;
import com.app.auth_service.application.service.RefreshTokenService;
import com.app.auth_service.application.service.RefreshTokenService.RotationResult;
import com.app.auth_service.domain.exception.UnauthorizedException;
import com.app.auth_service.infrastructure.client.UserServiceClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserServiceClient userServiceClient;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final AuthMapper authMapper;

  @Override
  public AuthResponseDto login(LoginRequestDto dto) {
    UserAuthDataDto user = userServiceClient.findByUsername(dto.username());

    if (user == null || !passwordEncoder.matches(dto.password(), user.password())) {
      throw new UnauthorizedException("Usuario o contraseña incorrectos");
    }

    if (user.blocked() || user.deletedAt() != null) {
      throw new UnauthorizedException("Cuenta inactiva o bloqueada");
    }

    return buildAuthResponse(user);
  }

  @Override
  public AuthResponseDto signUp(SignUpRequestDto dto) {
    UserRegisterDto registerDto = authMapper.toUserRegisterDto(dto);
    UserAuthDataDto created = userServiceClient.register(registerDto);

    return buildAuthResponse(created);
  }

  @Override
  public AuthResponseDto refresh(RefreshRequestDto dto) {
    RotationResult rotation = refreshTokenService.rotate(dto.refreshToken());

    UserAuthDataDto user = userServiceClient.findById(rotation.userId());

    if (user.blocked() || user.deletedAt() != null) {
      throw new UnauthorizedException("Cuenta inactiva o bloqueada");
    }

    String accessToken = jwtService.generateAccessToken(authMapper.toTokenClaims(user));
    return new AuthResponseDto(accessToken, rotation.refreshToken(), authMapper.toProfileResponseDto(user));
  }

  @Override
  public void logout(RefreshRequestDto dto) {
    refreshTokenService.revoke(dto.refreshToken());
  }

  @Override
  public ProfileResponseDto getProfile(String authorizationHeader) {
    requireAuthorization(authorizationHeader);
    UserProfileDataDto user = userServiceClient.profile(authorizationHeader);
    return authMapper.toProfileResponseDto(user);
  }

  @Override
  public ProfileResponseDto updateProfile(String authorizationHeader, ProfileUpdateRequestDto dto) {
    requireAuthorization(authorizationHeader);
    UserProfileDataDto user = userServiceClient.updateProfile(authorizationHeader, dto);
    return authMapper.toProfileResponseDto(user);
  }

  @Override
  public void changePassword(String authorizationHeader, ChangePasswordRequestDto dto) {
    requireAuthorization(authorizationHeader);
    userServiceClient.updatePassword(authorizationHeader, dto);
  }

  private void requireAuthorization(String authorizationHeader) {
    if (authorizationHeader == null || authorizationHeader.isBlank()) {
      throw new UnauthorizedException("No autenticado");
    }
  }

  private AuthResponseDto buildAuthResponse(UserAuthDataDto user) {
    TokenClaims claims = authMapper.toTokenClaims(user);
    String accessToken = jwtService.generateAccessToken(claims);
    String refreshToken = refreshTokenService.issue(user.id());

    return new AuthResponseDto(accessToken, refreshToken, authMapper.toProfileResponseDto(user));
  }
}
