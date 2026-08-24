package com.app.user_service.api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.app.user_service.application.dto.user.UserCreateDto;
import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.UserUpdateDto;
import com.app.user_service.application.service.UserService;
import com.app.user_service.common.pagination.PaginationResponse;
import com.app.user_service.domain.constant.PermissionNames;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @PreAuthorize("hasAuthority('" + PermissionNames.USER_CREATE + "')")
  public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.USER_READ + "')")
  public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findById(id));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('" + PermissionNames.USER_READ + "')")
  public ResponseEntity<PaginationResponse<UserResponseDto>> findAll(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Long roleId,
      @RequestParam(required = false) Boolean deleted,
      Pageable pageable) {
    return ResponseEntity.ok(userService.findAll(search, roleId, deleted, pageable));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.USER_UPDATE + "')")
  public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto) {
    return ResponseEntity.ok(userService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('" + PermissionNames.USER_DELETE + "')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/restore")
  @PreAuthorize("hasAuthority('" + PermissionNames.USER_DELETE + "')")
  public ResponseEntity<Void> restore(@PathVariable Long id) {
    userService.restore(id);
    return ResponseEntity.noContent().build();
  }
}

