package com.luisjrz96.blog.adapters.persistence.exception;

public class UnknownEventException extends RuntimeException {

  public UnknownEventException(String eventType) {
    super("Unknown event type: " + eventType);
  }

  public UnknownEventException(String eventType, Throwable cause) {
    super("Unknown event type: " + eventType, cause);
  }
}
