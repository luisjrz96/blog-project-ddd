package com.luisjrz96.blog.application.blog.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock private CreateCategoryHandler createCategoryHandler;

  @Mock private UpdateCategoryHandler updateCategoryHandler;

  @Mock private ArchiveCategoryHandler archiveCategoryHandler;

  @Mock private GetCategoriesPageHandler getCategoriesPageHandler;

  @Mock private GetCategoryByIdHandler getCategoryByIdHandler;

  @InjectMocks private CategoryService service;

  @Test
  void shouldDelegateCreateToHandler() {
    CreateCategoryCommand command = mock(CreateCategoryCommand.class);
    CategoryId expectedId = new CategoryId(UUID.randomUUID());

    when(createCategoryHandler.handle(command)).thenReturn(expectedId);

    CategoryId result = service.create(command);

    assertEquals(expectedId, result);
    verify(createCategoryHandler).handle(command);
  }

  @Test
  void shouldDelegateUpdateToHandler() {
    UpdateCategoryCommand command = mock(UpdateCategoryCommand.class);

    service.update(command);

    verify(updateCategoryHandler).handle(command);
    verifyNoMoreInteractions(updateCategoryHandler);
  }

  @Test
  void shouldDelegateArchiveToHandler() {
    ArchiveCategoryCommand command = mock(ArchiveCategoryCommand.class);

    service.archive(command);

    verify(archiveCategoryHandler).handle(command);
    verifyNoMoreInteractions(archiveCategoryHandler);
  }

  @Test
  void shouldDelegateGetPageToHandler() {
    CategoriesPageQuery query = mock(CategoriesPageQuery.class);
    Page<CategoryViewDto> expectedPage = spy(new Page<>(List.of(), 0, 10, 0));

    when(getCategoriesPageHandler.handle(query)).thenReturn(expectedPage);

    Page<CategoryViewDto> result = service.getPage(query);

    assertSame(expectedPage, result);
    verify(getCategoriesPageHandler).handle(query);
  }

  @Test
  void shouldDelegateFindByIdToHandler() {
    GetCategoryByIdQuery query = mock(GetCategoryByIdQuery.class);
    CategoryViewDto expected = mock(CategoryViewDto.class);

    when(getCategoryByIdHandler.handle(query)).thenReturn(expected);

    CategoryViewDto result = service.findById(query);

    assertSame(expected, result);
    verify(getCategoryByIdHandler).handle(query);
  }
}
