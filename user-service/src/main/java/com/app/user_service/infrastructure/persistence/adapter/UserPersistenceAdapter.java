package com.app.user_service.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.app.user_service.application.port.out.UserPersistencePort;
import com.app.user_service.domain.model.User;
import com.app.user_service.infrastructure.persistence.repository.UserRepository;
import com.app.user_service.infrastructure.persistence.specification.UserSpecifications;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {
  private final UserRepository repository;

  @Override
  public Optional<User> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return repository.findByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public boolean existsByUsername(String username) {
    return repository.existsByUsername(username);
  }

  @Override
  public boolean existsByRoleName(String roleName) {
    return repository.existsByRole_Name(roleName);
  }

  @Override
  public User save(User user) {
    return repository.save(user);
  }

  @Override
  public Page<User> search(String search, Long roleId, Boolean deleted, Pageable pageable) {
    return repository.findAll(UserSpecifications.search(search, roleId, deleted), pageable);
  }
}
