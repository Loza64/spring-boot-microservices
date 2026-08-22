package com.app.gateway_service;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayLoggingConfig {

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            System.out.println(">>> Petición interceptada por Gateway en la ruta: " + exchange.getRequest().getPath());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                System.out.println("<<< Respuesta del servicio con status: " + exchange.getResponse().getStatusCode());
            }));
        };
    }
}