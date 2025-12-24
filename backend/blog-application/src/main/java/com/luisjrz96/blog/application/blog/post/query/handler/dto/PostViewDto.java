package com.luisjrz96.blog.application.blog.post.query.handler.dto;

import java.time.Instant;
import java.util.Set;

import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

public record PostViewDto(
    PostId id,
    AuthorId authorId,
    Title title,
    Slug slug,
    Summary summary,
    Markdown body,
    PostStatus status,
    PostCategoryViewDto category,
    Set<PostTagViewDto> tags,
    ImageUrl coverImage,
    Instant createdAt,
    Instant updatedAt,
    Instant publishedAt,
    Instant archivedAt) {}
