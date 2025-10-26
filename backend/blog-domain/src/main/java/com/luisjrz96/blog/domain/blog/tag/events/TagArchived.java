package com.luisjrz96.blog.domain.blog.tag.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;

public record TagArchived(Instant archivedAt) implements DomainEvent {}
