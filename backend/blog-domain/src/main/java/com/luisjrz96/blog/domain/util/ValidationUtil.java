package com.luisjrz96.blog.domain.util;

import com.luisjrz96.blog.domain.exception.DomainException;

public class ValidationUtil {

  private ValidationUtil() {
    throw new DomainException("ValidationUtil must not be instantiated");
  }

  public static <T> T requireNonNull(T obj, String message) {
    if (obj == null) {
      throw new DomainException(message);
    }
    return obj;
  }

  public static void notNullAndNonBlank(String input, String message) {
    if (input == null || input.isBlank()) {
      throw new DomainException(message);
    }
  }

  public static void maxLength(String input, int size, String message) {
    if (input != null && input.length() > size) {
      throw new DomainException(message);
    }
  }
}
