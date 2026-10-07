package com.app.user_service.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.app.user_service.application.port.out.RolePersistencePort;
import com.app.user_service.domain.model.Role;
import com.app.user_service.infrastructure.persistence.repository.RoleRepository;
import com.app.user_service.infrastructure.persistence.specification.RoleSpecifications;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RolePersistencePort {
  private final RoleRepository repository;

  @Override
  public Optional<Role> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Optional<Role> findByName(String name) {
    return repository.findByName(name);
  }

  @Override
  public boolean existsByName(String name) {
    return repository.existsByName(name);
  }

  @Override
  public List<Role> findAll() {
    return repository.findAll();
  }

  @Override
  public Role save(Role role) {
    return repository.save(role);
  }

  @Override
  public List<Role> saveAll(List<Role> roles) {
    return repository.saveAll(roles);
  }

  @Override
  public Page<Role> search(String search, Boolean deleted, Pageable pageable) {
    return repository.findAll(RoleSpecifications.search(search, deleted), pageable);
  }
}
