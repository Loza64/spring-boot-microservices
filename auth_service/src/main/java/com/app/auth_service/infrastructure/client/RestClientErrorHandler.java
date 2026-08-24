package com.app.auth_service.infrastructure.client;

import com.app.auth_service.domain.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        int httpStatus = response.getStatusCode().value();
        String rawBody = readBody(response);
        JsonNode parsedBody = parseJson(rawBody);
        int status = extractStatus(parsedBody, httpStatus);
        String message = extractMessage(parsedBody, rawBody, httpStatus);
        String serviceName = url.getHost();

        log.warn("Error en {} {} -> status {} | body: {}", method, url, status, rawBody);

        throw new ExternalServiceException(serviceName, status, message, rawBody);
    }

    private String readBody(ClientHttpResponse response) {
        try {
            byte[] bytes = response.getBody() != null ? response.getBody().readAllBytes() : new byte[0];
            return bytes.length == 0 ? null : new String(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode parseJson(String rawBody) {
        if (rawBody == null) {
            return null;
        }
        try {
            return objectMapper.readTree(rawBody);
        } catch (Exception e) {
            return null;
        }
    }

    private int extractStatus(JsonNode parsedBody, int httpStatus) {
        if (parsedBody != null && parsedBody.has("status") && parsedBody.get("status").isInt()) {
            return parsedBody.get("status").asInt();
        }
        return httpStatus;
    }

    private String extractMessage(JsonNode parsedBody, String rawBody, int httpStatus) {
        if (parsedBody == null) {
            return rawBody != null
                    ? rawBody
                    : "El microservicio respondió con status " + httpStatus + " sin cuerpo legible";
        }
        if (parsedBody.has("message")) {
            return parsedBody.get("message").asText();
        }
        return rawBody;
    }
}
