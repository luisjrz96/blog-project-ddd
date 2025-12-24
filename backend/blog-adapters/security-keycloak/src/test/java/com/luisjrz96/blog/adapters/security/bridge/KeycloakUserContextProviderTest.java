package com.luisjrz96.blog.adapters.security.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.luisjrz96.blog.application.shared.security.Actor;

class KeycloakUserContextProviderTest {

  private final KeycloakUserContextProvider provider = new KeycloakUserContextProvider();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void currentUserId_shouldReturnNull_whenAuthenticationIsNull() {
    SecurityContextHolder.clearContext();
    assertNull(provider.currentUserId());
  }

  @Test
  void currentUserId_shouldReturnNull_whenPrincipalIsNotJwt() {
    var auth =
        new UsernamePasswordAuthenticationToken(
            "not-a-jwt", "n/a", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    SecurityContextHolder.getContext().setAuthentication(auth);
    assertNull(provider.currentUserId());
  }

  @Test
  void currentUserId_shouldReturnSubject_whenPrincipalIsJwt() {
    Jwt jwt = jwtWithSubject("user-123");
    var auth = new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertEquals("user-123", provider.currentUserId());
  }

  @Test
  void getCurrentUser_shouldReturnNull_whenAuthenticationIsNull() {
    SecurityContextHolder.clearContext();
    assertNull(provider.getCurrentUser());
  }

  @Test
  void getCurrentUser_shouldReturnNull_whenPrincipalIsNotJwt() {
    var auth =
        new UsernamePasswordAuthenticationToken(
            "not-a-jwt", "n/a", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertNull(provider.getCurrentUser());
  }

  @Test
  void getCurrentUser_shouldReturnActor_withSubjectAndRoles_whenPrincipalIsJwt() {
    Jwt jwt = jwtWithSubject("user-abc");
    var auth =
        new JwtAuthenticationToken(
            jwt,
            List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("SCOPE_posts:read")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    Actor actor = provider.getCurrentUser();

    assertNotNull(actor);
    assertNotNull("user-abc", actor.userId());

    assertTrue(actor.roles().contains("ROLE_ADMIN"));
    assertTrue(actor.roles().contains("SCOPE_posts:read"));
  }

  @Test
  void getCurrentUser_shouldCollectDistinctAuthorities() {
    Jwt jwt = jwtWithSubject("user-dup");
    var auth =
        new JwtAuthenticationToken(
            jwt,
            List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_ADMIN")));
    SecurityContextHolder.getContext().setAuthentication(auth);

    Actor actor = provider.getCurrentUser();

    assertNotNull(actor);
    assertEquals(Set.of("ROLE_ADMIN"), actor.roles());
  }

  private static Jwt jwtWithSubject(String sub) {
    return new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("sub", sub));
  }
}
