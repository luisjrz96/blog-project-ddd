package com.luisjrz96.blog.application.shared;

public record PageRequest(int page, int size) {
  public PageRequest {
    if (page < 0) throw new IllegalArgumentException("page must be positive or zero");
    if (size <= 0) throw new IllegalArgumentException("size must be positive");
  }

  public int offset() {
    return page * size;
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size);
  }
}
