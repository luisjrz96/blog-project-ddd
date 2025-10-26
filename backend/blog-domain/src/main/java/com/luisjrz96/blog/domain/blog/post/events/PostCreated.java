package com.luisjrz96.blog.domain.blog.post.events;

import java.time.Instant;
import java.util.List;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

public record PostCreated(
    PostId postId,
    AuthorId authorId,
    Title title,
    Slug slug,
    Summary summary,
    Markdown body,
    PostStatus status,
    CategoryId categoryId,
    List<TagId> tagIds,
    ImageUrl coverImage,
    Instant publishedAt,
    Instant createdAt)
    implements DomainEvent {}
