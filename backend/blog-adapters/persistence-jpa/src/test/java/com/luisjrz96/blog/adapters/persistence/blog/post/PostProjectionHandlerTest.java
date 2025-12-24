package com.luisjrz96.blog.adapters.persistence.blog.post;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.events.PostArchived;
import com.luisjrz96.blog.domain.blog.post.events.PostCreated;
import com.luisjrz96.blog.domain.blog.post.events.PostPublished;
import com.luisjrz96.blog.domain.blog.post.events.PostUpdated;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

@ExtendWith(MockitoExtension.class)
class PostProjectionHandlerTest {

  @Mock private JdbcTemplate jdbc;

  @InjectMocks private PostProjectionHandler handler;

  @Test
  void shouldInsertPostOnCreatedEvent() {
    // given
    Instant createdAt = Instant.parse("2025-01-01T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    AuthorId authorId = new AuthorId(UUID.randomUUID());
    PostCreated event =
        new PostCreated(
            new PostId(uuid),
            authorId,
            new Title("My First Post"),
            new Slug("my-first-post"),
            new Summary("This is the content of my first post."),
            new Markdown("# Hello World\nThis is my first blog post."),
            new CategoryId(UUID.randomUUID()),
            List.of(new TagId(UUID.randomUUID())),
            new ImageUrl("http://example.com/post-image.png"),
            createdAt);

    int version = 1;

    // when
    handler.project(event, version);

    // then
    verify(jdbc)
        .update(
            contains("INSERT INTO post_view"),
            eq(String.valueOf(uuid)),
            eq(String.valueOf(authorId.value())),
            eq("My First Post"),
            eq("my-first-post"),
            eq("This is the content of my first post."),
            eq("# Hello World\nThis is my first blog post."),
            eq("http://example.com/post-image.png"),
            eq("" + event.categoryId().value()),
            eq("DRAFT"),
            eq(Timestamp.from(createdAt)),
            eq(version));
  }

  @Test
  void shouldUpdatePostOnUpdatedEvent() {
    // given
    Instant updatedAt = Instant.parse("2025-01-02T10:00:00Z");
    UUID uuid = UUID.randomUUID();

    PostUpdated event =
        new PostUpdated(
            new PostId(uuid),
            new Title("My First Post - Updated"),
            new Slug("my-first-post-updated"),
            new Summary("This is the updated content of my first post."),
            new Markdown("# Hello World\nThis is my updated blog post."),
            new CategoryId(UUID.randomUUID()),
            List.of(new TagId(UUID.randomUUID())),
            new ImageUrl("http://example.com/post-image-updated.png"),
            updatedAt);

    int version = 2;

    // when
    handler.project(event, version);

    verify(jdbc)
        .update(
            contains("UPDATE post_view"),
            eq("My First Post - Updated"),
            eq("my-first-post-updated"),
            eq("This is the updated content of my first post."),
            eq("# Hello World\nThis is my updated blog post."),
            eq("http://example.com/post-image-updated.png"),
            eq("" + event.categoryId().value()),
            eq(Timestamp.from(updatedAt)),
            eq(version),
            eq(String.valueOf(uuid)),
            eq(version));
  }

  @Test
  void shouldArchivePostOnArchivedEvent() {
    // given
    Instant archivedAt = Instant.parse("2025-01-03T10:00:00Z");
    UUID uuid = UUID.randomUUID();

    PostArchived event = new PostArchived(new PostId(uuid), archivedAt);

    int version = 3;

    // when
    handler.project(event, version);

    verify(jdbc)
        .update(
            contains("SET status = 'ARCHIVED'"),
            eq(Timestamp.from(archivedAt)),
            eq(version),
            eq(String.valueOf(uuid)),
            eq(version));
  }

  @Test
  void shouldPublishPostOnPublishedEvent() {
    // given
    Instant timestamp = Instant.parse("2025-01-04T10:00:00Z");
    UUID uuid = UUID.randomUUID();

    PostPublished event = new PostPublished(new PostId(uuid), timestamp, timestamp);

    int version = 4;

    // when
    handler.project(event, version);

    verify(jdbc)
        .update(
            contains("SET status = 'PUBLISHED'"),
            eq(Timestamp.from(timestamp)),
            eq(Timestamp.from(timestamp)),
            eq(version),
            eq(String.valueOf(uuid)),
            eq(version));
  }
}
