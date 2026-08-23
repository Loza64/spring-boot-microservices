package com.app.user_service.infrastructure.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.user_service.infrastructure.config.JwtProperties;
import com.app.user_service.infrastructure.security.TokenClaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProperties jwtProperties;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
  }

  @SuppressWarnings("unchecked")
  private TokenClaims toTokenClaims(Claims claims) {
    return new TokenClaims(
            claims.get("id", Long.class),
            claims.get("role", String.class),
            (List<String>) claims.get("permissions", List.class));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      try {
        Claims rawClaims = Jwts.parser()
                .verifyWith(key())
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        TokenClaims claims = toTokenClaims(rawClaims);

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (claims.role() != null) {
          authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.role()));
        }
        if (claims.permissions() != null) {
          claims.permissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        }

        var authentication = new UsernamePasswordAuthenticationToken(String.valueOf(claims.id()), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (JwtException | IllegalArgumentException e) {
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
