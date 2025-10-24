package com.luisjrz96.blog.domain.blog.vos;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record Markdown(String value) {
  public Markdown {
    ValidationUtil.requireNonNull(value, "Markdown cannot be null");
  }
}
