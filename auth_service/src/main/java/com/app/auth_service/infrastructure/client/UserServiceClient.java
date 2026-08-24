package com.app.auth_service.infrastructure.client;

import com.app.auth_service.application.dto.auth.ChangePasswordRequestDto;
import com.app.auth_service.application.dto.auth.ProfileUpdateRequestDto;
import com.app.auth_service.application.dto.auth.UserRegisterDto;
import com.app.auth_service.application.dto.user.UserAuthDataDto;
import com.app.auth_service.application.dto.user.UserProfileDataDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient extends AbstractServiceClient {

    private static final String HEADER = "X-Internal-Api-Key";
    private static final String SERVICE_NAME = "user-service";

    private final RestClient.Builder restClientBuilder;
    private final RestClientErrorHandler errorHandler;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Value("${internal.api-key}")
    private String internalApiKey;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = restClientBuilder.baseUrl(userServiceUrl).build();
    }

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    //X-Internal-Api-Key
    public UserAuthDataDto findByUsername(String username) {
        return execute(() -> restClient.get()
                .uri("/api/internal/auth/by-username/{username}", username)
                .header(HEADER, internalApiKey)
                .retrieve()
                .onStatus(errorHandler)
                .body(UserAuthDataDto.class));
    }

    public UserAuthDataDto findById(Long id) {
        return execute(() -> restClient.get()
                .uri("/api/internal/auth/by-id/{id}", id)
                .header(HEADER, internalApiKey)
                .retrieve()
                .onStatus(errorHandler)
                .body(UserAuthDataDto.class));
    }

    public UserAuthDataDto register(UserRegisterDto dto) {
        return execute(() -> restClient.post()
                .uri("/api/internal/auth/signup")
                .header(HEADER, internalApiKey)
                .body(dto)
                .retrieve()
                .onStatus(errorHandler)
                .body(UserAuthDataDto.class));
    }

    //Bearer token
    public UserProfileDataDto profile(String authorizationHeader) {
        return execute(() -> restClient.get()
                .uri("/api/auth/profile")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .onStatus(errorHandler)
                .body(UserProfileDataDto.class));
    }

    public UserProfileDataDto updateProfile(String authorizationHeader, ProfileUpdateRequestDto dto) {
        return execute(() -> restClient.put()
                .uri("/api/auth/profile")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .body(dto)
                .retrieve()
                .onStatus(errorHandler)
                .body(UserProfileDataDto.class));
    }

    public void updatePassword(String authorizationHeader, ChangePasswordRequestDto dto) {
        execute(() -> {
            restClient.put()
                    .uri("/api/auth/profile/password")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .body(dto)
                    .retrieve()
                    .onStatus(errorHandler)
                    .toBodilessEntity();
            return null;
        });
    }
}