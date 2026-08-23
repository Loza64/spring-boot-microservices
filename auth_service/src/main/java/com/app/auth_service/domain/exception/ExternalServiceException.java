package com.app.auth_service.domain.exception;

public class ExternalServiceException extends RuntimeException {
    private final int status;

    public ExternalServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}