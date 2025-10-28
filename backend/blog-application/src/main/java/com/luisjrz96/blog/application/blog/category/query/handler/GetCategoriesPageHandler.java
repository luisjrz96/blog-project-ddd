package com.luisjrz96.blog.application.blog.category.query.handler;

import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.CategoriesPageQuery;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.shared.Page;

public class GetCategoriesPageHandler {

  private final CategoryViewReader reader;

  public GetCategoriesPageHandler(CategoryViewReader reader) {
    this.reader = reader;
  }

  public Page<CategoryViewDto> handle(CategoriesPageQuery query) {
    return reader.getPage(query.pageRequest());
  }
}
