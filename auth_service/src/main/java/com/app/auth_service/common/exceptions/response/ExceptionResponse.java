package com.app.auth_service.common.exceptions.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionResponse {
  private final int status;
  private final String message;
}
