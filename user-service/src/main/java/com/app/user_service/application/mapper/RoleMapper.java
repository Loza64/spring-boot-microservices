package com.app.user_service.application.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.user_service.application.dto.role.RoleCreateDto;
import com.app.user_service.application.dto.role.RoleResponseDto;
import com.app.user_service.application.dto.role.RoleUpdateDto;
import com.app.user_service.domain.model.Role;

import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { PermissionMapper.class })
public interface RoleMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  Role toEntity(RoleCreateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "permissions", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  void updateEntity(RoleUpdateDto dto, @MappingTarget Role entity);

  RoleResponseDto toResponseDto(Role r);

  @Named("toSummaryDto")
  @Mapping(target = "permissions", ignore = true)
  RoleResponseDto toSummaryDto(Role r);
}
