package com.app.user_service.domain.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.user_service.domain.dto.user.UserCreateDto;
import com.app.user_service.domain.dto.user.UserResponseDto;
import com.app.user_service.domain.dto.user.UserUpdateDto;
import com.app.user_service.domain.model.User;

@Mapper(componentModel = "spring", uses = { RoleMapper.class })
public interface UserMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blocked", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  User toEntity(UserCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "role", ignore = true)
  void updateEntity(UserUpdateDto dto, @MappingTarget User entity);

  @Mapping(target = "role", qualifiedByName = "toSummaryDto")
  UserResponseDto toResponseDto(User u);

  @Mapping(target = "role", qualifiedByName = "toSummaryDto")
  UserResponseDto toListResponseDto(User u);
}