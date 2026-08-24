package com.app.user_service.infrastructure.config;

import com.app.user_service.infrastructure.security.InternalApiKeyFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.user_service.infrastructure.security.JwtAuthenticationFilter;
import com.app.user_service.infrastructure.security.RestAccessDeniedHandler;
import com.app.user_service.infrastructure.security.RestAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final InternalApiKeyFilter internalApiKeyFilter;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(eh -> eh
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/internal/auth/by-username/**",
                "/api/internal/auth/by-id/**",
                "/api/internal/auth/signup")
            .hasAuthority("INTERNAL_SERVICE")
            .anyRequest().authenticated()
        )
        .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
