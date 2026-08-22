package com.app.auth_service.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConfictException extends RuntimeException {
  public ConfictException(String message) {
    super(message);
  }
}
