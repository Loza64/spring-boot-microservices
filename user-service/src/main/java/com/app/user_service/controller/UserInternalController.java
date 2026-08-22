package com.app.user_service.controller;

import com.app.user_service.domain.dto.user.auth.AuthResponseDto;
import com.app.user_service.domain.dto.user.auth.ChangePasswordDto;
import com.app.user_service.domain.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.domain.dto.user.UserResponseDto;
import com.app.user_service.domain.dto.user.auth.UserRegisterDto;
import com.app.user_service.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Valid
public class UserInternalController {

    private final UserServiceImpl userService;

    @GetMapping("/by-username/{username}")
    public ResponseEntity<AuthResponseDto> findByUsernameForAuth(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsernameForAuth(username));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegisterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerPublicUser(dto));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateMe(Authentication authentication, @RequestBody UserProfileUpdateDto dto) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), dto));
    }

    @PutMapping("/profile/password")
    public ResponseEntity<Void> changeMyPassword(Authentication authentication, @RequestBody ChangePasswordDto dto) {
        userService.changePassword(authentication.getName(), dto);
        return ResponseEntity.noContent().build();
    }
}