package com.luisjrz96.blog.domain.blog.post.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.post.PostId;

public record PostPublished(PostId postId, Instant updatedAt, Instant publishedAt)
    implements DomainEvent {}
