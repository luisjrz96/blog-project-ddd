package com.luisjrz96.blog.domain.shared.vos;

import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.util.ValidationUtil;

public record Url(String value) {
  public Url {
    ValidationUtil.notNullAndNonBlank(value, "Url cannot be null or empty");

    if (!value.matches("https?://.+")) {
      throw new DomainException(String.format("Invalid URL %s", value));
    }
  }
}
