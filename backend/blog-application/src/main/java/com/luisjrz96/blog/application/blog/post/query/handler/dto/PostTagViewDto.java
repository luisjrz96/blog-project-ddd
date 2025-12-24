package com.luisjrz96.blog.application.blog.post.query.handler.dto;

import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;

public record PostTagViewDto(TagId id, TagName name) {}
