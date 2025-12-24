package com.luisjrz96.blog.application.blog.post.query;

import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.post.PostStatus;

public record PostsPageQuery(PostStatus status, PageRequest pageRequest) {}
