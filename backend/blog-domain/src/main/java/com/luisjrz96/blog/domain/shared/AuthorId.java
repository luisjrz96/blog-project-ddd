package com.luisjrz96.blog.domain.shared;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record AuthorId(UUID value) {
  public AuthorId {
    ValidationUtil.requireNonNull(value, "AuthorId cannot be null");
  }
}
