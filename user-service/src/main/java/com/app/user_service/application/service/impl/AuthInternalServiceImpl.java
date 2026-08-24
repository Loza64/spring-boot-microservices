package com.app.user_service.application.service.impl;

import com.app.user_service.application.dto.role.RoleResponseDto;
import com.app.user_service.application.dto.user.auth.AuthResponseDto;
import com.app.user_service.application.dto.user.auth.UserRegisterDto;
import com.app.user_service.application.mapper.RoleMapper;
import com.app.user_service.application.mapper.UserMapper;
import com.app.user_service.application.service.AuthInternalService;
import com.app.user_service.application.service.RoleService;
import com.app.user_service.domain.exception.ConflictException;
import com.app.user_service.domain.exception.NotFoundException;
import com.app.user_service.domain.model.Role;
import com.app.user_service.domain.model.User;
import com.app.user_service.infrastructure.persistence.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthInternalServiceImpl implements AuthInternalService {

  private final UserRepository repository;
  private final UserMapper userMapper;
  private final RoleService roleService;
  private final RoleMapper  roleMapper;
  private final PasswordEncoder encoder;

  private final Long ROLE_CLIENT_DEFAULT = 3L;

  @Override
  @Transactional(readOnly = true)
  public AuthResponseDto findByUsernameForAuth(String username) {
    User user = repository.findByUsername(username).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    return userMapper.toAuthResponseDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public AuthResponseDto findByIdForAuth(Long id) {
    User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    return userMapper.toAuthResponseDto(user);
  }

  @Override
  @Transactional
  public AuthResponseDto registerPublicUser(UserRegisterDto dto) {
    if (repository.existsByEmail(dto.email())) throw new ConflictException("El email ya está registrado");
    if (repository.existsByUsername(dto.username())) throw new ConflictException("El username ya está en uso");

    RoleResponseDto roleResponse = roleService.findById(ROLE_CLIENT_DEFAULT);
    Role role = roleMapper.toEntityResponse(roleResponse);
    User user = userMapper.toRegisterUser(dto);
    user.setPassword(encoder.encode(dto.password()));
    user.setRole(role);

    repository.save(user);
    return findByUsernameForAuth(user.getUsername());
  }
}
