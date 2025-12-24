package com.luisjrz96.blog.domain.blog.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.tag.events.TagArchived;
import com.luisjrz96.blog.domain.blog.tag.events.TagCreated;
import com.luisjrz96.blog.domain.blog.tag.events.TagUpdated;
import com.luisjrz96.blog.domain.shared.Slug;

class TagTest {

  private TagName tagName(String v) {
    return new TagName(v);
  }

  @Test
  void create_shouldInitializeActiveTag_andEmitTagCreated() {
    // given
    TagName name = tagName("ddd");

    // when
    Tag tag = Tag.create(name);

    // then: state
    assertNotNull(tag.getId(), "id should be assigned on create()");
    assertEquals(name, tag.getName(), "name should match input");
    assertEquals(new Slug(name.value()), tag.getSlug(), "slug should derive from name");
    assertEquals(TagStatus.ACTIVE, tag.getStatus(), "new tags should start ACTIVE");

    assertNotNull(tag.getCreatedAt(), "createdAt should be set");
    assertNull(tag.getUpdatedAt(), "updatedAt should be null on first create");
    assertNull(tag.getArchivedAt(), "archivedAt should be null on first create");

    // then: emitted event
    var events = tag.getUncommittedEvents();
    assertEquals(1, events.size(), "create() should emit exactly one event");
    assertInstanceOf(TagCreated.class, events.getFirst());

    TagCreated created = (TagCreated) events.getFirst();
    assertEquals(tag.getId(), created.id());
    assertEquals(name, created.name());
    assertEquals(new Slug(name.value()), created.slug());
    assertEquals(TagStatus.ACTIVE, tag.getStatus());
    assertNotNull(created.createdAt(), "TagCreated should carry createdAt timestamp");
  }

  @Test
  void update_shouldMutateNameAndSlug_andEmitTagUpdated() {
    // given
    Tag tag = Tag.create(tagName("ddd"));
    tag.markEventsAsCommitted(); // clear TagCreated so we isolate update()

    // when
    TagName newName = tagName("architecture");
    Instant beforeUpdate = Instant.now();

    tag.update(newName);

    // then: state
    assertEquals(newName, tag.getName(), "name should be updated");
    assertEquals(new Slug(newName.value()), tag.getSlug(), "slug should follow updated name");

    assertNotNull(tag.getUpdatedAt(), "updatedAt should be set by update()");
    assertFalse(tag.getUpdatedAt().isBefore(beforeUpdate), "updatedAt should be now or later");

    // status should not change on update
    assertEquals(TagStatus.ACTIVE, tag.getStatus(), "updating a tag should not archive it");
    assertNull(tag.getArchivedAt(), "archivedAt should still be null after update");

    // then: emitted event
    var events = tag.getUncommittedEvents();
    assertEquals(1, events.size(), "update() should emit exactly one event");
    assertInstanceOf(TagUpdated.class, events.getFirst());

    TagUpdated updated = (TagUpdated) events.getFirst();
    assertEquals(tag.getId(), updated.id());
    assertEquals(newName, updated.name());
    assertEquals(new Slug(newName.value()), updated.slug());
    assertNotNull(updated.updatedAt(), "TagUpdated should carry updatedAt");
  }

  @Test
  void archive_shouldSetStatusArchived_andEmitTagArchived() {
    // given
    Tag tag = Tag.create(tagName("ddd"));
    tag.markEventsAsCommitted(); // ignore TagCreated for this test

    // when
    Instant beforeArchive = Instant.now();
    tag.archive();

    // then: state
    assertEquals(
        TagStatus.ARCHIVED, tag.getStatus(), "status should become ARCHIVED after archive()");

    assertNotNull(tag.getArchivedAt(), "archivedAt should be set on archive()");
    assertFalse(
        tag.getArchivedAt().isBefore(beforeArchive),
        "archivedAt should not be before the moment we archived");

    // updatedAt should not change here
    assertNull(tag.getUpdatedAt(), "archive() should not set updatedAt");

    // then: emitted event
    var events = tag.getUncommittedEvents();
    assertEquals(1, events.size(), "archive() should emit exactly one event");
    assertInstanceOf(TagArchived.class, events.getFirst());

    TagArchived archived = (TagArchived) events.getFirst();
    assertNotNull(archived.archivedAt(), "TagArchived should carry archivedAt timestamp");
  }

  @Test
  void canRehydrateFromPastEvents_usingReplayEvent() {
    // given a fake event history:
    TagId id = TagId.newId();
    TagName originalName = tagName("ddd");
    Slug originalSlug = new Slug(originalName.value());
    Instant createdAt = Instant.parse("2025-10-24T10:00:00Z");

    TagCreated created = new TagCreated(id, originalName, originalSlug, createdAt);

    TagName renamed = tagName("architecture");
    Slug renamedSlug = new Slug(renamed.value());
    Instant updatedAt = Instant.parse("2025-10-24T11:00:00Z");

    TagUpdated updated = new TagUpdated(id, renamed, renamedSlug, updatedAt);

    Instant archivedAt = Instant.parse("2025-10-24T12:00:00Z");
    TagArchived archived = new TagArchived(id, archivedAt);

    List<DomainEvent> history = List.of(created, updated, archived);

    // when: rebuild aggregate completely from events (event sourcing flow)
    Tag tag = new Tag();
    int version = 0;
    for (DomainEvent e : history) {
      tag.replayEvent(e);
      version++;
    }
    tag.setVersion(version);

    // then: final state matches last applied event
    assertEquals(id, tag.getId());
    assertEquals(renamed, tag.getName());
    assertEquals(renamedSlug, tag.getSlug());
    assertEquals(TagStatus.ARCHIVED, tag.getStatus());
    assertEquals(createdAt, tag.getCreatedAt(), "createdAt should come from TagCreated");
    assertEquals(updatedAt, tag.getUpdatedAt(), "updatedAt should come from TagUpdated");
    assertEquals(archivedAt, tag.getArchivedAt(), "archivedAt should come from TagArchived");

    assertEquals(version, tag.getVersion(), "version should equal number of applied events");

    // and rehydration should *not* leave new uncommitted events
    assertTrue(
        tag.getUncommittedEvents().isEmpty(),
        "rehydration via replayEvent() must not create new uncommitted events");
  }
}
