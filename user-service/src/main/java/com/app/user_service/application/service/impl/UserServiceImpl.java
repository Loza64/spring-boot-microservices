package com.app.user_service.application.service.impl;

import java.time.LocalDateTime;

import com.app.user_service.application.dto.user.UserCreateDto;
import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.UserUpdateDto;
import com.app.user_service.application.mapper.UserMapper;
import com.app.user_service.application.service.UserService;
import com.app.user_service.common.pagination.PaginationMapper;
import com.app.user_service.common.pagination.PaginationResponse;
import com.app.user_service.domain.constant.RoleNames;
import com.app.user_service.domain.exception.ConflictException;
import com.app.user_service.domain.exception.NotFoundException;
import com.app.user_service.domain.model.User;
import com.app.user_service.infrastructure.persistence.repository.RoleRepository;
import com.app.user_service.infrastructure.persistence.repository.UserRepository;
import com.app.user_service.infrastructure.persistence.specification.UserSpecifications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final UserMapper userMapper;
  private final PaginationMapper paginationMapper;
  private final RoleRepository roleRepository;
  private final PasswordEncoder encoder;

  private static final String SUPER_ADMIN = RoleNames.SUPER_ADMIN;

  @Override
  @Transactional
  public UserResponseDto create(UserCreateDto dto) {

    if (repository.existsByEmail(dto.email())) {
      throw new ConflictException("El email ya está registrado");
    }

    User user = userMapper.toEntity(dto);
    user.setPassword(encoder.encode(dto.password()));

    if (dto.role() != null && dto.role().getId() != null) {
      user.setRole(roleRepository.findById(dto.role().getId())
          .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + dto.role().getId())));
    }
    return userMapper.toResponseDto(repository.save(user));
  }

  @Override
  @Transactional
  public UserResponseDto update(Long id, UserUpdateDto dto) {
    User user = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

    if (user.getRole() != null && SUPER_ADMIN.equals(user.getRole().getName())) {
      throw new ConflictException("El usuario super admin no se puede modificar");
    }

    userMapper.updateEntity(dto, user);

    if (dto.role() != null && dto.role().getId() != null) {
      user.setRole(roleRepository.findById(dto.role().getId())
          .orElseThrow(() -> new NotFoundException("Rol no encontrado con ID: " + dto.role().getId())));
    }
    return userMapper.toResponseDto(repository.save(user));
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponseDto findById(Long id) {
    return repository.findById(id)
        .map(userMapper::toResponseDto)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    User user = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

    if (user.getRole() != null && SUPER_ADMIN.equals(user.getRole().getName())) {
      throw new ConflictException("El usuario super admin no se puede modificar");
    }

    if (user.getDeletedAt() != null) {
      throw new ConflictException("El usuario ya se encuentra eliminado");
    }

    user.setDeletedAt(LocalDateTime.now());
    repository.save(user);
  }

  @Override
  @Transactional
  public void restore(Long id) {
    User user = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

    if (user.getRole() != null && SUPER_ADMIN.equals(user.getRole().getName())) {
      throw new ConflictException("El usuario super admin no se puede modificar");
    }

    if (user.getDeletedAt() == null) {
      throw new ConflictException("El usuario no está eliminado, no se puede restaurar");
    }

    user.setDeletedAt(null);
    repository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public PaginationResponse<UserResponseDto> findAll(String search, Long roleId, Boolean deleted, Pageable pageable) {
    Page<User> page = repository.findAll(UserSpecifications.search(search, roleId, deleted), pageable);
    return paginationMapper.toPaginationResponse(page.map(userMapper::toListResponseDto));
  }
}
