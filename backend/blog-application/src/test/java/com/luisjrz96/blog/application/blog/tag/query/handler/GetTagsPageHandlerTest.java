package com.luisjrz96.blog.application.blog.tag.query.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.tag.port.TagViewReader;
import com.luisjrz96.blog.application.blog.tag.query.GetTagByIdQuery;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.blog.tag.TagStatus;
import com.luisjrz96.blog.domain.shared.Slug;

class GetTagsPageHandlerTest {

  @Test
  void handle_shouldReturnTagViewDtoFromReader() {
    // given
    TagViewReader reader = mock(TagViewReader.class);
    GetTagByIdHandler handler = new GetTagByIdHandler(reader);

    TagId id = new TagId(UUID.randomUUID());
    GetTagByIdQuery query = new GetTagByIdQuery(id);

    TagViewDto dto =
        new TagViewDto(
            id,
            new TagName("Backend"),
            new Slug("backend"),
            TagStatus.ACTIVE,
            Instant.parse("2025-10-24T10:00:00Z"),
            null,
            null);

    when(reader.getById(id)).thenReturn(Optional.of(dto));

    // when
    TagViewDto result = handler.handle(query);

    // then
    assertSame(dto, result);

    verify(reader, times(1)).getById(id);
    verifyNoMoreInteractions(reader);
  }
}
