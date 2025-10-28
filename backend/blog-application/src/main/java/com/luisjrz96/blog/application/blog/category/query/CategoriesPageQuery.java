package com.luisjrz96.blog.application.blog.category.query;

import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;

public record CategoriesPageQuery(CategoryStatus status, PageRequest pageRequest) {}
