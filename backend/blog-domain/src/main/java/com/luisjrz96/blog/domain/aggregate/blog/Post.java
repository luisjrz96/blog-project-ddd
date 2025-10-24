package com.luisjrz96.blog.domain.aggregate.blog;

import java.time.Instant;
import java.util.List;

import com.luisjrz96.blog.domain.entity.blog.Category;
import com.luisjrz96.blog.domain.entity.blog.Tag;
import com.luisjrz96.blog.domain.util.ValidationUtil;
import com.luisjrz96.blog.domain.vos.blog.Markdown;
import com.luisjrz96.blog.domain.vos.blog.PostId;
import com.luisjrz96.blog.domain.vos.blog.PostStatus;
import com.luisjrz96.blog.domain.vos.shared.AuthorId;
import com.luisjrz96.blog.domain.vos.shared.ImageUrl;
import com.luisjrz96.blog.domain.vos.shared.Slug;
import com.luisjrz96.blog.domain.vos.shared.Summary;
import com.luisjrz96.blog.domain.vos.shared.Title;

public class Post {
  private final PostId postId;
  private final AuthorId authorId;
  private Title title;
  private Slug slug;
  private Summary summary;
  private Markdown body;
  private PostStatus postStatus;
  private Category category;
  private List<Tag> tags;
  private ImageUrl coverImage;
  private Instant publishedAt;

  public Post(
      PostId postId,
      AuthorId authorId,
      Title title,
      Summary summary,
      Markdown body,
      Category category,
      List<Tag> tags,
      ImageUrl coverImage,
      Instant publishedAt) {
    this.postId = ValidationUtil.requireNonNull(postId, "PostId cannot be null for Post");
    this.authorId = ValidationUtil.requireNonNull(authorId, "author cannot be null for Post");
    this.title = title;
    this.slug = Slug.from(title.value());
    this.summary = summary;
    this.body = body;
    this.postStatus = PostStatus.DRAFT;
    this.category = category;
    this.tags = tags;
    this.coverImage = coverImage;
    this.publishedAt = publishedAt;
  }

  public PostId getPostId() {
    return postId;
  }

  public AuthorId getAuthorId() {
    return authorId;
  }

  public Title getTitle() {
    return title;
  }

  public void setTitle(Title title) {
    this.title = title;
  }

  public Slug getSlug() {
    return slug;
  }

  public void setSlug(Slug slug) {
    this.slug = slug;
  }

  public Summary getSummary() {
    return summary;
  }

  public void setSummary(Summary summary) {
    this.summary = summary;
  }

  public Markdown getBody() {
    return body;
  }

  public void setBody(Markdown body) {
    this.body = body;
  }

  public PostStatus getPostStatus() {
    return postStatus;
  }

  public void setPostStatus(PostStatus postStatus) {
    this.postStatus = postStatus;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public ImageUrl getCoverImage() {
    if (coverImage == null) {
      return category.getDefaultImage();
    }
    return coverImage;
  }

  public void setCoverImage(ImageUrl coverImage) {
    this.coverImage = coverImage;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Instant publishedAt) {
    this.publishedAt = publishedAt;
  }
}
