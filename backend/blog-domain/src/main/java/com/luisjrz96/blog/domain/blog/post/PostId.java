package com.luisjrz96.blog.domain.blog.post;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record PostId(UUID value) {
  public PostId {
    ValidationUtil.requireNonNull(value, "PostId cannot be null");
  }

  public static PostId newId() {
    return new PostId(UUID.randomUUID());
  }
}
