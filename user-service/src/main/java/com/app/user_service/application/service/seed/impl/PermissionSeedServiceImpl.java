package com.app.user_service.application.service.seed.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.user_service.application.service.seed.PermissionSeedService;
import com.app.user_service.domain.constant.PermissionNames;
import com.app.user_service.domain.model.Permission;
import com.app.user_service.infrastructure.persistence.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionSeedServiceImpl implements PermissionSeedService {

  private static final Logger log = LoggerFactory.getLogger(PermissionSeedServiceImpl.class);

  private static final List<String> PERMISSIONS_TO_SEED = List.of(
      PermissionNames.USER_CREATE,
      PermissionNames.USER_READ,
      PermissionNames.USER_UPDATE,
      PermissionNames.USER_DELETE,
      PermissionNames.ROLE_CREATE,
      PermissionNames.ROLE_READ,
      PermissionNames.ROLE_UPDATE,
      PermissionNames.ROLE_DELETE,
      PermissionNames.PERMISSION_READ,
      PermissionNames.PERMISSION_UPDATE);

  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public Set<Permission> seed() {
    return PERMISSIONS_TO_SEED.stream()
        .map(this::seedPermission)
        .collect(Collectors.toSet());
  }

  private Permission seedPermission(String permissionName) {
    Permission permission = permissionRepository.findByName(permissionName)
        .orElseGet(() -> createPermission(permissionName));
    return permission;
  }

  private Permission createPermission(String permissionName) {
    Permission permission = permissionRepository.save(Permission.builder()
        .name(permissionName)
        .build());
    log.info("Permiso creado: {}", permissionName);
    return permission;
  }
}
