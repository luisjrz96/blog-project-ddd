package com.luisjrz96.blog.domain.blog.tag.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.tag.TagId;

public record TagArchived(TagId id, Instant archivedAt) implements DomainEvent {}
