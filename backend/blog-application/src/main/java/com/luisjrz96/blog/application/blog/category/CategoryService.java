package com.luisjrz96.blog.application.blog.category;

import com.luisjrz96.blog.application.blog.category.command.ArchiveCategoryCommand;
import com.luisjrz96.blog.application.blog.category.command.CreateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.command.UpdateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.command.handler.ArchiveCategoryHandler;
import com.luisjrz96.blog.application.blog.category.command.handler.CreateCategoryHandler;
import com.luisjrz96.blog.application.blog.category.command.handler.UpdateCategoryHandler;
import com.luisjrz96.blog.application.blog.category.query.CategoriesPageQuery;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.blog.category.query.GetCategoryByIdQuery;
import com.luisjrz96.blog.application.blog.category.query.handler.GetCategoriesPageHandler;
import com.luisjrz96.blog.application.blog.category.query.handler.GetCategoryByIdHandler;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

public class CategoryService {
  private final CreateCategoryHandler createCategoryHandler;
  private final UpdateCategoryHandler updateCategoryHandler;
  private final ArchiveCategoryHandler archiveCategoryHandler;
  private final GetCategoriesPageHandler getCategoriesPageHandler;
  private final GetCategoryByIdHandler getCategoryByIdHandler;

  public CategoryService(
      CreateCategoryHandler createCategoryHandler,
      UpdateCategoryHandler updateCategoryHandler,
      ArchiveCategoryHandler archiveCategoryHandler,
      GetCategoriesPageHandler getCategoriesPageHandler,
      GetCategoryByIdHandler getCategoryByIdHandler) {
    this.createCategoryHandler = createCategoryHandler;
    this.updateCategoryHandler = updateCategoryHandler;
    this.archiveCategoryHandler = archiveCategoryHandler;
    this.getCategoriesPageHandler = getCategoriesPageHandler;
    this.getCategoryByIdHandler = getCategoryByIdHandler;
  }

  public CategoryId create(CreateCategoryCommand createCategoryCommand) {
    return createCategoryHandler.handle(createCategoryCommand);
  }

  public void update(UpdateCategoryCommand updateCategoryCommand) {
    updateCategoryHandler.handle(updateCategoryCommand);
  }

  public void archive(ArchiveCategoryCommand archiveCategoryCommand) {
    archiveCategoryHandler.handle(archiveCategoryCommand);
  }

  public Page<CategoryViewDto> getPage(CategoriesPageQuery categoriesPageQuery) {
    return getCategoriesPageHandler.handle(categoriesPageQuery);
  }

  public CategoryViewDto findById(GetCategoryByIdQuery getCategoryByIdQuery) {
    return getCategoryByIdHandler.handle(getCategoryByIdQuery);
  }
}
