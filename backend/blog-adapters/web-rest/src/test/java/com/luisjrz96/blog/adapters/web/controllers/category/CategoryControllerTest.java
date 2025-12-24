package com.luisjrz96.blog.adapters.web.controllers.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.luisjrz96.blog.adapters.web.dto.CreateCategoryRequest;
import com.luisjrz96.blog.adapters.web.dto.UpdateCategoryRequest;
import com.luisjrz96.blog.application.blog.category.CategoryService;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.domain.blog.category.CategoryId;

class CategoryControllerTest {

  private CategoryService categoryService;
  private CategoryViewMapper mapper;
  private CategoryController controller;

  @BeforeEach
  void setUp() {
    categoryService = mock(CategoryService.class);
    mapper = mock(CategoryViewMapper.class);
    controller = new CategoryController(categoryService, mapper);
  }

  @Test
  void adminArchiveCategory_callsService_andReturnsNoContent() {
    String id = UUID.randomUUID().toString();

    ResponseEntity<Void> resp = controller.adminArchiveCategory(id);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(categoryService, times(1)).archive(any());
  }

  @Test
  void adminGetCategories_returnsOk_withPage() {
    Page<CategoryViewDto> pageSpy = spy(new Page<>(List.of(), 0L, 0, 10));
    when(categoryService.getPage(any())).thenReturn(pageSpy);
    when(mapper.toViewList(any())).thenReturn(Collections.emptyList());

    ResponseEntity<?> resp = controller.adminGetCategories(0, 10, null);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(categoryService, times(1)).getPage(any());
    verify(mapper, times(1)).toViewList(any());
  }

  @Test
  void getCategories_returnsOk_withPage() {
    Page<CategoryViewDto> pageSpy = spy(new Page<>(List.of(), 0L, 0, 10));
    when(categoryService.getPage(any())).thenReturn(pageSpy);
    when(mapper.toViewList(any())).thenReturn(Collections.emptyList());

    ResponseEntity<?> resp = controller.getCategories(0, 10);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(categoryService, times(1)).getPage(any());
    verify(mapper, times(1)).toViewList(any());
  }

  @Test
  void apiAdminCategoriesPost_callsCreate_andReturnsCreated() {
    CreateCategoryRequest req = mock(CreateCategoryRequest.class);
    when(req.getName()).thenReturn("name");
    when(req.getDefaultImage()).thenReturn(URI.create("https://img/img.png"));

    CategoryId returnedId = mock(CategoryId.class);
    when(returnedId.value()).thenReturn(UUID.randomUUID());
    when(categoryService.create(any())).thenReturn(returnedId);

    ResponseEntity<?> resp = controller.apiAdminCategoriesPost(req);

    assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    verify(categoryService, times(1)).create(any());
  }

  @Test
  void adminUpdateCategory_callsUpdate_andReturnsNoContent() {
    UpdateCategoryRequest req = mock(UpdateCategoryRequest.class);
    when(req.getName()).thenReturn("new-name");
    when(req.getDefaultImage()).thenReturn(URI.create("https://img/img2.png"));

    String id = UUID.randomUUID().toString();
    ResponseEntity<Void> resp = controller.adminUpdateCategory(id, req);

    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(categoryService, times(1)).update(any());
  }

  @Test
  void getCategory_callsService_andReturnsOk() {
    String id = UUID.randomUUID().toString();
    CategoryViewDto dto = mock(CategoryViewDto.class);
    when(categoryService.findById(any())).thenReturn(dto);
    when(mapper.toView(dto)).thenReturn(null);

    ResponseEntity<?> resp = controller.getCategory(id);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(categoryService, times(1)).findById(any());
    verify(mapper, times(1)).toView(dto);
  }
}
