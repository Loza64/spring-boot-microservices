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

import com.app.user_service.domain.model.Role;

import lombok.NonNull;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
  boolean existsByName(String name);

  Optional<Role> findByName(String name);

  @Override
  @NonNull
  @EntityGraph(attributePaths = { "permissions" }, type = EntityGraphType.FETCH)
  Optional<Role> findById(@NonNull Long id);

  @Override
  @NonNull
  List<Role> findAll();

  @Override
  @NonNull
  Page<Role> findAll(Specification<Role> spec, Pageable pageable);
}

