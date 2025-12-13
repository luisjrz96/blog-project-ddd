package com.luisjrz96.blog.domain.blog.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.util.ValidationUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

  @ParameterizedTest
  @MethodSource("invalidStrings")
  void notNullAndNonBlank_throwsWhenInvalid(String value, String message) {
    DomainException ex =
            assertThrows(
                    DomainException.class,
                    () -> ValidationUtil.notNullAndNonBlank(value, message)
            );

    assertEquals(message, ex.getMessage());
  }

  static Stream<Arguments> invalidStrings() {
    return Stream.of(
            Arguments.of(null, "bio cannot be null or blank"),
            Arguments.of("", "title cannot be empty"),
            Arguments.of("   \t  ", "slug cannot be blank")
    );
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
