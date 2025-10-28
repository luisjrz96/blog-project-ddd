package com.luisjrz96.blog.application.blog.category.query.handler;

import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.blog.category.query.GetCategoryByIdQuery;

public class GetCategoryByIdHandler {

  private final CategoryViewReader reader;

  public GetCategoryByIdHandler(CategoryViewReader reader) {
    this.reader = reader;
  }

  public CategoryViewDto handle(GetCategoryByIdQuery query) {
    return reader.getById(query.id());
  }
}
