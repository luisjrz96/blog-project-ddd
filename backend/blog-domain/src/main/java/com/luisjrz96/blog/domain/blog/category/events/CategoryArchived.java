package com.luisjrz96.blog.domain.blog.category.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

public record CategoryArchived(CategoryId id, Instant archivedAt) implements DomainEvent {}
