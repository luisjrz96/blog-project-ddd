package com.luisjrz96.blog.adapters.persistence.blog.tag;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.blog.tag.events.TagArchived;
import com.luisjrz96.blog.domain.blog.tag.events.TagCreated;
import com.luisjrz96.blog.domain.blog.tag.events.TagUpdated;
import com.luisjrz96.blog.domain.shared.Slug;

@ExtendWith(MockitoExtension.class)
class TagProjectionHandlerTest {

  @Mock private JdbcTemplate jdbc;

  @InjectMocks private TagProjectionHandler handler;

  @Test
  void shouldInsertTagOnCreatedEvent() {
    // given
    Instant now = Instant.parse("2025-01-01T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    TagCreated event = new TagCreated(new TagId(uuid), new TagName("Tech"), new Slug("tech"), now);

    int version = 1;

    // when
    handler.project(event, version);

    // then
    verify(jdbc)
        .update(
            contains("INSERT INTO tag_view"),
            eq(String.valueOf(uuid)),
            eq("Tech"),
            eq("tech"),
            eq(CategoryStatus.ACTIVE.name()),
            eq(Timestamp.from(now)),
            eq(version));
  }

  @Test
  void shouldUpdateTagOnUpdatedEvent() {
    // given
    Instant now = Instant.parse("2025-01-02T10:00:00Z");
    UUID uuid = UUID.randomUUID();

    TagUpdated event =
        new TagUpdated(new TagId(uuid), new TagName("Tech Updated"), new Slug("tech-updated"), now);

    int version = 2;

    // when
    handler.project(event, version);

    // then
    verify(jdbc)
        .update(
            contains("UPDATE tag_view"),
            eq("Tech Updated"),
            eq("tech-updated"),
            eq(Timestamp.from(now)),
            eq(version),
            eq(String.valueOf(uuid)),
            eq(version));
  }

  @Test
  void shouldArchiveTagOnArchivedEvent() {
    // given
    Instant now = Instant.parse("2025-01-03T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    TagArchived event = new TagArchived(new TagId(uuid), now);

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
