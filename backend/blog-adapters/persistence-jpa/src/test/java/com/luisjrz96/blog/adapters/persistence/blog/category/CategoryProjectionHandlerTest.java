package com.luisjrz96.blog.adapters.persistence.blog.category;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.luisjrz96.blog.adapters.persistence.exception.UnknownEventException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.blog.category.events.CategoryArchived;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryUpdated;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

@ExtendWith(MockitoExtension.class)
class CategoryProjectionHandlerTest {

  @Mock private JdbcTemplate jdbc;

  @InjectMocks private CategoryProjectionHandler handler;

  @Test
  void shouldInsertCategoryOnCreatedEvent() {
    // given
    Instant now = Instant.parse("2025-01-01T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    CategoryCreated event =
        new CategoryCreated(
            new CategoryId(uuid),
            new CategoryName("Tech"),
            new Slug("tech"),
            new ImageUrl("http://example.com/image.png"),
            now);

    int version = 1;

    // when
    handler.project(event, version);

    // then
    verify(jdbc)
        .update(
            contains("INSERT INTO category_view"),
            eq(String.valueOf(uuid)),
            eq("Tech"),
            eq("tech"),
            eq("http://example.com/image.png"),
            eq(CategoryStatus.ACTIVE.name()),
            eq(Timestamp.from(now)),
            eq(version));
  }

  @Test
  void shouldUpdateCategoryOnUpdatedEvent() {
    // given
    Instant now = Instant.parse("2025-01-02T10:00:00Z");
    UUID uuid = UUID.randomUUID();

    CategoryUpdated event =
        new CategoryUpdated(
            new CategoryId(uuid),
            new CategoryName("Tech Updated"),
            new Slug("tech-updated"),
            new ImageUrl("http://example.com/image2.png"),
            now);

    int version = 2;

    // when
    handler.project(event, version);

    // then
    verify(jdbc)
        .update(
            contains("UPDATE category_view"),
            eq("Tech Updated"),
            eq("tech-updated"),
            eq("http://example.com/image2.png"),
            eq(Timestamp.from(now)),
            eq(version),
            eq(String.valueOf(uuid)),
            eq(version));
  }

  @Test
  void shouldArchiveCategoryOnArchivedEvent() {
    // given
    Instant now = Instant.parse("2025-01-03T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    CategoryArchived event = new CategoryArchived(new CategoryId(uuid), now);

    int version = 3;

    // when
    handler.project(event, version);

    // then
    verify(jdbc)
        .update(
            contains("SET status = 'ARCHIVED'"),
            eq(Timestamp.from(now)),
            eq(version),
            eq(String.valueOf(uuid)),
            eq(version));
  }

  @Test
  void shouldThrowExceptionForUnknownEvent() {
    // given
    DomainEvent unknownEvent = mock(DomainEvent.class);

    // when + then
    assertThrows(UnknownEventException.class, () -> handler.project(unknownEvent, 1));

    verifyNoInteractions(jdbc);
  }
}
