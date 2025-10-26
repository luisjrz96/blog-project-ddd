package com.luisjrz96.blog.domain.shared;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record Summary(String value) {
  public Summary {
    ValidationUtil.notNullAndNonBlank(value, "Summary cannot be null or empty");
  }
}
