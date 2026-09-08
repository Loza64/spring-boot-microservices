package com.app.user_service.infrastructure.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.app.user_service.application.service.seed.RoleSeedService;

import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

  private final RoleSeedService roleSeedService;

  @Override
  public void run(String... args) {
    roleSeedService.seed();
  }
}
