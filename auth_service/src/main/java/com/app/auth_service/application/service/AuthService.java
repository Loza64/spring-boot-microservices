package com.app.auth_service.application.service;

import com.app.auth_service.application.dto.auth.AuthResponseDto;
import com.app.auth_service.application.dto.auth.ChangePasswordRequestDto;
import com.app.auth_service.application.dto.auth.LoginRequestDto;
import com.app.auth_service.application.dto.auth.ProfileUpdateRequestDto;
import com.app.auth_service.application.dto.auth.RefreshRequestDto;
import com.app.auth_service.application.dto.auth.SignUpRequestDto;
import com.app.auth_service.application.dto.user.ProfileResponseDto;

public interface AuthService {

  AuthResponseDto login(LoginRequestDto dto);

  AuthResponseDto signUp(SignUpRequestDto dto);

  AuthResponseDto refresh(RefreshRequestDto dto);

  void logout(RefreshRequestDto dto);

  ProfileResponseDto getProfile(String authorizationHeader);

  ProfileResponseDto updateProfile(String authorizationHeader, ProfileUpdateRequestDto dto);

  void changePassword(String authorizationHeader, ChangePasswordRequestDto dto);
}
