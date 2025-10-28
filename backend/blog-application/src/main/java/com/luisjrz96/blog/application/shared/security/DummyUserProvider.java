package com.luisjrz96.blog.application.shared.security;

import java.util.Set;

import com.luisjrz96.blog.application.shared.port.UserProvider;

// TODO: remove it once the security adapter is defined
public class DummyUserProvider implements UserProvider {

  private final Actor dummyAdmin = new Actor("admin-user", Set.of(Role.ADMIN));

  @Override
  public Actor getCurrentUser() {
    return dummyAdmin;
  }
}
