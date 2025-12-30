package com.luisjrz96.blog.it;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Util {

  private Util() {
    throw new IllegalArgumentException("Util class must not be instantiated");
  }

  public static final String LOCALHOST_URL = "http://localhost";

  public static String getApiEndpoint(
      @NonNull int port, @NonNull String path, @Nullable String pathSuffix) {
    return LOCALHOST_URL + ":" + port + path + Optional.ofNullable(pathSuffix).orElse("");
  }
}
