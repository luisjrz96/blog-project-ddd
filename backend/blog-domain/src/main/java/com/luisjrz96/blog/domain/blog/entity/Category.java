package com.luisjrz96.blog.domain.blog.entity;

import java.util.Objects;

import com.luisjrz96.blog.domain.blog.vos.CategoryId;
import com.luisjrz96.blog.domain.blog.vos.CategoryName;
import com.luisjrz96.blog.domain.shared.vos.ImageUrl;
import com.luisjrz96.blog.domain.shared.vos.Slug;

public class Category {

  private final CategoryId id;
  private CategoryName name;
  private Slug slug;
  private ImageUrl defaultImage;

  public Category(CategoryId id, CategoryName name, Slug slug, ImageUrl defaultImage) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
    this.slug = Objects.requireNonNull(slug);
    this.defaultImage = Objects.requireNonNull(defaultImage);
  }

  public CategoryId getId() {
    return id;
  }

  public CategoryName getName() {
    return name;
  }

  public void setName(CategoryName name) {
    this.name = name;
  }

  public Slug getSlug() {
    return slug;
  }

  public void setSlug(Slug slug) {
    this.slug = slug;
  }

  public ImageUrl getDefaultImage() {
    return defaultImage;
  }

  public void setDefaultImage(ImageUrl defaultImage) {
    this.defaultImage = defaultImage;
  }
}
