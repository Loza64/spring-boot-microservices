package com.app.user_service.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.app.user_service.application.port.out.PermissionPersistencePort;
import com.app.user_service.domain.model.Permission;
import com.app.user_service.infrastructure.persistence.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionPersistencePort {
  private final PermissionRepository repository;

  @Override
  public Optional<Permission> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Optional<Permission> findByName(String name) {
    return repository.findByName(name);
  }

  @Override
  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }

  @Override
  public List<Permission> findAll() {
    return repository.findAll();
  }

  @Override
  public Page<Permission> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public List<Permission> findAllById(Iterable<Long> ids) {
    return repository.findAllById(ids);
  }

  @Override
  public Permission save(Permission permission) {
    return repository.save(permission);
  }

  @Override
  public List<Permission> saveAll(List<Permission> permissions) {
    return repository.saveAll(permissions);
  }
}
