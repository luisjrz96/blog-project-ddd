package com.luisjrz96.blog.domain.blog.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.events.PostArchived;
import com.luisjrz96.blog.domain.blog.post.events.PostCreated;
import com.luisjrz96.blog.domain.blog.post.events.PostPublished;
import com.luisjrz96.blog.domain.blog.post.events.PostUpdated;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

class PostTest {

  // --- helper constructors for VOs used in tests ---
  private AuthorId author(UUID v) {
    return new AuthorId(v);
  }

  private Title title(String v) {
    return new Title(v);
  }

  private Summary summary(String v) {
    return new Summary(v);
  }

  private Markdown body(String v) {
    return new Markdown(v);
  }

  private CategoryId category(UUID v) {
    return new CategoryId(v);
  }

  private TagId tag(UUID v) {
    return new TagId(v);
  }

  private List<TagId> tags() {
    return List.of(new TagId(UUID.randomUUID()), new TagId(UUID.randomUUID()));
  }

  private ImageUrl img(String v) {
    return new ImageUrl(v);
  }

  // ---------- CREATE ----------

  @Test
  void create_shouldInitializeDraftPost_andEmitPostCreated() {
    // given
    AuthorId authorId = author(UUID.randomUUID());
    Title title = title("Event Sourcing in Practice");
    Summary summary = summary("How to design aggregates with events");
    Markdown body = body("## Hello\nBody here");
    CategoryId categoryId = category(UUID.randomUUID());
    List<TagId> tagIds = tags();
    ImageUrl cover = img("https://cdn.example.com/covers/es.png");

    // when
    Post post = Post.create(authorId, title, summary, body, categoryId, tagIds, cover);

    // then: state
    assertNotNull(post.getId(), "post id should have been generated");
    assertEquals(authorId, post.getAuthorId());
    assertEquals(title, post.getTitle());
    assertEquals(summary, post.getSummary());
    assertEquals(body, post.getBody());
    assertEquals(PostStatus.DRAFT, post.getStatus(), "new post should start as DRAFT");
    assertEquals(categoryId, post.getCategoryId());
    assertEquals(tagIds, post.getTagIds());
    assertEquals(cover, post.getCoverImage());
    assertNotNull(post.getSlug(), "slug should be generated from title");
    assertNotNull(post.getCreatedAt(), "createdAt should be set");
    assertNull(post.getUpdatedAt(), "updatedAt should be null initially");
    assertNull(post.getPublishedAt(), "publishedAt should be null initially");
    assertNull(post.getArchivedAt(), "archivedAt should be null initially");

    // then: emitted event
    var events = post.getUncommittedEvents();
    assertEquals(1, events.size(), "create() should emit exactly one event");
    assertInstanceOf(PostCreated.class, events.getFirst());

    PostCreated created = (PostCreated) events.getFirst();
    assertEquals(post.getId(), created.postId());
    assertEquals(authorId, created.authorId());
    assertEquals(title, created.title());
    assertEquals(summary, created.summary());
    assertEquals(body, created.body());
    assertEquals(PostStatus.DRAFT, created.status());
    assertEquals(categoryId, created.categoryId());
    assertEquals(tagIds, created.tagIds());
    assertEquals(cover, created.coverImage());
    assertEquals(post.getSlug(), created.slug(), "slug in event should match aggregate slug");
    assertEquals(post.getCreatedAt(), created.createdAt(), "createdAt should match");
  }

  // ---------- UPDATE ----------

  @Test
  void update_shouldEmitPostUpdated_andMutateEditableFields() {
    // given: create initial post
    Post post =
        Post.create(
            author(UUID.randomUUID()),
            title("Initial title"),
            summary("initial summary"),
            body("initial body"),
            category(UUID.randomUUID()),
            tags(),
            img("https://cdn.example.com/covers/initial.png"));

    post.markEventsAsCommitted(); // ignore PostCreated for this test

    // when: update post metadata/content
    Title newTitle = title("Refined title");
    Summary newSummary = summary("refined summary");
    Markdown newBody = body("refined body with more detail");
    CategoryId newCategory = category(UUID.randomUUID());
    List<TagId> newTags = List.of(tag(UUID.randomUUID()), tag(UUID.randomUUID()));
    ImageUrl newCover = img("https://cdn.example.com/covers/refined.png");

    Instant beforeUpdate = Instant.now();
    post.update(newTitle, newSummary, newBody, newCategory, newTags, newCover);

    // then: state after update
    assertEquals(newTitle, post.getTitle());
    assertEquals(newSummary, post.getSummary());
    assertEquals(newBody, post.getBody());
    assertEquals(newCategory, post.getCategoryId());
    assertEquals(newTags, post.getTagIds());
    assertEquals(newCover, post.getCoverImage());

    assertNotNull(post.getSlug(), "slug should still be set");
    assertEquals(
        new Slug(newTitle.value()), post.getSlug(), "slug should be updated to reflect new title");

    assertNotNull(post.getUpdatedAt(), "updatedAt should be set when updating");
    assertFalse(post.getUpdatedAt().isBefore(beforeUpdate), "updatedAt should be now or later");

    // status should not change on update
    assertEquals(PostStatus.DRAFT, post.getStatus(), "update() should not change status");
    assertNull(post.getPublishedAt(), "publish date should still be null after update");
    assertNull(post.getArchivedAt(), "archivedAt should still be null after update");

    // then: one new event emitted
    var events = post.getUncommittedEvents();
    assertEquals(1, events.size(), "update() should emit exactly one event");
    assertInstanceOf(PostUpdated.class, events.getFirst());

    PostUpdated updated = (PostUpdated) events.getFirst();
    assertEquals(post.getId(), updated.postId());
    assertEquals(newTitle, updated.title());
    assertEquals(newSummary, updated.summary());
    assertEquals(newBody, updated.body());
    assertEquals(newCategory, updated.categoryId());
    assertEquals(newTags, updated.tagIds());
    assertEquals(newCover, updated.coverImage());
    assertEquals(post.getSlug(), updated.slug(), "event slug should match aggregate slug");
    assertEquals(
        post.getUpdatedAt(),
        updated.updatedAt(),
        "event updatedAt should match aggregate updatedAt");
  }

