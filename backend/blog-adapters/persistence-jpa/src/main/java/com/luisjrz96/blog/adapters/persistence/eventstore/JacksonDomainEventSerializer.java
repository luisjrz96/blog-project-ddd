package com.luisjrz96.blog.adapters.persistence.eventstore;

import java.io.IOException;
import java.util.Map;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luisjrz96.blog.adapters.persistence.exception.EventSerializationException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileCreated;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileUpdated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryArchived;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryUpdated;
import com.luisjrz96.blog.domain.blog.post.events.PostArchived;
import com.luisjrz96.blog.domain.blog.post.events.PostCreated;
import com.luisjrz96.blog.domain.blog.post.events.PostPublished;
import com.luisjrz96.blog.domain.blog.post.events.PostUpdated;
import com.luisjrz96.blog.domain.blog.tag.events.TagArchived;
import com.luisjrz96.blog.domain.blog.tag.events.TagCreated;
import com.luisjrz96.blog.domain.blog.tag.events.TagUpdated;

@Service
public class JacksonDomainEventSerializer implements DomainEventSerializer {

  private final ObjectMapper objectMapper;
  private final Map<String, Class<? extends DomainEvent>> registry;

  public JacksonDomainEventSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new JsonNullableModule());
    this.registry =
        Map.ofEntries(
            Map.entry("CategoryCreated", CategoryCreated.class),
            Map.entry("CategoryUpdated", CategoryUpdated.class),
            Map.entry("CategoryArchived", CategoryArchived.class),
            Map.entry("PostCreated", PostCreated.class),
            Map.entry("PostUpdated", PostUpdated.class),
            Map.entry("PostPublished", PostPublished.class),
            Map.entry("PostArchived", PostArchived.class),
            Map.entry("TagCreated", TagCreated.class),
            Map.entry("TagUpdated", TagUpdated.class),
            Map.entry("TagArchived", TagArchived.class),
            Map.entry("AuthorProfileCreated", AuthorProfileCreated.class),
            Map.entry("AuthorProfileUpdated", AuthorProfileUpdated.class));
  }

  @Override
  public String serialize(DomainEvent event) {
    try {
      return objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      throw new EventSerializationException(
          "Failed to serialize domain event " + event.getClass().getName(), e);
    }
  }

  @Override
  public DomainEvent deserialize(String eventType, String payloadJson) {
    Class<? extends DomainEvent> clazz = registry.get(eventType);
    if (clazz == null) {
      throw new EventSerializationException(
          "Unknown eventType '" + eventType + "'; cannot deserialize");
    }

    try {
      return objectMapper.readValue(payloadJson, clazz);
    } catch (IOException e) {
      throw new EventSerializationException(
          "Failed to deserialize eventType " + eventType + " into " + clazz.getName(), e);
    }
  }
}
