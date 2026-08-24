package com.app.user_service.application.service.impl;

import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.application.mapper.UserMapper;
import com.app.user_service.application.service.ProfileService;
import com.app.user_service.domain.exception.BadRequestException;
import com.app.user_service.domain.exception.ConflictException;
import com.app.user_service.domain.exception.NotFoundException;
import com.app.user_service.domain.model.User;
import com.app.user_service.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto profile(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Long id, UserProfileUpdateDto dto) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

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
    public void updatePassword(Long id, ChangePasswordDto dto) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (!encoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BadRequestException("La contraseña actual no es correcta");
        }

        user.setPassword(encoder.encode(dto.newPassword()));
        repository.save(user);
    }
}