  // ---------- PUBLISH ----------

  @Test
  void publish_shouldTransitionToPublished_andEmitPostPublished() {
    // given
    Post post =
        Post.create(
            author(UUID.randomUUID()),
            title("Draft post"),
            summary("desc"),
            body("some body"),
            category(UUID.randomUUID()),
            tags(),
            img("https://cdn.example.com/covers/draft.png"));
    post.markEventsAsCommitted(); // clear PostCreated

    // sanity: starts as DRAFT
    assertEquals(PostStatus.DRAFT, post.getStatus());

    // when
    Instant beforePublish = Instant.now();
    post.publish();

    // then: state
    assertEquals(PostStatus.PUBLISHED, post.getStatus(), "status should now be PUBLISHED");
    assertNotNull(post.getPublishedAt(), "publishedAt should be set");
    assertNotNull(post.getUpdatedAt(), "updatedAt should also be set when publishing");
    assertFalse(
        post.getPublishedAt().isBefore(beforePublish), "publishedAt should be now or later");
    assertFalse(post.getUpdatedAt().isBefore(beforePublish), "updatedAt should be now or later");

    // then: emitted event
    var events = post.getUncommittedEvents();
    assertEquals(1, events.size(), "publish() should emit exactly one event");
    assertInstanceOf(PostPublished.class, events.getFirst());

    PostPublished pub = (PostPublished) events.getFirst();
    assertEquals(post.getId(), pub.postId());
    assertEquals(post.getPublishedAt(), pub.publishedAt());
    assertEquals(post.getUpdatedAt(), pub.updatedAt());
  }

  @Test
  void publish_shouldThrowIfAlreadyPublished() {
    // given
    Post post =
        Post.create(
            author(UUID.randomUUID()),
            title("Hello"),
            summary("s"),
            body("b"),
            category(UUID.randomUUID()),
            tags(),
            img("https://blog.com/image/img1.png"));

    post.markEventsAsCommitted();
    post.publish();
    post.markEventsAsCommitted();

    // when / then
    DomainException ex =
        assertThrows(
            DomainException.class, post::publish, "publishing twice should not be allowed");
    assertTrue(ex.getMessage().contains("already published"));
  }

  @Test
  void publish_shouldThrowIfArchived() {
    // given
    Post post =
        Post.create(
            author(UUID.randomUUID()),
            title("Hello"),
            summary("s"),
            body("b"),
            category(UUID.randomUUID()),
            tags(),
            img("https://blog.com/image/img1.png"));

    post.markEventsAsCommitted();
    post.archive();
    post.markEventsAsCommitted();

    // when / then
    DomainException ex =
        assertThrows(DomainException.class, post::publish, "cannot publish an archived post");
    assertTrue(ex.getMessage().contains("Archived posts cannot be published"));
  }

  // ---------- ARCHIVE ----------

  @Test
  void archive_shouldTransitionToArchived_andEmitPostArchived() {
    // given
    Post post =
        Post.create(
            author(UUID.randomUUID()),
            title("Some Post"),
            summary("summary"),
            body("body text"),
            category(UUID.randomUUID()),
            tags(),
            img("https://blog.com/image/img1.png"));

    post.markEventsAsCommitted(); // ignore PostCreated for this check

    // when
    Instant beforeArchive = Instant.now();
    post.archive();

    // then: state
    assertEquals(PostStatus.ARCHIVED, post.getStatus());
    assertNotNull(post.getArchivedAt(), "archivedAt should be set when archiving");
    assertFalse(
        post.getArchivedAt().isBefore(beforeArchive),
        "archivedAt should not be in the past relative to call");

    assertNull(post.getUpdatedAt(), "archive() should not touch updatedAt in current design");

    // then: emitted event
    var events = post.getUncommittedEvents();
    assertEquals(1, events.size(), "archive() should emit exactly one event");
    assertInstanceOf(PostArchived.class, events.getFirst());

    PostArchived archived = (PostArchived) events.getFirst();
    assertEquals(post.getId(), archived.postId());
    assertEquals(post.getArchivedAt(), archived.archivedAt());
  }

