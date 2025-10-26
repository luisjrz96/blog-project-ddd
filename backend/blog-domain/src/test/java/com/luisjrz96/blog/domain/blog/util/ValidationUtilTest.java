package com.luisjrz96.blog.domain.blog.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.util.ValidationUtil;

class ValidationUtilTest {

  // --- requireNonNull ---

  @Test
  void requireNonNull_returnsObjectWhenNotNull() {
    String value = "hello";

    String result = ValidationUtil.requireNonNull(value, "must not be null");

    assertSame(value, result, "requireNonNull should return the same reference");
  }

  @Test
  void requireNonNull_throwsDomainExceptionWhenNull() {
    String message = "field X is required";

    DomainException ex =
        assertThrows(DomainException.class, () -> ValidationUtil.requireNonNull(null, message));

    assertEquals(message, ex.getMessage());
  }

  // --- notNullAndNonBlank ---

  @Test
  void notNullAndNonBlank_acceptsValidNonEmptyString() {
    assertDoesNotThrow(() -> ValidationUtil.notNullAndNonBlank("valid text", "should not throw"));
  }

  @Test
  void notNullAndNonBlank_throwsWhenNull() {
    String message = "bio cannot be null or blank";

    DomainException ex =
        assertThrows(DomainException.class, () -> ValidationUtil.notNullAndNonBlank(null, message));

    assertEquals(message, ex.getMessage());
  }

  @Test
  void notNullAndNonBlank_throwsWhenEmpty() {
    String message = "title cannot be empty";

    DomainException ex =
        assertThrows(DomainException.class, () -> ValidationUtil.notNullAndNonBlank("", message));

    assertEquals(message, ex.getMessage());
  }

  @Test
  void notNullAndNonBlank_throwsWhenOnlyWhitespace() {
    String message = "slug cannot be blank";

    DomainException ex =
        assertThrows(
            DomainException.class, () -> ValidationUtil.notNullAndNonBlank("   \t  ", message));

    assertEquals(message, ex.getMessage());
  }

  // --- maxLength ---

  @Test
  void maxLength_allowsNull() {
    // null is allowed and should not throw
    assertDoesNotThrow(() -> ValidationUtil.maxLength(null, 10, "too long"));
  }

  @Test
  void maxLength_allowsStringWithinLimit() {
    String input = "hello"; // length = 5
    assertDoesNotThrow(() -> ValidationUtil.maxLength(input, 5, "too long"));
  }

  @Test
  void maxLength_allowsStringBelowLimit() {
    String input = "hey"; // length = 3
    assertDoesNotThrow(() -> ValidationUtil.maxLength(input, 5, "too long"));
  }

  @Test
  void maxLength_throwsWhenExceedsLimit() {
    String message = "summary too long";
    String input = "abcdef"; // length = 6

    DomainException ex =
        assertThrows(DomainException.class, () -> ValidationUtil.maxLength(input, 5, message));

    assertEquals(message, ex.getMessage());
  }
}
