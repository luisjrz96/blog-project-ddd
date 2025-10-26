package com.luisjrz96.blog.domain.blog.post.events;

import java.time.Instant;

import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.util.ValidationUtil;

public record PostArchived(PostId postId, Instant archivedAt) implements DomainEvent {

  public PostArchived {
    ValidationUtil.requireNonNull(postId, "PostId cannot be null for PostArchived event");
  }
}
