package com.luisjrz96.blog.adapters.security.bridge;

import com.luisjrz96.blog.application.shared.port.AuthorizationService;
import com.luisjrz96.blog.application.shared.security.Actor;

public class KeycloakAuthorizationService implements AuthorizationService {

  @Override
  public boolean canManageTaxonomy(Actor user) {
    return user != null && user.isAdmin();
  }
}
