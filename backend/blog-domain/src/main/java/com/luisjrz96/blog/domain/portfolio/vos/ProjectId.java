package com.luisjrz96.blog.domain.portfolio.vos;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record ProjectId(UUID value) {
  public ProjectId {
    ValidationUtil.requireNonNull(value, "ProjectId cannot be null");
  }
}
