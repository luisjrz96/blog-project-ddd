package com.luisjrz96.blog.domain.blog.category;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record CategoryName(String value) {
  public CategoryName {
    ValidationUtil.notNullAndNonBlank(value, "CategoryName cannot be null or empty");
    ValidationUtil.maxLength(value, 50, "CategoryName exceeds characters length");
  }
}
