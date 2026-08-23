package com.app.user_service.domain.exception;

public class ServerException extends RuntimeException {
  public ServerException(String message) {
    super(message);
  }
}

