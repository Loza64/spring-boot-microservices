package com.app.auth_service.infrastructure.client;

import com.app.auth_service.application.dto.auth.UserRegisterDto;
import com.app.auth_service.application.dto.user.UserAuthDataDto;
import com.app.auth_service.domain.exception.NotFoundException;
import com.app.auth_service.domain.exception.ServerException;
import com.app.auth_service.domain.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private static final String HEADER = "X-Internal-Api-Key";

    private final RestClient restClient;
    private final String internalApiKey;
    private final ObjectMapper objectMapper;

    public UserServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.user-service.url}") String userServiceUrl,
            @Value("${internal.api-key}") String internalApiKey,
            ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.baseUrl(userServiceUrl).build();
        this.internalApiKey = internalApiKey;
        this.objectMapper = objectMapper;
    }

    public UserAuthDataDto findByUsername(String username) {
        return restClient.get()
                .uri("/api/internal/auth/by-username/{username}", username)
                .header(HEADER, internalApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String errorMessage = extractErrorMessage(new String(res.getBody().readAllBytes()));
                    throw new UnauthorizedException(errorMessage);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new ServerException("El servicio de usuarios no está disponible");
                })
                .body(UserAuthDataDto.class);
    }

    public UserAuthDataDto findById(Long id) {
        return restClient.get()
                .uri("/api/internal/auth/by-id/{id}", id)
                .header(HEADER, internalApiKey)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String errorMessage = extractErrorMessage(new String(res.getBody().readAllBytes()));
                    throw new NotFoundException(errorMessage);
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
                    String errorMessage = extractErrorMessage(new String(res.getBody().readAllBytes()));
                    throw new NotFoundException(errorMessage);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new ServerException("El servicio de usuarios no está disponible");
                })
                .body(UserAuthDataDto.class);
    }

    private String extractErrorMessage(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            if (jsonNode.has("message")) {
                return jsonNode.get("message").asText();
            }
        } catch (Exception e) {
        }
        return responseBody;
    }
}