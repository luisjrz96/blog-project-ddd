package com.luisjrz96.blog.domain.blog.category.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;

public record CategoryArchived(Instant archivedAt) implements DomainEvent {}
