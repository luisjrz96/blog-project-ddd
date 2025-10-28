package com.luisjrz96.blog.application.blog.category.query;

import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;

public record GetCategoryByIdQuery(CategoryStatus status, CategoryId id) {}
