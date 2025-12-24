package com.luisjrz96.blog.application.blog.category.port;

import com.luisjrz96.blog.domain.blog.category.CategoryId;

public interface CategoryLookup {
  boolean isActive(CategoryId id);
}
