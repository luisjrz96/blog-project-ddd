package com.luisjrz96.blog.application.blog.category.query.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.category.port.CategoryViewReader;
import com.luisjrz96.blog.application.blog.category.query.CategoriesPageQuery;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.category.CategoryStatus;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;

public class GetCategoriesPageHandlerTest {

  @Test
  void handle_shouldReturnPageFromReader() {
    // given
    CategoryViewReader reader = mock(CategoryViewReader.class);
    GetCategoriesPageHandler handler = new GetCategoriesPageHandler(reader);

    PageRequest pageRequest = PageRequest.of(0, 2);
    CategoriesPageQuery query = new CategoriesPageQuery(pageRequest);

    CategoryViewDto dto1 =
        new CategoryViewDto(
            new CategoryId(UUID.randomUUID()),
            new CategoryName("Backend"),
            new Slug("backend"),
            new ImageUrl("https://img/1.png"),
            CategoryStatus.ACTIVE,
            Instant.parse("2025-10-24T10:00:00Z"),
            Instant.parse("2025-10-24T11:00:00Z"),
            null);

    CategoryViewDto dto2 =
        new CategoryViewDto(
            new CategoryId(UUID.randomUUID()),
            new CategoryName("Platform"),
            new Slug("platform"),
            new ImageUrl("https://img/2.png"),
            CategoryStatus.ARCHIVED,
            Instant.parse("2025-10-24T09:00:00Z"),
            null,
            Instant.parse("2025-10-24T12:00:00Z"));

    Page<CategoryViewDto> expectedPage = new Page<>(List.of(dto1, dto2), 2, 0, 2);

    when(reader.getPage(pageRequest)).thenReturn(expectedPage);

    // when
    Page<CategoryViewDto> result = handler.handle(query);

    // then
    assertSame(expectedPage, result);

    verify(reader, times(1)).getPage(pageRequest);
    verifyNoMoreInteractions(reader);
  }
}
