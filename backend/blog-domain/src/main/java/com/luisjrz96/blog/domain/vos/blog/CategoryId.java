package com.luisjrz96.blog.domain.vos.blog;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record CategoryId(UUID value) {

  public CategoryId {
    ValidationUtil.requireNonNull(value, "CategoryId cannot be null");
  }
}
