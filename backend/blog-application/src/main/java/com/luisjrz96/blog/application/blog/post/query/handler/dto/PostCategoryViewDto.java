package com.luisjrz96.blog.application.blog.post.query.handler.dto;

import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;

public record PostCategoryViewDto(CategoryId id, CategoryName name) {}
