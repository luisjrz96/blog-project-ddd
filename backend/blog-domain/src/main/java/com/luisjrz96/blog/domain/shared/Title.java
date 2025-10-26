package com.luisjrz96.blog.domain.shared;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record Title(String value) {
  public Title {
    ValidationUtil.notNullAndNonBlank(value, "Title cannot be empty or null");
  }
}
