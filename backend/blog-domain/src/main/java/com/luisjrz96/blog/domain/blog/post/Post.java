package com.luisjrz96.blog.domain.blog.post;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.luisjrz96.blog.domain.AggregateRoot;
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
import com.luisjrz96.blog.domain.util.ValidationUtil;

public class Post extends AggregateRoot {
  private PostId id;
  private AuthorId authorId;
  private Title title;
  private Slug slug;
  private Summary summary;
  private Markdown body;
  private PostStatus status;
  private CategoryId categoryId;
  private List<TagId> tagIds;
  private ImageUrl coverImage;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant publishedAt;
  private Instant archivedAt;

  public Post() {}

  private void apply(PostCreated e) {
    this.id = e.postId();
    this.authorId = e.authorId();
    this.title = e.title();
    this.slug = e.slug();
    this.summary = e.summary();
    this.body = e.body();
    this.categoryId = e.categoryId();
    this.tagIds = new ArrayList<>(e.tagIds());
    this.coverImage = e.coverImage();
    this.status = e.status();
    this.publishedAt = e.publishedAt();
    this.createdAt = e.createdAt();
  }

  private void apply(PostUpdated e) {
    this.title = e.title();
    this.slug = e.slug();
    this.summary = e.summary();
    this.body = e.body();
    this.categoryId = e.categoryId();
    this.tagIds = new ArrayList<>(e.tagIds());
    this.coverImage = e.coverImage();
    this.updatedAt = e.updatedAt();
  }

  private void apply(PostPublished e) {
    this.status = PostStatus.PUBLISHED;
    this.updatedAt = e.updatedAt();
    this.publishedAt = e.publishedAt();
  }

  private void apply(PostArchived e) {
    this.status = PostStatus.ARCHIVED;
    this.archivedAt = e.archivedAt();
  }

  @Override
  protected void when(DomainEvent event) {
    switch (event) {
      case PostCreated e -> apply(e);
      case PostUpdated e -> apply(e);
      case PostPublished e -> apply(e);
      case PostArchived e -> apply(e);
      default -> throw new DomainException("Unhandled event type for Post " + event.getClass());
    }
  }

  public static Post create(
      AuthorId authorId,
      Title title,
      Summary summary,
      Markdown body,
      CategoryId categoryId,
      List<TagId> tagIds,
      ImageUrl coverImage) {
    Post post = new Post();
    Slug slug = Slug.from(title.value());
    Instant now = Instant.now();

    post.applyChange(
        new PostCreated(
            PostId.newId(),
            authorId,
            title,
            slug,
            summary,
            body,
            PostStatus.DRAFT,
            categoryId,
            tagIds,
            coverImage,
            null,
            now));

    return post;
  }

  public void update(
      Title title,
      Summary summary,
      Markdown body,
      CategoryId categoryId,
      List<TagId> tagIds,
      ImageUrl coverImage) {

    Instant now = Instant.now();
    Slug newSlug = new Slug(title.value());

    this.applyChange(
        new PostUpdated(
            this.id, title, newSlug, summary, body, categoryId, tagIds, coverImage, now));
  }

  public void publish() {
    if (this.status == PostStatus.PUBLISHED) {
      throw new DomainException("Post is already published");
    }

    if (this.status == PostStatus.ARCHIVED) {
      throw new DomainException("Archived posts cannot be published");
    }

    ValidationUtil.requireNonNull(this.body, "Post body cannot be null");
    Instant now = Instant.now();
    applyChange(new PostPublished(this.id, now, now));
  }

  public void archive() {
    if (this.status == PostStatus.ARCHIVED) {
      throw new DomainException("Post is already archived");
    }
    applyChange(new PostArchived(this.id, Instant.now()));
  }

  public PostId getId() {
    return id;
  }

  public AuthorId getAuthorId() {
    return authorId;
  }

  public Title getTitle() {
    return title;
  }

  public Slug getSlug() {
    return slug;
  }

  public Summary getSummary() {
    return summary;
  }

  public Markdown getBody() {
    return body;
  }

  public PostStatus getStatus() {
    return status;
  }

  public CategoryId getCategoryId() {
    return categoryId;
  }

  public List<TagId> getTagIds() {
    return tagIds;
  }

  public ImageUrl getCoverImage() {
    return coverImage;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getArchivedAt() {
    return archivedAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
