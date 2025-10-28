package com.luisjrz96.blog.application.shared.port;

import com.luisjrz96.blog.application.shared.security.Actor;

public interface UserProvider {
  /** Returns the current authenticated actor (user). */
  Actor getCurrentUser();
}
