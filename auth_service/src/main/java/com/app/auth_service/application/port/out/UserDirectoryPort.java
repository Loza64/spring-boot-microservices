package com.app.auth_service.application.port.out;

import com.app.auth_service.application.dto.auth.ChangePasswordRequestDto;
import com.app.auth_service.application.dto.auth.ProfileUpdateRequestDto;
import com.app.auth_service.application.dto.auth.UserRegisterDto;
import com.app.auth_service.application.dto.user.UserAuthDataDto;
import com.app.auth_service.application.dto.user.UserProfileDataDto;

public interface UserDirectoryPort {
  UserAuthDataDto findByUsername(String username);

  UserAuthDataDto findById(Long id);

  UserAuthDataDto register(UserRegisterDto dto);

  UserProfileDataDto profile(String authorizationHeader);

  UserProfileDataDto updateProfile(String authorizationHeader, ProfileUpdateRequestDto dto);

  void updatePassword(String authorizationHeader, ChangePasswordRequestDto dto);
}
