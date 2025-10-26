package com.luisjrz96.blog.domain.portfolio.mediaitem;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record MediaId(UUID value) {

  public MediaId {
    ValidationUtil.requireNonNull(value, "MediaId cannot be null");
  }
}
