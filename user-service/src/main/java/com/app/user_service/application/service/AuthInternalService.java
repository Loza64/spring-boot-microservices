package com.app.user_service.application.service;

import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.auth.AuthResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.application.dto.user.auth.UserRegisterDto;

public interface AuthInternalService {

  AuthResponseDto findByUsernameForAuth(String username);

  AuthResponseDto findByIdForAuth(Long id);

  AuthResponseDto registerPublicUser(UserRegisterDto dto);

  UserResponseDto getProfile(Long id);

  UserResponseDto updateProfile(Long id, UserProfileUpdateDto dto);

  void changePassword(Long id, ChangePasswordDto dto);
}
