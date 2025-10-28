package com.luisjrz96.blog.application.blog.category.port;

import com.luisjrz96.blog.domain.blog.category.Category;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

public interface CategoryRepository {
  Category load(CategoryId id);

  void save(Category category);
}
