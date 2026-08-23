package com.app.user_service.application.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.app.user_service.application.dto.permission.PermissionResponseDto;
import com.app.user_service.application.dto.permission.PermissionUpdateDto;
import com.app.user_service.common.pagination.PaginationResponse;

public interface PermissionService {

  void createAllIfNotExists(List<String> names);

  PermissionResponseDto update(Long id, PermissionUpdateDto dto);

  PaginationResponse<PermissionResponseDto> findAll(Pageable pageable);
}

