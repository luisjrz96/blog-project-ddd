package com.luisjrz96.blog.application.blog.tag.query;

import java.time.Instant;

import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;
import com.luisjrz96.blog.domain.shared.Slug;

public record TagViewDto(
    TagId id,
    TagName name,
    Slug slug,
    TagStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant archivedAt) {}
