package com.app.auth_service.application.port.out;

public interface PasswordHashingPort {
  String encode(String rawPassword);

  boolean matches(String rawPassword, String encodedPassword);
}
