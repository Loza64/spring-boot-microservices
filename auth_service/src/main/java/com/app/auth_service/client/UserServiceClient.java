package com.app.auth_service.client;

import com.app.auth_service.common.exceptions.NotFoundException;
import com.app.auth_service.common.exceptions.ServerException;
import com.app.auth_service.common.exceptions.UnauthorizedException;
import com.app.auth_service.domain.dto.auth.UserRegisterDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.app.auth_service.domain.dto.user.UserAuthDataDto;

@Component
public class UserServiceClient {

    private static final String HEADER = "X-Internal-Api-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    public UserServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.user-service.url}") String userServiceUrl,
            @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = restClientBuilder.baseUrl(userServiceUrl).build();
        this.internalApiKey = internalApiKey;
    }

    public UserAuthDataDto findByUsername(String username) {
        return restClient.get()
                .uri("/api/internal/auth/by-username/{username}", username)
                .header(HEADER, internalApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new UnauthorizedException("Usuario o contraseña incorrectos");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new ServerException("El servicio de usuarios no está disponible");
                })
                .body(UserAuthDataDto.class);
    }

    public UserAuthDataDto register(UserRegisterDto dto) {
        return restClient.post()
                .uri("/api/internal/auth/signup")
                .header(HEADER, internalApiKey)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new NotFoundException("No se pudo registrar el usuario");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new ServerException("El servicio de usuarios no está disponible");
                })
                .body(UserAuthDataDto.class);
    }
}