package com.app.user_service.infrastructure.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.app.user_service.application.service.seed.PermissionSeedService;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class PermissionSeeder implements CommandLineRunner {

  private final PermissionSeedService permissionSeedService;

  @Override
  public void run(String... args) {
    permissionSeedService.seed();
  }
}
