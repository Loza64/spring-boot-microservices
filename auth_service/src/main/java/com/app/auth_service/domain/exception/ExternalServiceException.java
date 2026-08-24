package com.app.auth_service.domain.exception;

public class ExternalServiceException extends RuntimeException {

    private final String serviceName;
    private final int status;
    private final String errorBody;
    private final boolean connectionFailure;

    public ExternalServiceException(String serviceName, int status, String message, String errorBody) {
        super(message);
        this.serviceName = serviceName;
        this.status = status;
        this.errorBody = errorBody;
        this.connectionFailure = false;
    }

    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.status = 0;
        this.errorBody = null;
        this.connectionFailure = true;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public boolean isConnectionFailure() {
        return connectionFailure;
    }
}
