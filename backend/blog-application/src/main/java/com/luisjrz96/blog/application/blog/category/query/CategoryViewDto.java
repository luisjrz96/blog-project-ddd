package com.luisjrz96.blog.application.blog.category.query;

import java.time.Instant;

import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

public record CategoryViewDto(
    CategoryId id,
    CategoryName name,
    Slug slug,
    ImageUrl defaultImage,
    CategoryStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant archivedAt) {}
