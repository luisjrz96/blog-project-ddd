package com.luisjrz96.blog.domain.blog.category.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

public record CategoryCreated(
    CategoryId id,
    CategoryName name,
    Slug slug,
    ImageUrl defaultImage,
    CategoryStatus status,
    Instant createdAt)
    implements DomainEvent {}
