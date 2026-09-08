package com.app.user_service.application.service.seed;

import java.util.Set;

import com.app.user_service.domain.model.Permission;

public interface PermissionSeedService {

  Set<Permission> seed();
}
