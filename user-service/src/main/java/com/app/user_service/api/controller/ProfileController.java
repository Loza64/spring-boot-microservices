package com.app.user_service.api.controller;

import com.app.user_service.application.dto.user.UserResponseDto;
import com.app.user_service.application.dto.user.auth.ChangePasswordDto;
import com.app.user_service.application.dto.user.auth.UserProfileUpdateDto;
import com.app.user_service.application.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Valid
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> profile(Authentication authentication) {
        return ResponseEntity.ok(profileService.profile(Long.valueOf(authentication.getName())));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateMe(Authentication authentication, @RequestBody UserProfileUpdateDto dto) {
        return ResponseEntity.ok(profileService.updateProfile(Long.valueOf(authentication.getName()), dto));
    }

    @PutMapping("/profile/password")
    public ResponseEntity<Void> updatePassword(Authentication authentication, @RequestBody ChangePasswordDto dto) {
        profileService.updatePassword(Long.valueOf(authentication.getName()), dto);
        return ResponseEntity.noContent().build();
    }
}
