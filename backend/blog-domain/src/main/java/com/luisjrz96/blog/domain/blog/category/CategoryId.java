package com.luisjrz96.blog.domain.blog.category;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record CategoryId(UUID value) {

  public CategoryId {
    ValidationUtil.requireNonNull(value, "CategoryId cannot be null");
  }

  public static CategoryId newId() {
    return new CategoryId(UUID.randomUUID());
  }
}
