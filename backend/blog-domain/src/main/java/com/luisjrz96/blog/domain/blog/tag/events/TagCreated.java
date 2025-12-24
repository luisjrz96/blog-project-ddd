package com.luisjrz96.blog.domain.blog.tag.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.shared.Slug;

public record TagCreated(TagId id, TagName name, Slug slug, Instant createdAt)
    implements DomainEvent {}
