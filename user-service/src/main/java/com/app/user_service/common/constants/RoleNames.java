package com.app.user_service.common.constants;

import java.util.List;

public final class RoleNames {

  public static final String SUPER_ADMIN = "SUPER_ADMIN";
  public static final String ADMIN = "ADMIN";
  public static final String CLIENT = "CLIENT";

  public static final List<String> HIERARCHY = List.of(SUPER_ADMIN, ADMIN, CLIENT);

  private RoleNames() {
  }
}