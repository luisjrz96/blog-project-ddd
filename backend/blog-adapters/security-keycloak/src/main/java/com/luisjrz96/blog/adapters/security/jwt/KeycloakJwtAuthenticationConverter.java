package com.luisjrz96.blog.adapters.security.jwt;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class KeycloakJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final Converter<Jwt, Collection<GrantedAuthority>> rolesConverter =
      new KeycloakRealmRoleConverter("blog-web");

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    var authorities = rolesConverter.convert(jwt);
    return new JwtAuthenticationToken(jwt, authorities);
  }
}
