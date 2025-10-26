package com.luisjrz96.blog.domain.shared;

import java.text.Normalizer;

import com.luisjrz96.blog.domain.util.ValidationUtil;

public record Slug(String value) {
  public Slug {
    ValidationUtil.notNullAndNonBlank(value, "Slug cannot be null or empty");
    value = normalize(value);
  }

  /** Normalizes input text into a lowercase, accent-free, hyphen-separated slug. */
  private static String normalize(String input) {
    String result = input.strip().toLowerCase();
    result = Normalizer.normalize(result, Normalizer.Form.NFD);
    result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    result = result.replaceAll("[^a-z0-9]+", "-");
    result = result.replaceAll("^-|-$", "");
    return result;
  }

  public static Slug from(String text) {
    return new Slug(text);
  }
}
