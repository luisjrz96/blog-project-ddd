package com.luisjrz96.blog.adapters.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class KeycloakJwtAuthenticationConverterTest {

  private final KeycloakJwtAuthenticationConverter converter =
      new KeycloakJwtAuthenticationConverter();

  @Test
  void convert_shouldReturnJwtAuthenticationToken_withSameJwt() {
    Jwt jwt = jwtWithClaims(Map.of("sub", "user-1"));

    AbstractAuthenticationToken auth = converter.convert(jwt);

    assertInstanceOf(JwtAuthenticationToken.class, auth);
    assertEquals(jwt, ((JwtAuthenticationToken) auth).getToken());
    assertEquals(jwt, auth.getPrincipal());
  }

  @Test
  void convert_shouldIncludeAuthorities_fromRealmAndClientRoles_whenPresent() {
    Jwt jwt =
        jwtWithClaims(
            Map.of(
                "sub",
                "user-1",
                "realm_access",
                Map.of("roles", List.of("ADMIN", "EDITOR")),
                "resource_access",
                Map.of(
                    "blog-web", Map.of("roles", List.of("AUTHOR")),
                    "some-other-client", Map.of("roles", List.of("IGNORED")))));

    JwtAuthenticationToken auth = (JwtAuthenticationToken) converter.convert(jwt);

    assertNotNull(auth);
    assertTrue(authorityStrings(auth.getAuthorities()).contains("ROLE_ADMIN"));
    assertTrue(authorityStrings(auth.getAuthorities()).contains("ROLE_EDITOR"));
  }

  @Test
  void convert_shouldReturnEmptyAuthorities_whenNoRolesClaims() {
    Jwt jwt = jwtWithClaims(Map.of("sub", "user-1"));

    JwtAuthenticationToken auth = (JwtAuthenticationToken) converter.convert(jwt);

    assertNotNull(auth);
    assertTrue(auth.getAuthorities().isEmpty());
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        claims);
  }

  private static Collection<String> authorityStrings(Collection<? extends GrantedAuthority> auths) {
    return auths.stream().map(GrantedAuthority::getAuthority).toList();
  }
}
