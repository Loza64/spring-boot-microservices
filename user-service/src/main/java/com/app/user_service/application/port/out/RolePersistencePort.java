package com.app.user_service.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.user_service.domain.model.Role;

public interface RolePersistencePort {
  Optional<Role> findById(Long id);

  Optional<Role> findByName(String name);

  boolean existsByName(String name);

  List<Role> findAll();

  Role save(Role role);

  List<Role> saveAll(List<Role> roles);

  Page<Role> search(String search, Boolean deleted, Pageable pageable);
}
