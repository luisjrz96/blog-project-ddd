package com.luisjrz96.blog.domain.entity.blog;

import java.util.Objects;

import com.luisjrz96.blog.domain.vos.blog.TagId;
import com.luisjrz96.blog.domain.vos.blog.TagName;
import com.luisjrz96.blog.domain.vos.shared.Slug;

public class Tag {

  private final TagId tagId;
  private TagName tagName;
  private Slug slug;

  public Tag(TagId tagId, TagName tagName, Slug slug) {
    this.tagId = Objects.requireNonNull(tagId);
    this.tagName = Objects.requireNonNull(tagName);
    this.slug = Objects.requireNonNull(slug);
  }

  public TagId getTagId() {
    return tagId;
  }

  public TagName getTagName() {
    return tagName;
  }

  public void setTagName(TagName tagName) {
    this.tagName = tagName;
  }

  public Slug getSlug() {
    return slug;
  }

  public void setSlug(Slug slug) {
    this.slug = slug;
  }
}
