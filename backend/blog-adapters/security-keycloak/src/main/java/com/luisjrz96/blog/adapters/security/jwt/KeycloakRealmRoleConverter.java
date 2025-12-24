package com.luisjrz96.blog.adapters.security.jwt;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
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
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    Set<String> roles = new HashSet<>();
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess != null) {
      Object raw = realmAccess.get(ROLES);
      if (raw instanceof Collection<?> c) c.forEach(r -> roles.add(String.valueOf(r)));
    }
    if (clientId != null) {
      Map<String, Object> resourcesAccess = jwt.getClaim("resources_access");
      if (resourcesAccess != null) {
        Object client = resourcesAccess.get(ROLES);
        if (client instanceof Map<?, ?> m) {
          Object clientRoles = m.get(ROLES);
          if (clientRoles instanceof Collection<?> c) c.forEach(r -> roles.add(String.valueOf(r)));
        }
      }
    }

    return roles.stream()
        .filter(Objects::nonNull)
        .map(String::toUpperCase)
        .map(r -> r.startsWith("ROLE") ? r : "ROLE_" + r)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
  }
}
