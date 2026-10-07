package com.app.auth_service.infrastructure.security.adapter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.auth_service.application.port.out.PasswordHashingPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordHashingAdapter implements PasswordHashingPort {
  private final PasswordEncoder passwordEncoder;

  @Override
  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
