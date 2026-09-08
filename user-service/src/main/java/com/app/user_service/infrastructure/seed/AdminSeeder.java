package com.app.user_service.infrastructure.seed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.app.user_service.application.dto.seed.SeedAdminDto;
import com.app.user_service.application.service.seed.AdminSeedService;

import lombok.RequiredArgsConstructor;

@Component
@Order(3)
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

  private final AdminSeedService adminSeedService;

  @Value("${seed.super-admin.username}")
  private String superAdminUsername;

  @Value("${seed.super-admin.email}")
  private String superAdminEmail;

  @Value("${seed.super-admin.password}")
  private String superAdminPassword;

  @Override
  public void run(String... args) {
    SeedAdminDto admin = new SeedAdminDto(superAdminUsername, superAdminEmail, superAdminPassword);
    adminSeedService.seed(admin);
  }
}
