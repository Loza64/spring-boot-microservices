package com.app.auth_service.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.auth_service.application.dto.auth.*;
import com.app.auth_service.application.dto.user.ProfileResponseDto;
import com.app.auth_service.application.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signUp(@Valid @RequestBody SignUpRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        return ResponseEntity.ok(authService.refresh(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDto dto) {
        authService.logout(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> profile(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(authService.getProfile(authorization));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ProfileUpdateRequestDto dto) {
        return ResponseEntity.ok(authService.updateProfile(authorization, dto));
    }

    @PutMapping("/profile/password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        authService.changePassword(authorization, dto);
        return ResponseEntity.noContent().build();
    }
}
