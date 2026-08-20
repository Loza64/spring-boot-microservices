package com.app.user_service.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.app.user_service.common.constants.PermissionNames;
import com.app.user_service.common.constants.RoleNames;
import com.app.user_service.common.pagination.PaginationResponse;
import com.app.user_service.domain.dto.permission.PermissionResponseDto;
import com.app.user_service.domain.dto.permission.PermissionUpdateDto;
import com.app.user_service.service.PermissionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Validated
public class PermissionController {

  private final PermissionService permissionService;

  @GetMapping
  @PreAuthorize("hasAuthority('" + PermissionNames.PERMISSION_READ + "')")
  public ResponseEntity<PaginationResponse<PermissionResponseDto>> findAll(Pageable pageable) {
    return ResponseEntity.ok(permissionService.findAll(pageable));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.PERMISSION_UPDATE + "')")
  public ResponseEntity<PermissionResponseDto> update(@PathVariable Long id,
      @Valid @RequestBody PermissionUpdateDto dto) {
    return ResponseEntity.ok(permissionService.update(id, dto));
  }

  @PostMapping("/seed")
  @PreAuthorize("hasRole('" + RoleNames.SUPER_ADMIN + "')")
  public ResponseEntity<Void> seed(@RequestBody List<@NotBlank String> permissionNames) {
    permissionService.createAllIfNotExists(permissionNames);
    return ResponseEntity.noContent().build();
  }
}