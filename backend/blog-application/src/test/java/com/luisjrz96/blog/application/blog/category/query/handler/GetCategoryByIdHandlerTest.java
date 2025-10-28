package com.luisjrz96.blog.application.blog.category.query.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.blog.category.query.GetCategoryByIdQuery;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

public class GetCategoryByIdHandlerTest {

  @Test
  void handle_shouldReturnCategoryViewDtoFromReader() {
    // given
    CategoryViewReader reader = mock(CategoryViewReader.class);
    GetCategoryByIdHandler handler = new GetCategoryByIdHandler(reader);

    CategoryId id = new CategoryId(UUID.randomUUID());
    GetCategoryByIdQuery query = new GetCategoryByIdQuery(id);

    CategoryViewDto dto =
        new CategoryViewDto(
            id,
            new CategoryName("Backend"),
            new Slug("backend"),
            new ImageUrl("https://img/backend.png"),
            CategoryStatus.ACTIVE,
            Instant.parse("2025-10-24T10:00:00Z"),
            null,
            null);

    when(reader.getById(id)).thenReturn(dto);

    // when
    CategoryViewDto result = handler.handle(query);

    // then
    assertSame(dto, result);

    verify(reader, times(1)).getById(id);
    verifyNoMoreInteractions(reader);
  }
}
