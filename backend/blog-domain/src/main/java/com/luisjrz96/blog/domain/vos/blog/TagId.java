package com.luisjrz96.blog.domain.vos.blog;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record TagId(UUID value) {

  public TagId {
    ValidationUtil.requireNonNull(value, "TagId cannot be null");
  }
}
