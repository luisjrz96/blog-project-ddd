package com.luisjrz96.blog.domain.vos.blog;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record Markdown(String value) {
  public Markdown {
    ValidationUtil.requireNonNull(value, "Markdown cannot be null");
  }
}
