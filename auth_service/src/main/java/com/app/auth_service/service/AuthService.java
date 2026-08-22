package com.app.auth_service.service;

import java.util.List;

import com.app.auth_service.domain.dto.user.ProfileResponseDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.auth_service.client.UserServiceClient;
import com.app.auth_service.common.exceptions.UnauthorizedException;
import com.app.auth_service.domain.dto.auth.*;
import com.app.auth_service.domain.dto.user.PermissionResponseDto;
import com.app.auth_service.domain.dto.user.UserAuthDataDto;
import com.app.auth_service.service.RefreshTokenService.RotationResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

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

    public AuthResponseDto signUp(SignUpRequestDto dto) {
        UserAuthDataDto created = userServiceClient.register(
                new UserRegisterDto(dto.username(), dto.name(), dto.surname(), dto.email(), dto.password()));

        return buildAuthResponse(created);
    }

    public AuthResponseDto refresh(RefreshRequestDto dto) {
        RotationResult rotation = refreshTokenService.rotate(dto.refreshToken());

        UserAuthDataDto user = userServiceClient.findByUsername(rotation.username());

        if (user.blocked() || user.deletedAt() != null) {
            throw new UnauthorizedException("Cuenta inactiva o bloqueada");
        }

        String accessToken = jwtService.generateAccessToken(toClaims(user));
        return new AuthResponseDto(accessToken, rotation.refreshToken(), toProfile(user));
    }

    public void logout(RefreshRequestDto dto) {
        refreshTokenService.revoke(dto.refreshToken());
    }

    private AuthResponseDto buildAuthResponse(UserAuthDataDto user) {
        String accessToken = jwtService.generateAccessToken(toClaims(user));
        String refreshToken = refreshTokenService.issue(user.id(), user.username());

        return new AuthResponseDto(accessToken, refreshToken, toProfile(user));
    }

    private TokenClaims toClaims(UserAuthDataDto user) {
        String roleName = user.role() == null ? null : user.role().name();
        List<String> permissions = user.role() == null
                ? List.of()
                : user.role().permissions().stream().map(PermissionResponseDto::name).toList();

        return new TokenClaims(user.username(), user.id(), roleName, permissions);
    }

    private ProfileResponseDto toProfile(UserAuthDataDto user) {
        return new ProfileResponseDto(user.id(), user.username(), user.name(), user.surname(), user.email(), user.role());
    }
}