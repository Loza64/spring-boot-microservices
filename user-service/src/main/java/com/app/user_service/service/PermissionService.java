package com.app.user_service.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.app.user_service.common.pagination.PaginationMapper;
import com.app.user_service.common.pagination.PaginationResponse;
import com.app.user_service.domain.dto.permission.PermissionResponseDto;
import com.app.user_service.domain.dto.permission.PermissionUpdateDto;
import com.app.user_service.domain.mapper.PermissionMapper;
import com.app.user_service.domain.model.Permission;
import com.app.user_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.app.user_service.common.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class PermissionService {

  private final PermissionRepository permissionRepository;
  private final PermissionMapper permissionMapper;
  private final PaginationMapper paginationMapper;

  @Transactional
  public void createAllIfNotExists(List<String> names) {
    if (names.isEmpty())
      return;

    Set<String> existing = permissionRepository.findAll().stream()
        .map(Permission::getName)
        .collect(Collectors.toSet());

    List<Permission> toCreate = names.stream()
        .filter(name -> !existing.contains(name))
        .map(this::buildPermission)
        .toList();

    if (!toCreate.isEmpty()) {
      permissionRepository.saveAll(toCreate);
    }
  }

  private Permission buildPermission(String name) {
    return Permission.builder().name(name).build();
  }

  @Transactional
  public PermissionResponseDto update(Long id, PermissionUpdateDto dto) {
    Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Permiso no encontrado con ID: " + id));

    permissionMapper.updateEntity(dto, permission);
    return permissionMapper.toResponseDto(permissionRepository.save(permission));
  }

  @Transactional(readOnly = true)
  public PaginationResponse<PermissionResponseDto> findAll(Pageable pageable) {
    Page<Permission> page = permissionRepository.findAll(pageable);
    return paginationMapper.toPaginationResponse(page.map(permissionMapper::toResponseDto));
  }

}