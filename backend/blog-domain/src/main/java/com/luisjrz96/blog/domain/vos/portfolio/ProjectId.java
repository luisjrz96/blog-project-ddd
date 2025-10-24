package com.luisjrz96.blog.domain.vos.portfolio;

import java.util.UUID;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record ProjectId(UUID value) {
  public ProjectId {
    ValidationUtil.requireNonNull(value, "ProjectId cannot be null");
  }
}
