package com.luisjrz96.blog.adapters.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.luisjrz96.blog.adapters.security.jwt.KeycloakJwtAuthenticationConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final KeycloakJwtAuthenticationConverter authenticationConverter;

  public SecurityConfig(KeycloakJwtAuthenticationConverter authenticationConverter) {
    this.authenticationConverter = authenticationConverter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/openapi/**")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/authors")
                    .authenticated()
                    .requestMatchers("/api/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)));

    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var cfg = new CorsConfiguration();
    cfg.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://localhost:8080"));
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    cfg.setExposedHeaders(List.of("Location"));
    cfg.setAllowCredentials(true);
    cfg.setMaxAge(3600L);

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
