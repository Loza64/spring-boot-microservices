package com.app.user_service.application.service;

import com.app.user_service.application.dto.user.auth.AuthResponseDto;
import com.app.user_service.application.dto.user.auth.UserRegisterDto;

public interface AuthInternalService {

  AuthResponseDto findByUsernameForAuth(String username);

  AuthResponseDto findByIdForAuth(Long id);

  AuthResponseDto registerPublicUser(UserRegisterDto dto);

}
