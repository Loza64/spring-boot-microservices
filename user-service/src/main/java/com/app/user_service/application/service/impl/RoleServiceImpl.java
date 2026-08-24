package com.app.user_service.application.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.app.user_service.application.dto.base.IdDto;
import com.app.user_service.application.dto.role.RoleCreateDto;
import com.app.user_service.application.dto.role.RoleResponseDto;
import com.app.user_service.application.dto.role.RoleUpdateDto;
import com.app.user_service.application.mapper.RoleMapper;
import com.app.user_service.application.service.RoleService;
import com.app.user_service.common.pagination.PaginationMapper;
import com.app.user_service.common.pagination.PaginationResponse;
import com.app.user_service.domain.constant.RoleNames;
import com.app.user_service.domain.exception.ConflictException;
import com.app.user_service.domain.exception.ForbiddenException;
import com.app.user_service.domain.exception.NotFoundException;
import com.app.user_service.domain.model.Role;
import com.app.user_service.infrastructure.persistence.repository.PermissionRepository;
import com.app.user_service.infrastructure.persistence.repository.RoleRepository;
import com.app.user_service.infrastructure.persistence.specification.RoleSpecifications;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository repository;
  private final RoleMapper mapper;
  private final PaginationMapper paginationMapper;
  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public RoleResponseDto create(RoleCreateDto dto) {

    if (repository.existsByName(dto.name())) {
      throw new ConflictException("El rol con nombre " + dto.name() + " ya existe");
    }

    Role role = mapper.toEntityCreate(dto);
    if (dto.permissions() != null) {
      List<Long> ids = dto.permissions().stream().map(IdDto::getId).toList();
      role.setPermissions(new HashSet<>(permissionRepository.findAllById(ids)));
    }

    return mapper.toResponseDto(repository.save(role));
  }

  @Override
  @Transactional
  public RoleResponseDto update(Long id, RoleUpdateDto dto) {
    Role role = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));

    requireNotSuperAdmin(role);

    mapper.toUpdateEntity(dto, role);

    if (dto.permissions() != null) {
      List<Long> ids = dto.permissions().stream().map(IdDto::getId).toList();
      role.setPermissions(new HashSet<>(permissionRepository.findAllById(ids)));
    }

    return mapper.toResponseDto(repository.save(role));
  }

  @Override
  @Transactional(readOnly = true)
  public RoleResponseDto findById(Long id) {
    return repository.findById(id)
            .map(mapper::toResponseDto)
            .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Role role = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));

    requireNotSuperAdmin(role);

    if (role.getDeletedAt() != null) {
      throw new ConflictException("El rol ya se encuentra eliminado");
    }

    role.setDeletedAt(LocalDateTime.now());
    repository.save(role);
  }

  @Override
  @Transactional
  public void restore(Long id) {
    Role role = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + id));

    requireNotSuperAdmin(role);

    if (role.getDeletedAt() == null) {
      throw new ConflictException("El rol no está eliminado, no se puede restaurar");
    }

    role.setDeletedAt(null);
    repository.save(role);
  }

  @Override
  @Transactional(readOnly = true)
  public PaginationResponse<RoleResponseDto> findAll(String search, Boolean showDeleted, Pageable pageable) {
    Specification<Role> spec = RoleSpecifications.search(search, showDeleted);

    Page<Role> page = repository.findAll(spec, pageable);
    return paginationMapper.toPaginationResponse(page.map(mapper::toSummaryDto));
  }

  @Override
  @Transactional
  public void createAllIfNotExists(List<String> names) {
    if (names.isEmpty())
      return;

    Set<String> existing = repository.findAll().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

    List<Role> toCreate = names.stream()
            .filter(name -> !existing.contains(name))
            .map(this::buildRole)
            .toList();

    if (!toCreate.isEmpty()) {
      repository.saveAll(toCreate);
    }
  }

  private void requireNotSuperAdmin(Role role) {
    if (RoleNames.SUPER_ADMIN.equals(role.getName())) {
      throw new ForbiddenException("El rol SUPER_ADMIN no puede ser modificado");
    }
  }

  private Role buildRole(String name) {
    return Role.builder().name(name).build();
  }
}