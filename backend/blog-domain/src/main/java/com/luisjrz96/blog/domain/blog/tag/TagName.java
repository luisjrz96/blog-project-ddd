package com.luisjrz96.blog.domain.blog.tag;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record TagName(String value) {
  public TagName {
    ValidationUtil.notNullAndNonBlank(value, "TagName cannot be null or empty");
    ValidationUtil.maxLength(value, 50, "TagName exceeds character length");
  }
}
