package com.app.user_service.application.port.out;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.user_service.domain.model.Permission;

public interface PermissionPersistencePort {
  Optional<Permission> findById(Long id);

  Optional<Permission> findByName(String name);

  boolean existsByName(String name);

  List<Permission> findAll();

  Page<Permission> findAll(Pageable pageable);

  List<Permission> findAllById(Iterable<Long> ids);

  Permission save(Permission permission);

  List<Permission> saveAll(List<Permission> permissions);
}
