package com.luisjrz96.blog.domain.blog.tag;

import java.time.Instant;

import com.luisjrz96.blog.domain.AggregateRoot;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.tag.events.TagArchived;
import com.luisjrz96.blog.domain.blog.tag.events.TagCreated;
import com.luisjrz96.blog.domain.blog.tag.events.TagUpdated;
import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.shared.Slug;

public class Tag extends AggregateRoot {
  private TagId id;
  private TagName name;
  private Slug slug;
  private TagStatus status;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant archivedAt;

  public static Tag create(TagName name) {
    Tag tag = new Tag();
    Instant now = Instant.now();
    tag.applyChange(
        new TagCreated(TagId.newId(), name, new Slug(name.value()), TagStatus.ACTIVE, now));
    return tag;
  }

  public void update(TagName name) {
    Instant now = Instant.now();
    this.applyChange(new TagUpdated(this.id, name, new Slug(name.value()), now));
  }

  public void archive() {
    if (this.status == TagStatus.ARCHIVED) {
      throw new DomainException("Tag already archived");
    }
    Instant now = Instant.now();
    this.applyChange(new TagArchived(this.id, now));
  }

  @Override
  protected void when(DomainEvent event) {
    switch (event) {
      case TagCreated e -> apply(e);
      case TagUpdated e -> apply(e);
      case TagArchived e -> apply(e);
      default -> throw new DomainException("Unhandled event type for Tag: " + event);
    }
  }

  private void apply(TagCreated e) {
    this.id = e.id();
    this.name = e.name();
    this.slug = e.slug();
    this.status = e.status();
    this.createdAt = e.createdAt();
  }

  private void apply(TagUpdated e) {
    this.name = e.name();
    this.slug = e.slug();
    this.updatedAt = e.updatedAt();
  }

  private void apply(TagArchived e) {
    this.status = TagStatus.ARCHIVED;
    this.archivedAt = e.archivedAt();
  }

  public TagId getId() {
    return id;
  }

  public TagName getName() {
    return name;
  }

  public Slug getSlug() {
    return slug;
  }

  public TagStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Instant getArchivedAt() {
    return archivedAt;
  }
}
