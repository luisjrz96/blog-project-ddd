package com.luisjrz96.blog.application.blog.tag.query.handler;

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

import com.luisjrz96.blog.application.blog.tag.port.TagViewReader;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.application.blog.tag.query.TagsPageQuery;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;
import com.luisjrz96.blog.domain.shared.Slug;

class GetTagByIdHandlerTest {

  @Test
  void handle_shouldReturnTagPageWithStatusFromReader() {
    // given
    TagViewReader reader = mock(TagViewReader.class);
    GetTagsPageHandler handler = new GetTagsPageHandler(reader);

    PageRequest pageRequest = PageRequest.of(0, 2);
    TagsPageQuery query = new TagsPageQuery(TagStatus.ACTIVE, pageRequest);

    TagViewDto dto1 =
        new TagViewDto(
            new TagId(UUID.randomUUID()),
            new TagName("Backend"),
            new Slug("backend"),
            TagStatus.ACTIVE,
            Instant.parse("2025-10-24T10:00:00Z"),
            Instant.parse("2025-10-24T11:00:00Z"),
            null);

    TagViewDto dto2 =
        new TagViewDto(
            new TagId(UUID.randomUUID()),
            new TagName("Platform"),
            new Slug("platform"),
            TagStatus.ACTIVE,
            Instant.parse("2025-10-24T09:00:00Z"),
            null,
            Instant.parse("2025-10-24T12:00:00Z"));

    Page<TagViewDto> expectedPage = new Page<>(List.of(dto1, dto2), 2, 0, 2);

    when(reader.getPageWithStatus(TagStatus.ACTIVE, pageRequest)).thenReturn(expectedPage);

    // when
    Page<TagViewDto> result = handler.handle(query);

    // then
    assertSame(expectedPage, result);

    verify(reader, times(1)).getPageWithStatus(TagStatus.ACTIVE, pageRequest);
    verifyNoMoreInteractions(reader);
  }

  @Test
  void handle_shouldReturnTagPageFromReader() {
    // given
    TagViewReader reader = mock(TagViewReader.class);
    GetTagsPageHandler handler = new GetTagsPageHandler(reader);

    PageRequest pageRequest = PageRequest.of(0, 2);
    TagsPageQuery query = new TagsPageQuery(null, pageRequest);

    TagViewDto dto1 =
        new TagViewDto(
            new TagId(UUID.randomUUID()),
            new TagName("Backend"),
            new Slug("backend"),
            TagStatus.ACTIVE,
            Instant.parse("2025-10-24T10:00:00Z"),
            Instant.parse("2025-10-24T11:00:00Z"),
            null);

    TagViewDto dto2 =
        new TagViewDto(
            new TagId(UUID.randomUUID()),
            new TagName("Platform"),
            new Slug("platform"),
            TagStatus.ARCHIVED,
            Instant.parse("2025-10-24T09:00:00Z"),
            null,
            Instant.parse("2025-10-24T12:00:00Z"));

    Page<TagViewDto> expectedPage = new Page<>(List.of(dto1, dto2), 2, 0, 2);

    when(reader.getPage(pageRequest)).thenReturn(expectedPage);

    // when
    Page<TagViewDto> result = handler.handle(query);

    // then
    assertSame(expectedPage, result);

    verify(reader, times(1)).getPage(pageRequest);
    verifyNoMoreInteractions(reader);
  }
}
