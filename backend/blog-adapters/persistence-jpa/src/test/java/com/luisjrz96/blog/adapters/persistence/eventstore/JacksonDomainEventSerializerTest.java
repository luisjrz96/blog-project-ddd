package com.luisjrz96.blog.adapters.persistence.eventstore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisjrz96.blog.adapters.persistence.exception.EventSerializationException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

class JacksonDomainEventSerializerTest {

  private ObjectMapper objectMapper;
  private JacksonDomainEventSerializer serializer;

  @BeforeEach
  void setUp() {
    objectMapper = spy(ObjectMapper.class);
    serializer = new JacksonDomainEventSerializer(objectMapper);
  }

  @Test
  void testSerialize_ShouldReturnJsonString() {
    // Given
    String uuid = "cbc76841-8508-46bf-a86e-0cf0572794d0";
    Instant creationDate = Instant.parse("2025-01-01T10:00:00Z");
    DomainEvent event =
        new CategoryCreated(
            new CategoryId(UUID.fromString(uuid)),
            new CategoryName("Software Development"),
            new Slug("software-development"),
            new ImageUrl("http://example.com/image.png"),
            creationDate);
    String expectedJson =
        "{\"id\":{\"value\":\"cbc76841-8508-46bf-a86e-0cf0572794d0\"},"
            + "\"name\":{\"value\":\"Software Development\"},"
            + "\"slug\":{\"value\":\"software-development\"},"
            + "\"defaultImage\":{\"value\":\"http://example.com/image.png\"},"
            + "\"createdAt\":1735725600.000000000}";

    // When
    String json = serializer.serialize(event);

    // Then
    assertEquals(expectedJson, json);
  }

  @Test
  void testSerialize_ShouldThrowExceptionWhenSerializationFails() throws Exception {
    // Given
    String uuid = "cbc76841-8508-46bf-a86e-0cf0572794d0";
    Instant creationDate = Instant.parse("2025-01-01T10:00:00Z");
    DomainEvent event =
        new CategoryCreated(
            new CategoryId(UUID.fromString(uuid)),
            new CategoryName("Software Development"),
            new Slug("software-development"),
            new ImageUrl("http://example.com/image.png"),
            creationDate);

    when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

    // When & Then
    EventSerializationException exception =
        assertThrows(EventSerializationException.class, () -> serializer.serialize(event));
    assertTrue(exception.getMessage().contains("Failed to serialize domain event"));
  }

  @Test
  void testDeserialize_ShouldReturnCorrectDomainEvent() throws Exception {
    // Given
    String uuid = "cbc76841-8508-46bf-a86e-0cf0572794d0";
    Instant creationDate = Instant.parse("2025-01-01T10:00:00Z");
    DomainEvent expectedEvent =
        new CategoryCreated(
            new CategoryId(UUID.fromString(uuid)),
            new CategoryName("Software Development"),
            new Slug("software-development"),
            new ImageUrl("http://example.com/image.png"),
            creationDate);
    String payloadJson =
        "{\"id\":{\"value\":\"cbc76841-8508-46bf-a86e-0cf0572794d0\"},"
            + "\"name\":{\"value\":\"Software Development\"},"
            + "\"slug\":{\"value\":\"software-development\"},"
            + "\"defaultImage\":{\"value\":\"http://example.com/image.png\"},"
            + "\"createdAt\":1735725600.000000000}";

    // When
    DomainEvent event = serializer.deserialize("CategoryCreated", payloadJson);

    // Then
    assertNotNull(event);
    assertEquals(CategoryCreated.class, event.getClass());
    assertEquals(expectedEvent, event);
  }

  @Test
  void testDeserialize_ShouldThrowExceptionForUnknownEventType() {
    // Given
    String eventType = "UnknownEvent";
    String payloadJson = "{}";

    // When & Then
    EventSerializationException exception =
        assertThrows(
            EventSerializationException.class,
            () -> {
              serializer.deserialize(eventType, payloadJson);
            });
    assertTrue(exception.getMessage().contains("Unknown eventType 'UnknownEvent'"));
  }

  @Test
  void testDeserialize_ShouldThrowExceptionWhenDeserializationFails() {
    // Given
    String eventType = "CategoryCreated";
    String payloadJson =
        "{\"id\":{\"value\":\"cbc76841-8508-46bf-a86e-0cf0572794d0\"},"
            + "\"name\":{\"value\":\"Software Development\"},"
            + "\"slug\":{\"value\":\"software-development\"},"
            + "\"defaultImage\":{\"value\":\"http://example.com/image.png\"},"
            + "\"status\":\"ACTIVE\","
            + "\"createdAt\":1735725600.000000000";

    // When & Then
    EventSerializationException exception =
        assertThrows(
            EventSerializationException.class,
            () -> {
              serializer.deserialize(eventType, payloadJson);
            });
    assertTrue(exception.getMessage().contains("Failed to deserialize eventType CategoryCreated"));
  }
}
