package com.app.user_service.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.app.user_service.common.constants.PermissionNames;
import com.app.user_service.common.constants.RoleNames;
import com.app.user_service.common.pagination.PaginationResponse;
import com.app.user_service.domain.dto.role.RoleCreateDto;
import com.app.user_service.domain.dto.role.RoleResponseDto;
import com.app.user_service.domain.dto.role.RoleUpdateDto;
import com.app.user_service.service.RoleServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

  private final RoleServiceImpl roleService;

  @PostMapping
  @PreAuthorize("hasAuthority('" + PermissionNames.ROLE_CREATE + "')")
  public ResponseEntity<RoleResponseDto> create(@Valid @RequestBody RoleCreateDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(dto));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.ROLE_READ + "')")
  public ResponseEntity<RoleResponseDto> findById(@PathVariable Long id) {
    return ResponseEntity.ok(roleService.findById(id));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('" + PermissionNames.ROLE_READ + "')")
  public ResponseEntity<PaginationResponse<RoleResponseDto>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Boolean showDeleted,
      Pageable pageable) {
    return ResponseEntity.ok(roleService.findAll(search, showDeleted, pageable));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.ROLE_UPDATE + "')")
  public ResponseEntity<RoleResponseDto> update(@PathVariable Long id, @Valid @RequestBody RoleUpdateDto dto) {
    return ResponseEntity.ok(roleService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.ROLE_DELETE + "')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    roleService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/restore")
  @PreAuthorize("hasAuthority('" + PermissionNames.ROLE_DELETE + "')")
  public ResponseEntity<Void> restore(@PathVariable Long id) {
    roleService.restore(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/seed")
  @PreAuthorize("hasRole('" + RoleNames.SUPER_ADMIN + "')")
  public ResponseEntity<Void> seed(@RequestBody List<@NotBlank String> roleNames) {
    roleService.createAllIfNotExists(roleNames);
    return ResponseEntity.noContent().build();
  }
}