package com.luisjrz96.blog.domain.blog.category;

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
import com.luisjrz96.blog.domain.blog.category.events.CategoryArchived;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryUpdated;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

class CategoryTest {

  private CategoryName name(String v) {
    return new CategoryName(v);
  }

  private ImageUrl img(String v) {
    return new ImageUrl(v);
  }

  @Test
  void create_shouldInitializeCategoryActive_andEmitCategoryCreated() {
    // given
    CategoryName catName = name("Backend");
    ImageUrl defaultImage = img("https://cdn.example.com/backend.png");

    // when
    Category category = Category.create(catName, defaultImage);

    // then: state
    assertNotNull(category.getId(), "id should be assigned on create()");
    assertEquals(catName, category.getName());
    assertEquals(defaultImage, category.getDefaultImage());
    assertEquals(CategoryStatus.ACTIVE, category.getStatus(), "new category should start ACTIVE");

    assertEquals(
        new Slug(catName.value()), category.getSlug(), "slug should be derived from category name");

    assertNotNull(category.getCreatedAt(), "createdAt should be set on creation");
    assertNull(category.getUpdatedAt(), "updatedAt should be null initially");
    assertNull(category.getArchivedAt(), "archivedAt should be null initially");

    // then: emitted event
    var events = category.getUncommittedEvents();
    assertEquals(1, events.size(), "create() should emit exactly one event");
    assertInstanceOf(CategoryCreated.class, events.getFirst());

    CategoryCreated created = (CategoryCreated) events.getFirst();
    assertEquals(category.getId(), created.id());
    assertEquals(catName, created.name());
    assertEquals(defaultImage, created.defaultImage());
    assertEquals(new Slug(catName.value()), created.slug());
    assertEquals(CategoryStatus.ACTIVE, category.getStatus());
    assertNotNull(created.createdAt(), "CategoryCreated should carry createdAt timestamp");
  }

  @Test
  void update_shouldEmitCategoryUpdated_andMutateEditableFields() {
    // given: start with a created category
    Category category =
        Category.create(name("Backend"), img("https://cdn.example.com/backend.png"));

    category.markEventsAsCommitted(); // clear CategoryCreated

    // when: update fields
    CategoryName newName = name("Platform Engineering");
    ImageUrl newImage = img("https://cdn.example.com/platform.png");

    Instant beforeUpdate = Instant.now();

    category.update(newName, newImage);

    // then: state after update
    assertEquals(newName, category.getName(), "name should be updated");
    assertEquals(newImage, category.getDefaultImage(), "defaultImage should be updated");
    assertEquals(new Slug(newName.value()), category.getSlug(), "slug should track updated name");

    assertNotNull(category.getUpdatedAt(), "updatedAt should be set on update()");
    assertFalse(
        category.getUpdatedAt().isBefore(beforeUpdate), "updatedAt should be 'now' or later");

    // unchanged fields
    assertEquals(CategoryStatus.ACTIVE, category.getStatus(), "status should NOT change on update");
    assertNotNull(category.getCreatedAt(), "createdAt should NOT be cleared on update");
    assertNull(category.getArchivedAt(), "archivedAt should still be null");

    // then: emitted event
    var events = category.getUncommittedEvents();
    assertEquals(1, events.size(), "update should emit exactly one event");
    assertInstanceOf(CategoryUpdated.class, events.getFirst());

    CategoryUpdated updated = (CategoryUpdated) events.getFirst();
    assertEquals(category.getId(), updated.id());
    assertEquals(newName, updated.name());
    assertEquals(new Slug(newName.value()), updated.slug());
    assertEquals(newImage, updated.defaultImage());
    assertNotNull(updated.updatedAt(), "CategoryUpdated should carry updatedAt timestamp");
  }

  @Test
  void archive_shouldMarkCategoryArchived_andEmitCategoryArchived() {
    // given
    Category category =
        Category.create(name("Backend"), img("https://cdn.example.com/backend.png"));
    category.markEventsAsCommitted(); // ignore CategoryCreated for this assert

    // when
    Instant beforeArchive = Instant.now();
    category.archive();

    // then: state
    assertEquals(
        CategoryStatus.ARCHIVED,
        category.getStatus(),
        "status should become ARCHIVED after archive()");

    assertNotNull(category.getArchivedAt(), "archivedAt should be set when archiving");
    assertFalse(
        category.getArchivedAt().isBefore(beforeArchive), "archivedAt should be 'now' or later");

    // updatedAt should not be touched by archive()
    // (only update() should set updatedAt)
    assertNull(category.getUpdatedAt(), "archive() should not set updatedAt");

    // then: emitted event
    var events = category.getUncommittedEvents();
    assertEquals(1, events.size(), "archive() should emit exactly one event");
    assertInstanceOf(CategoryArchived.class, events.getFirst());

    CategoryArchived archived = (CategoryArchived) events.getFirst();
    assertNotNull(archived.archivedAt(), "CategoryArchived should include archivedAt timestamp");
  }

  @Test
  void canRehydrateFromPastEvents_usingReplayEvent() {
    // given: fake persisted history
    CategoryId id = CategoryId.newId();
    CategoryName originalName = name("Backend");
    ImageUrl originalImage = img("https://cdn.example.com/backend.png");
    Slug originalSlug = new Slug(originalName.value());
    Instant createdAt = Instant.parse("2025-10-24T10:00:00Z");

    CategoryCreated created =
        new CategoryCreated(id, originalName, originalSlug, originalImage, createdAt);

    CategoryName renamed = name("Platform Engineering");
    ImageUrl newImage = img("https://cdn.example.com/platform.png");
    Slug newSlug = new Slug(renamed.value());
    Instant updatedAt = Instant.parse("2025-10-24T11:00:00Z");

    CategoryUpdated updated = new CategoryUpdated(id, renamed, newSlug, newImage, updatedAt);

    Instant archivedAt = Instant.parse("2025-10-24T12:00:00Z");
    CategoryArchived archived = new CategoryArchived(id, archivedAt);

    List<DomainEvent> history = List.of(created, updated, archived);

    // when: rebuild aggregate by replaying
    Category category = new Category();
    int v = 0;
    for (DomainEvent e : history) {
      category.replayEvent(e);
      v++;
    }
    category.setVersion(v);

    // then: final state reflects last applied event
    assertEquals(id, category.getId());
    assertEquals(renamed, category.getName());
    assertEquals(newSlug, category.getSlug());
    assertEquals(newImage, category.getDefaultImage());
    assertEquals(CategoryStatus.ARCHIVED, category.getStatus());
    assertEquals(createdAt, category.getCreatedAt(), "createdAt should come from first event");
    assertEquals(updatedAt, category.getUpdatedAt(), "updatedAt should come from CategoryUpdated");
    assertEquals(
        archivedAt, category.getArchivedAt(), "archivedAt should come from CategoryArchived");

    assertEquals(
        v, category.getVersion(), "version should equal number of historical events applied");

    // and: rehydration should not create new uncommitted events
    assertTrue(
        category.getUncommittedEvents().isEmpty(), "rehydration should not leave pending events");
  }
}
