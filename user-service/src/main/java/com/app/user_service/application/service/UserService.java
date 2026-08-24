package com.app.user_service.application.service;

import org.springframework.data.domain.Pageable;

import com.app.user_service.application.dto.user.UserCreateDto;
import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.UserUpdateDto;
import com.app.user_service.common.pagination.PaginationResponse;

public interface UserService extends CrudService<Long, UserCreateDto, UserUpdateDto, UserResponseDto> {

  PaginationResponse<UserResponseDto> findAll(String search, Long roleId, Boolean deleted, Pageable pageable);
}
