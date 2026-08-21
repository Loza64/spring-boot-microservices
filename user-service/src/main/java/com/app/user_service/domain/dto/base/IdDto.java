package com.app.user_service.domain.dto.base;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdDto {
  @NotNull
  private Long id;
}
