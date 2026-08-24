package com.app.user_service.infrastructure.seed;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.user_service.domain.constant.PermissionNames;
import com.app.user_service.domain.constant.RoleNames;
import com.app.user_service.domain.model.Permission;
import com.app.user_service.domain.model.Role;
import com.app.user_service.domain.model.User;
import com.app.user_service.infrastructure.persistence.repository.PermissionRepository;
import com.app.user_service.infrastructure.persistence.repository.RoleRepository;
import com.app.user_service.infrastructure.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

  private static final List<String> ALL_PERMISSIONS = List.of(
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
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;

  @Value("${seed.super-admin.username}")
  private String superAdminUsername;

  @Value("${seed.super-admin.email}")
  private String superAdminEmail;

  @Value("${seed.super-admin.password}")
  private String superAdminPassword;

  @Override
  @Transactional
  public void run(String... args) {
    Set<Permission> allPermissions = seedPermissions();

    Role superAdminRole = seedSuperAdminRole(allPermissions);
    seedRoleIfMissing(RoleNames.ADMIN);
    seedRoleIfMissing(RoleNames.CLIENT);

    seedSuperAdminUser(superAdminRole);
  }

  private Set<Permission> seedPermissions() {
    return ALL_PERMISSIONS.stream()
        .map(name -> permissionRepository.findByName(name)
            .orElseGet(() -> {
              Permission saved = permissionRepository.save(Permission.builder().name(name).build());
              log.info("Permiso creado: {}", name);
              return saved;
            }))
        .collect(Collectors.toSet());
  }

  private Role seedSuperAdminRole(Set<Permission> allPermissions) {
    Role role = roleRepository.findByName(RoleNames.SUPER_ADMIN)
        .orElseGet(() -> {
          Role created = Role.builder().name(RoleNames.SUPER_ADMIN).permissions(new HashSet<>()).build();
          log.info("Rol creado: {}", RoleNames.SUPER_ADMIN);
          return created;
        });

    Set<Permission> current = role.getPermissions() != null ? role.getPermissions() : new HashSet<>();
    Set<Permission> merged = new HashSet<>(current);
    merged.addAll(allPermissions);
    role.setPermissions(merged);

    return roleRepository.save(role);
  }

  private void seedRoleIfMissing(String name) {
    if (roleRepository.existsByName(name)) {
      return;
    }
    roleRepository.save(Role.builder().name(name).permissions(new HashSet<>()).build());
    log.info("Rol creado: {}", name);
  }

  private void seedSuperAdminUser(Role superAdminRole) {
    if (userRepository.existsByRole_Name(RoleNames.SUPER_ADMIN)) {
      return;
    }

    if (userRepository.existsByUsername(superAdminUsername) || userRepository.existsByEmail(superAdminEmail)) {
      log.warn("No se pudo crear el super admin por defecto: el username o email ya está en uso");
      return;
    }

    User superAdmin = User.builder()
        .username(superAdminUsername)
        .name("Super")
        .surname("Admin")
        .email(superAdminEmail)
        .password(encoder.encode(superAdminPassword))
        .blocked(false)
        .role(superAdminRole)
        .build();

    User saved = userRepository.save(superAdmin);
    log.info("Usuario super admin creado con id {}", saved.getId());
  }
}
