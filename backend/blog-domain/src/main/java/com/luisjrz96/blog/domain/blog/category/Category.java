package com.luisjrz96.blog.domain.blog.category;

import java.time.Instant;

import com.luisjrz96.blog.domain.AggregateRoot;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.events.CategoryArchived;
import com.luisjrz96.blog.domain.blog.category.events.CategoryCreated;
import com.luisjrz96.blog.domain.blog.category.events.CategoryUpdated;
import com.luisjrz96.blog.domain.exception.DomainException;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

public class Category extends AggregateRoot {
  private CategoryId id;
  private CategoryName name;
  private Slug slug;
  private ImageUrl defaultImage;
  private CategoryStatus status;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant archivedAt;

  public static Category create(CategoryName categoryName, ImageUrl defaultImage) {
    Category category = new Category();
    Instant now = Instant.now();
    category.applyChange(
        new CategoryCreated(
            CategoryId.newId(), categoryName, new Slug(categoryName.value()), defaultImage, now));
    return category;
  }

  public void update(CategoryName categoryName, ImageUrl defaultImage) {
    Instant now = Instant.now();
    this.applyChange(
        new CategoryUpdated(
            this.id, categoryName, new Slug(categoryName.value()), defaultImage, now));
  }

  public void archive() {
    if (this.status == CategoryStatus.ARCHIVED) {
      throw new DomainException("Category already archived");
    }
    Instant now = Instant.now();
    this.applyChange(new CategoryArchived(this.id, now));
  }

  @Override
  protected void when(DomainEvent event) {
    switch (event) {
      case CategoryCreated e -> apply(e);
      case CategoryUpdated e -> apply(e);
      case CategoryArchived e -> apply(e);
      default -> throw new DomainException("Unhandled event type for Category: " + event);
    }
  }

  private void apply(CategoryCreated e) {
    this.id = e.id();
    this.name = e.name();
    this.defaultImage = e.defaultImage();
    this.slug = e.slug();
    this.status = CategoryStatus.ACTIVE;
    this.createdAt = e.createdAt();
  }

  private void apply(CategoryUpdated e) {
    this.name = e.name();
    this.slug = e.slug();
    this.defaultImage = e.defaultImage();
    this.updatedAt = e.updatedAt();
  }

  private void apply(CategoryArchived e) {
    this.status = CategoryStatus.ARCHIVED;
    this.archivedAt = e.archivedAt();
  }

  public CategoryId getId() {
    return id;
  }

  public CategoryName getName() {
    return name;
  }

  public Slug getSlug() {
    return slug;
  }

  public ImageUrl getDefaultImage() {
    return defaultImage;
  }

  public CategoryStatus getStatus() {
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
