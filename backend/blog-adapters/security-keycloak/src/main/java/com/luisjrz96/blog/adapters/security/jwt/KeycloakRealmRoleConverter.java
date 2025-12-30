package com.luisjrz96.blog.adapters.security.jwt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final String ROLES = "roles";
  private final String clientId;

  public KeycloakRealmRoleConverter(String clientId) {
    this.clientId = clientId;
  }

  @Override
  public @Nullable Collection<GrantedAuthority> convert(Jwt jwt) {
    Objects.requireNonNull(jwt, "jwt must not be null");

    Set<String> roles = new HashSet<>();
    addRealmRoles(jwt, roles);
    addClientRoles(jwt, roles);

    return roles.stream()
        .map(KeycloakRealmRoleConverter::normalizeRole)
        .map(SimpleGrantedAuthority::new)
        .collect(java.util.stream.Collectors.toUnmodifiableSet());
  }

  private static void addRealmRoles(Jwt jwt, Set<String> out) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess == null) return;
    addRolesValue(realmAccess.get(ROLES), out);
  }

  private void addClientRoles(Jwt jwt, Set<String> out) {
    if (clientId == null || clientId.isBlank()) return;
    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    if (resourceAccess == null) return;

    Object client = resourceAccess.get(clientId);
    if (!(client instanceof Map<?, ?> clientMap)) return;

    addRolesValue(clientMap.get(ROLES), out);
  }

  private static void addRolesValue(Object maybeRoles, Set<String> out) {
    if (maybeRoles instanceof Collection<?> roles) {
      for (Object r : roles) out.add(String.valueOf(r));
    }
  }

  private static String normalizeRole(String role) {
    String r = String.valueOf(role).toUpperCase();
    return r.startsWith("ROLE_") ? r : "ROLE_" + r;
  }
}
