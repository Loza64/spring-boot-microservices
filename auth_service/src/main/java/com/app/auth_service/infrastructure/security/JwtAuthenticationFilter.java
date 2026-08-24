package com.app.auth_service.infrastructure.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.auth_service.application.dto.auth.TokenClaims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenService tokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      Optional<TokenClaims> claimsOpt = tokenService.parseToken(token);

      if (claimsOpt.isPresent()) {
        TokenClaims claims = claimsOpt.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (claims.role() != null) {
          authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.role()));
        }
        if (claims.permissions() != null) {
          claims.permissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        }

        var authentication = new UsernamePasswordAuthenticationToken(String.valueOf(claims.id()), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
