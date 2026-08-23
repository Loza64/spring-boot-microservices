package com.app.auth_service.application.service;

import com.app.auth_service.application.dto.auth.AuthResponseDto;
import com.app.auth_service.application.dto.auth.LoginRequestDto;
import com.app.auth_service.application.dto.auth.RefreshRequestDto;
import com.app.auth_service.application.dto.auth.SignUpRequestDto;

public interface AuthService {

  AuthResponseDto login(LoginRequestDto dto);

  AuthResponseDto signUp(SignUpRequestDto dto);

  AuthResponseDto refresh(RefreshRequestDto dto);

  void logout(RefreshRequestDto dto);
}
