package com.app.user_service.api.controller;

import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.auth.AuthResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.application.dto.user.auth.UserRegisterDto;
import com.app.user_service.application.service.AuthInternalService;
import com.app.user_service.domain.exception.ForbiddenException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/auth")
@RequiredArgsConstructor
@Valid
public class InternalAuthController {

  private final AuthInternalService authInternalService;

  //X-Internal-Api-Key
  @GetMapping("/by-username/{username}")
  public ResponseEntity<AuthResponseDto> findByUsernameForAuth(@PathVariable String username) {
    return ResponseEntity.ok(authInternalService.findByUsernameForAuth(username));
  }

  @GetMapping("/by-id/{id}")
  public ResponseEntity<AuthResponseDto> findByIdForAuth(@PathVariable Long id) {
    return ResponseEntity.ok(authInternalService.findByIdForAuth(id));
  }

  @PostMapping("/signup")
  public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegisterDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authInternalService.registerPublicUser(dto));
  }
}