  @Test
  void archive_shouldThrowIfAlreadyArchived() {
    // given
    Post post =
        Post.create(
            author(UUID.randomUUID()),
            title("Some Post"),
            summary("summary"),
            body("body text"),
            category(UUID.randomUUID()),
            tags(),
            img("https://blog.com/image/img1.png"));

    post.markEventsAsCommitted();
    post.archive();
    post.markEventsAsCommitted();

    // when / then
    DomainException ex =
        assertThrows(DomainException.class, post::archive, "archiving twice should not be allowed");
    assertTrue(ex.getMessage().contains("already archived"));
  }

  // ---------- REHYDRATION ----------

  @Test
  void canRehydrateFromEventHistory_usingReplayEvent() {
    // given: simulate past events from the event store

    PostId postId = PostId.newId();
    AuthorId authorId = author(UUID.randomUUID());
    Title initialTitle = title("Initial Title");
    Slug initialSlug = Slug.from(initialTitle.value());
    Summary initialSummary = summary("init summary");
    Markdown initialBody = body("init body");
    CategoryId initialCategory = category(UUID.randomUUID());
    List<TagId> initialTags = tags();
    ImageUrl initialCover = img("https://cdn.example.com/init.png");
    Instant createdAt = Instant.parse("2025-10-24T10:00:00Z");

    PostCreated createdEvent =
        new PostCreated(
            postId,
            authorId,
            initialTitle,
            initialSlug,
            initialSummary,
            initialBody,
            PostStatus.DRAFT,
            initialCategory,
            initialTags,
            initialCover,
            createdAt);

    Title updatedTitle = title("Better Title");
    Slug updatedSlug = Slug.from(updatedTitle.value());
    Summary updatedSummary = summary("better summary");
    Markdown updatedBody = body("better body");
    CategoryId updatedCategory = category(UUID.randomUUID());
    List<TagId> updatedTags = List.of(tag(UUID.randomUUID()), tag(UUID.randomUUID()));
    ImageUrl updatedCover = img("https://cdn.example.com/better.png");
    Instant updatedAt = Instant.parse("2025-10-24T11:00:00Z");

    PostUpdated updatedEvent =
        new PostUpdated(
            postId,
            updatedTitle,
            updatedSlug,
            updatedSummary,
            updatedBody,
            updatedCategory,
            updatedTags,
            updatedCover,
            updatedAt);

    Instant publishedUpdatedAt = Instant.parse("2025-10-24T12:00:00Z");
    Instant publishedAt = Instant.parse("2025-10-24T12:00:00Z");
    PostPublished publishedEvent = new PostPublished(postId, publishedUpdatedAt, publishedAt);

    // let's say post was not archived in this history
    List<DomainEvent> history = List.of(createdEvent, updatedEvent, publishedEvent);

    // when: rebuild aggregate from events
    Post post = new Post();
    int version = 0;
    for (DomainEvent e : history) {
      post.replayEvent(e); // calls when() -> apply(...)
      version++;
    }
    post.setVersion(version);

    // then: final state reflects last event
    assertEquals(postId, post.getId());
    assertEquals(authorId, post.getAuthorId());

    assertEquals(updatedTitle, post.getTitle());
    assertEquals(updatedSlug, post.getSlug());
    assertEquals(updatedSummary, post.getSummary());
    assertEquals(updatedBody, post.getBody());
    assertEquals(updatedCategory, post.getCategoryId());
    assertEquals(updatedTags, post.getTagIds());
    assertEquals(updatedCover, post.getCoverImage());

    assertEquals(PostStatus.PUBLISHED, post.getStatus(), "should now be published");
    assertEquals(createdAt, post.getCreatedAt(), "createdAt should come from PostCreated");
    assertEquals(
        publishedUpdatedAt,
        post.getUpdatedAt(),
        "updatedAt should reflect the last mutation timestamp (PostPublished)");
    assertEquals(publishedAt, post.getPublishedAt(), "publishedAt should come from PostPublished");
    assertNull(
        post.getArchivedAt(), "archivedAt should still be null because no PostArchived in history");

    assertEquals(version, post.getVersion(), "version should equal number of applied events");

    // and: replaying history should NOT create new uncommitted events
    assertTrue(
        post.getUncommittedEvents().isEmpty(),
        "rehydration via replayEvent() must not leave pending events");
  }
}
