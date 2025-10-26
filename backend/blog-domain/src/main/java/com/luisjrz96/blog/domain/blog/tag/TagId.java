package com.luisjrz96.blog.domain.blog.tag;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record TagId(UUID value) {

  public TagId {
    ValidationUtil.requireNonNull(value, "TagId cannot be null");
  }

  public static TagId newId() {
    return new TagId(UUID.randomUUID());
  }
}
