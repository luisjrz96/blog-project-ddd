package com.luisjrz96.blog.domain.blog.vos;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record PostId(UUID value) {
  public PostId {
    ValidationUtil.requireNonNull(value, "PostId cannot be null");
  }
}
