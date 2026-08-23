package com.app.user_service.application.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.app.user_service.application.dto.role.RoleCreateDto;
import com.app.user_service.application.dto.role.RoleResponseDto;
import com.app.user_service.application.dto.role.RoleUpdateDto;
import com.app.user_service.common.pagination.PaginationResponse;

public interface RoleService extends CrudService<Long, RoleCreateDto, RoleUpdateDto, RoleResponseDto> {

  PaginationResponse<RoleResponseDto> findAll(String search, Boolean showDeleted, Pageable pageable);

  void createAllIfNotExists(List<String> names);
}

