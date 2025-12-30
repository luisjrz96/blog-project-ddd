package com.luisjrz96.blog.adapters.web.controllers.blog.category;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.luisjrz96.blog.adapters.web.api.CategoriesApi;
import com.luisjrz96.blog.adapters.web.dto.CategoryStatus;
import com.luisjrz96.blog.adapters.web.dto.CategoryView;
import com.luisjrz96.blog.adapters.web.dto.CreateCategoryRequest;
import com.luisjrz96.blog.adapters.web.dto.CreateCategoryResponse;
import com.luisjrz96.blog.adapters.web.dto.PageCategoryView;
import com.luisjrz96.blog.adapters.web.dto.UpdateCategoryRequest;
import com.luisjrz96.blog.application.blog.category.CategoryService;
import com.luisjrz96.blog.application.blog.category.command.ArchiveCategoryCommand;
import com.luisjrz96.blog.application.blog.category.command.CreateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.command.UpdateCategoryCommand;
import com.luisjrz96.blog.application.blog.category.query.CategoriesPageQuery;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.blog.category.query.GetCategoryByIdQuery;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.shared.ImageUrl;

@RestController
public class CategoryController implements CategoriesApi {

  private final CategoryService categoryService;
  private final CategoryViewMapper mapper;

  public CategoryController(CategoryService categoryService, CategoryViewMapper mapper) {
    this.categoryService = categoryService;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<Void> adminArchiveCategory(String id) {
    categoryService.archive(new ArchiveCategoryCommand(new CategoryId(UUID.fromString(id))));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<PageCategoryView> adminGetCategories(
      Integer page, Integer size, CategoryStatus status) {
    if (status == null) status = CategoryStatus.ACTIVE;

    var domainStatus =
        com.luisjrz96.blog.domain.blog.category.CategoryStatus.valueOf(status.name());
    var query = new CategoriesPageQuery(domainStatus, PageRequest.of(page, size));
    var pageDto = categoryService.getPage(query);

    return ResponseEntity.ok(toPageView(pageDto));
  }

  @Override
  public ResponseEntity<CategoryView> adminGetCategory(String id) {
    return this.getCategory(id);
  }

  @Override
  public ResponseEntity<Void> adminUpdateCategory(
      String id, UpdateCategoryRequest updateCategoryRequest) {
    var cmd =
        new UpdateCategoryCommand(
            new CategoryId(UUID.fromString(id)),
            new CategoryName(updateCategoryRequest.getName()),
            new ImageUrl(updateCategoryRequest.getDefaultImage().toString()));
    categoryService.update(cmd);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<CreateCategoryResponse> apiAdminCategoriesPost(
      CreateCategoryRequest createCategoryRequest) {
    var cmd =
        new CreateCategoryCommand(
            new CategoryName(createCategoryRequest.getName()),
            new ImageUrl(createCategoryRequest.getDefaultImage().toString()));
    var id = categoryService.create(cmd);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreateCategoryResponse(String.valueOf(id.value())));
  }

  @Override
  public ResponseEntity<PageCategoryView> getCategories(Integer page, Integer size) {
    var domainStatus =
        com.luisjrz96.blog.domain.blog.category.CategoryStatus.valueOf(
            CategoryStatus.ACTIVE.name());
    var query = new CategoriesPageQuery(domainStatus, PageRequest.of(page, size));
    var pageDto = categoryService.getPage(query);

    return ResponseEntity.ok(toPageView(pageDto));
  }

  @Override
  public ResponseEntity<CategoryView> getCategory(String id) {
    var dto =
        categoryService.findById(new GetCategoryByIdQuery(new CategoryId(UUID.fromString(id))));
    return ResponseEntity.ok(mapper.toView(dto));
  }

  private PageCategoryView toPageView(Page<CategoryViewDto> page) {
    int totalPages = (int) Math.ceil((double) page.total() / page.size());
    return new PageCategoryView(
        mapper.toViewList(page.items()), page.page(), page.size(), page.total(), totalPages);
  }
}
