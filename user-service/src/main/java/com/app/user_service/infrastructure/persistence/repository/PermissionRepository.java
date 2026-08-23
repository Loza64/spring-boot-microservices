package com.app.user_service.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.user_service.domain.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}

