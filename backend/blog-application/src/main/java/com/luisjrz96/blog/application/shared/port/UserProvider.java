package com.luisjrz96.blog.application.shared.port;

import com.luisjrz96.blog.application.shared.security.Actor;

public interface UserProvider {
  String currentUserId();

  Actor getCurrentUser();
}
