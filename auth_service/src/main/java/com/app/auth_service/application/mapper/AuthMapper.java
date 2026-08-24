package com.app.auth_service.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.app.auth_service.application.dto.auth.SignUpRequestDto;
import com.app.auth_service.application.dto.auth.TokenClaims;
import com.app.auth_service.application.dto.auth.UserRegisterDto;
import com.app.auth_service.application.dto.user.PermissionResponseDto;
import com.app.auth_service.application.dto.user.ProfileResponseDto;
import com.app.auth_service.application.dto.user.RoleResponseDto;
import com.app.auth_service.application.dto.user.UserAuthDataDto;
import com.app.auth_service.application.dto.user.UserProfileDataDto;

@Mapper(componentModel = "spring")
public interface AuthMapper {

  UserRegisterDto toUserRegisterDto(SignUpRequestDto dto);

  ProfileResponseDto toProfileResponseDto(UserAuthDataDto user);

  ProfileResponseDto toProfileResponseDto(UserProfileDataDto user);

  @Mapping(target = "role", source = "role", qualifiedByName = "roleName")
  @Mapping(target = "permissions", source = "role", qualifiedByName = "permissionNames")
  TokenClaims toTokenClaims(UserAuthDataDto user);

  @Named("roleName")
  default String roleName(RoleResponseDto role) {
    return role == null ? null : role.name();
  }

  @Named("permissionNames")
  default List<String> permissionNames(RoleResponseDto role) {
    if (role == null || role.permissions() == null) {
      return List.of();
    }
    return role.permissions().stream().map(PermissionResponseDto::name).toList();
  }
}
