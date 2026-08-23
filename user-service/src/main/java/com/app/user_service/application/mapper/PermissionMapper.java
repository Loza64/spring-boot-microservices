package com.app.user_service.application.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.app.user_service.application.dto.permission.PermissionResponseDto;
import com.app.user_service.application.dto.permission.PermissionUpdateDto;
import com.app.user_service.domain.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

  PermissionResponseDto toResponseDto(Permission p);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  void updateEntity(PermissionUpdateDto dto, @MappingTarget Permission entity);
}

