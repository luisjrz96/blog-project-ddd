package com.luisjrz96.blog.application.shared.security;

import java.util.Set;

public record Actor(String userId, Set<Role> roles) {

  public boolean isAdmin() {
    return roles != null && roles.contains(Role.ADMIN);
  }

  public boolean isAuthenticated() {
    return userId != null && !userId.isBlank();
  }
}
