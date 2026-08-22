package com.app.auth_service.client;

import com.app.auth_service.domain.dto.auth.UserRegisterDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.app.auth_service.domain.dto.user.UserAuthDataDto;

@Component
public class UserServiceClient {

    private static final String HEADER = "X-Internal-Api-Key";

    private final RestClient restClient;
    private final String internalApiKey;

    public UserServiceClient(
            @Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder,
            @Value("${services.user-service.url}") String userServiceUrl,
            @Value("${internal.api-key}") String internalApiKey) {
        this.restClient = restClientBuilder.baseUrl(userServiceUrl).build();
        this.internalApiKey = internalApiKey;
    }

    public UserAuthDataDto findByUsername(String username) {
        return restClient.get()
                .uri("/by-username/{username}", username)
                .header(HEADER, internalApiKey)
                .retrieve()
                .body(UserAuthDataDto.class);
    }

    public UserAuthDataDto register(UserRegisterDto dto) {
        return restClient.post()
                .uri("/signup")
                .header(HEADER, internalApiKey)
                .body(dto)
                .retrieve()
                .body(UserAuthDataDto.class);
    }
}