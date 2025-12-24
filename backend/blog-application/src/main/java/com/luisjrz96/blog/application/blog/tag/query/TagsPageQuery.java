package com.luisjrz96.blog.application.blog.tag.query;

import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;

public record TagsPageQuery(TagStatus status, PageRequest pageRequest) {}
