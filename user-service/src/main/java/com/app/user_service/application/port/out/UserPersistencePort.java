package com.app.user_service.application.port.out;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.user_service.domain.model.User;

public interface UserPersistencePort {
  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByRoleName(String roleName);

  User save(User user);

  Page<User> search(String search, Long roleId, Boolean deleted, Pageable pageable);
}
