package com.app.auth_service.domain.exception;

public class ServerException extends RuntimeException {
  public ServerException(String message) {
    super(message);
  }
}
