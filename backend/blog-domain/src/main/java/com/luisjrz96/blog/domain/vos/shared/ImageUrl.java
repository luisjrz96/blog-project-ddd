package com.luisjrz96.blog.domain.vos.shared;

import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.util.ValidationUtil;

public record ImageUrl(String value) {
  public ImageUrl {
    ValidationUtil.notNullAndNonBlank(value, "ImageUrl cannot be null or empty");

    if (!value.matches("^https?://.+\\.(png|jpe?g|webp|avif)$")) {
      throw new DomainException(String.format("Invalid image URL %s", value));
    }
  }
}
