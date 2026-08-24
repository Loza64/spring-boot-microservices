package com.app.auth_service.infrastructure.client;

import com.app.auth_service.domain.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

@Slf4j
public abstract class AbstractServiceClient {

    protected abstract String getServiceName();

    protected <T> T execute(Supplier<T> call) {
        try {
            return call.get();
        } catch (ExternalServiceException e) {
            throw e;
        } catch (ResourceAccessException e) {
            log.error("'{}' no disponible: {}", getServiceName(), e.getMessage());
            throw new ExternalServiceException(getServiceName(),"No se pudo contactar al microservicio '" + getServiceName() + "': " + e.getMessage(), e);
        } catch (RestClientException e) {
            log.error("Error inesperado al comunicarse con '{}': {}", getServiceName(), e.getMessage());
            throw new ExternalServiceException(getServiceName(), "Error inesperado al comunicarse con '" + getServiceName() + "': " + e.getMessage(), e);
        }
    }
}
