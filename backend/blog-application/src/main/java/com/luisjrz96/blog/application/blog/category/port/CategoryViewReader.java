package com.luisjrz96.blog.application.blog.category.port;

import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;

public interface CategoryViewReader {
  CategoryViewDto getById(CategoryId id);

  Page<CategoryViewDto> getPage(PageRequest pageRequest);
  Page<CategoryViewDto> getPageWithStatus(CategoryStatus status, PageRequest pageRequest);
}
