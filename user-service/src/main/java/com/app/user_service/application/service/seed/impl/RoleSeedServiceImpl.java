package com.app.user_service.application.service.seed.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.user_service.application.service.seed.RoleSeedService;
import com.app.user_service.domain.constant.RoleNames;
import com.app.user_service.domain.model.Permission;
import com.app.user_service.domain.model.Role;
import com.app.user_service.infrastructure.persistence.repository.PermissionRepository;
import com.app.user_service.infrastructure.persistence.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleSeedServiceImpl implements RoleSeedService {

  private static final Logger log = LoggerFactory.getLogger(RoleSeedServiceImpl.class);
  private static final List<String> DEFAULT_ROLES = List.of(RoleNames.ADMIN, RoleNames.CLIENT);

  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;

  @Override
  @Transactional
  public Role seed() {
    Set<Permission> availablePermissions = new HashSet<>(permissionRepository.findAll());
    Role superAdminRole = seedSuperAdminRole(availablePermissions);

    DEFAULT_ROLES.forEach(this::seedRoleIfMissing);
    return superAdminRole;
  }

  private Role seedSuperAdminRole(Set<Permission> availablePermissions) {
    Role superAdminRole = roleRepository.findByName(RoleNames.SUPER_ADMIN)
        .orElseGet(() -> {
          Role created = createRole(RoleNames.SUPER_ADMIN);
          log.info("Rol creado: {}", RoleNames.SUPER_ADMIN);
          return created;
        });

    Set<Permission> assignedPermissions = getAssignedPermissions(superAdminRole);
    assignedPermissions.addAll(availablePermissions);
    superAdminRole.setPermissions(assignedPermissions);

    return roleRepository.save(superAdminRole);
  }

  private void seedRoleIfMissing(String name) {
    if (roleRepository.existsByName(name)) {
      return;
    }
    roleRepository.save(createRole(name));
    log.info("Rol creado: {}", name);
  }

  private Role createRole(String name) {
    return Role.builder().name(name).permissions(new HashSet<>()).build();
  }

  private Set<Permission> getAssignedPermissions(Role role) {
    return role.getPermissions() == null ? new HashSet<>() : new HashSet<>(role.getPermissions());
  }
}
