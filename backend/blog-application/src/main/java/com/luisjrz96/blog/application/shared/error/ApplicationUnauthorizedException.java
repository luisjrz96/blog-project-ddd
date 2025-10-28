package com.luisjrz96.blog.application.shared.error;

public class ApplicationUnauthorizedException extends RuntimeException {
  public ApplicationUnauthorizedException(String message) {
    super(message);
  }
}
