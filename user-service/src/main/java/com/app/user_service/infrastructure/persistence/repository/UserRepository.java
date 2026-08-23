package com.app.user_service.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.app.user_service.domain.model.User;

import lombok.NonNull;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  boolean existsByRole_Name(String name);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  @EntityGraph(attributePaths = "role", type = EntityGraphType.FETCH)
  Optional<User> findByUsername(String username);

  @Override
  @EntityGraph(attributePaths = { "role" }, type = EntityGraphType.FETCH)
  List<User> findAll();

  @Override
  @EntityGraph(attributePaths = { "role" }, type = EntityGraphType.FETCH)
  Page<User> findAll(Specification<User> spec, @NonNull Pageable pageable);

  @Override
  @NonNull
  @EntityGraph(attributePaths = { "role", "role.permissions" }, type = EntityGraphType.FETCH)
  Optional<User> findById(@NonNull Long id);
}

