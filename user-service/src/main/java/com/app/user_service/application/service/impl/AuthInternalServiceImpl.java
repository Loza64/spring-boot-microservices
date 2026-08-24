package com.app.user_service.application.service.impl;

import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.auth.AuthResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.application.dto.user.auth.UserRegisterDto;
import com.app.user_service.application.mapper.UserMapper;
import com.app.user_service.application.service.AuthInternalService;
import com.app.user_service.domain.exception.BadRequestException;
import com.app.user_service.domain.exception.ConflictException;
import com.app.user_service.domain.exception.NotFoundException;
import com.app.user_service.domain.model.Role;
import com.app.user_service.domain.model.User;
import com.app.user_service.infrastructure.persistence.repository.RoleRepository;
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
  private final RoleRepository roleRepository;
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
    if (repository.existsByEmail(dto.email())) {
      throw new ConflictException("El email ya está registrado");
    }
    if (repository.existsByUsername(dto.username())) {
      throw new ConflictException("El username ya está en uso");
    }

    Role clientRole = roleRepository.findById(ROLE_CLIENT_DEFAULT)
        .orElseThrow(() -> new IllegalStateException("El rol CLIENT no existe. Verifica el seed."));

    User user = userMapper.toRegisterUser(dto);
    user.setPassword(encoder.encode(dto.password()));
    user.setRole(clientRole);

    repository.save(user);
    return findByUsernameForAuth(user.getUsername());
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponseDto getProfile(Long id) {
    User user = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    return userMapper.toResponseDto(user);
  }

  @Override
  @Transactional
  public UserResponseDto updateProfile(Long id, UserProfileUpdateDto dto) {
    User user = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

    if (!user.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
      throw new ConflictException("El email ya está registrado");
    }

    user.setName(dto.name());
    user.setSurname(dto.surname());
    user.setEmail(dto.email());

    return userMapper.toResponseDto(repository.save(user));
  }

  @Override
  @Transactional
  public void changePassword(Long id, ChangePasswordDto dto) {
    User user = repository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

    if (!encoder.matches(dto.currentPassword(), user.getPassword())) {
      throw new BadRequestException("La contraseña actual no es correcta");
    }

    user.setPassword(encoder.encode(dto.newPassword()));
    repository.save(user);
  }
}
