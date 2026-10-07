package com.app.user_service.application.service.seed.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.user_service.application.dto.seed.SeedAdminDto;
import com.app.user_service.application.port.out.RolePersistencePort;
import com.app.user_service.application.port.out.PasswordHashingPort;
import com.app.user_service.application.port.out.UserPersistencePort;
import com.app.user_service.application.service.seed.AdminSeedService;
import com.app.user_service.domain.constant.RoleNames;
import com.app.user_service.domain.model.Role;
import com.app.user_service.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSeedServiceImpl implements AdminSeedService {

  private static final Logger log = LoggerFactory.getLogger(AdminSeedServiceImpl.class);

  private final UserPersistencePort userRepository;
  private final RolePersistencePort roleRepository;
  private final PasswordHashingPort passwordHashing;

  @Override
  @Transactional
  public void seed(SeedAdminDto admin) {
    if (userRepository.existsByRoleName(RoleNames.SUPER_ADMIN)) {
      return;
    }

    if (userRepository.existsByUsername(admin.username()) || userRepository.existsByEmail(admin.email())) {
      log.warn("No se pudo crear el super admin por defecto: el username o email ya está en uso");
      return;
    }

    Role superAdminRole = roleRepository.findByName(RoleNames.SUPER_ADMIN)
        .orElseThrow(() -> new IllegalStateException("El rol SUPER_ADMIN no fue creado"));

    User superAdmin = buildSuperAdmin(admin, superAdminRole);

    User saved = userRepository.save(superAdmin);
    log.info("Usuario super admin creado con id {}", saved.getId());
  }

  private User buildSuperAdmin(SeedAdminDto admin, Role superAdminRole) {
    return User.builder()
        .username(admin.username())
        .name("Super")
        .surname("Admin")
        .email(admin.email())
        .password(passwordHashing.encode(admin.password()))
        .blocked(false)
        .role(superAdminRole)
        .build();
  }
}
