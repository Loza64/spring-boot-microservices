package com.app.user_service.application.service;

import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;

public interface ProfileService {
    UserResponseDto profile(Long id);
    UserResponseDto updateProfile(Long id, UserProfileUpdateDto dto);
    void updatePassword(Long id, ChangePasswordDto dto);
}
