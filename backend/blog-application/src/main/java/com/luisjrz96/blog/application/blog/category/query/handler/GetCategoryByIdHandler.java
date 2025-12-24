package com.luisjrz96.blog.application.blog.category.query.handler;

import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.blog.category.query.GetCategoryByIdQuery;
import com.luisjrz96.blog.application.shared.error.NotFoundException;

public class GetCategoryByIdHandler {

  private final CategoryViewReader reader;

  public GetCategoryByIdHandler(CategoryViewReader reader) {
    this.reader = reader;
  }

  public CategoryViewDto handle(GetCategoryByIdQuery query) {
    return reader
        .getById(query.id())
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("Category with id %s not found", query.id().value())));
  }
}
