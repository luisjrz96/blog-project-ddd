package com.luisjrz96.blog.application.blog.post.command;

import com.luisjrz96.blog.domain.blog.post.PostId;

public record PublishPostCommand(PostId id) {}
