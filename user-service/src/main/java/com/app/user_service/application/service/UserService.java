package com.app.user_service.application.service;

import org.springframework.data.domain.Pageable;

import com.app.user_service.application.dto.user.UserCreateDto;
import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.UserUpdateDto;
import com.app.user_service.application.dto.user.auth.AuthResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.application.dto.user.auth.UserRegisterDto;
import com.app.user_service.common.pagination.PaginationResponse;

public interface UserService extends CrudService<Long, UserCreateDto, UserUpdateDto, UserResponseDto> {

  PaginationResponse<UserResponseDto> findAll(String search, Long roleId, Boolean deleted, Pageable pageable);

  AuthResponseDto findByUsernameForAuth(String username);

  AuthResponseDto findByIdForAuth(Long id);

  AuthResponseDto registerPublicUser(UserRegisterDto dto);

  UserResponseDto getProfile(Long id);

  UserResponseDto updateProfile(Long id, UserProfileUpdateDto dto);

  void changePassword(Long id, ChangePasswordDto dto);
}

