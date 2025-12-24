package com.luisjrz96.blog.application.blog.post.query.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.post.port.PostViewReader;
import com.luisjrz96.blog.application.blog.post.query.GetPostByIdQuery;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostCategoryViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostTagViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.domain.blog.category.CategoryId;
import com.luisjrz96.blog.domain.blog.category.CategoryName;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.blog.post.PostId;
import com.luisjrz96.blog.domain.blog.post.PostStatus;
import com.luisjrz96.blog.domain.blog.tag.TagId;
import com.luisjrz96.blog.domain.blog.tag.TagName;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Slug;
import com.luisjrz96.blog.domain.shared.Summary;
import com.luisjrz96.blog.domain.shared.Title;

class GetPostByIdHandlerTest {

  @Test
  void handle_shouldReturnPostViewDtoFromReader() {
    // given
    PostViewReader reader = mock(PostViewReader.class);
    GetPostByIdHandler handler = new GetPostByIdHandler(reader);

    PostId id = new PostId(UUID.randomUUID());
    GetPostByIdQuery query = new GetPostByIdQuery(id);

    PostViewDto dto =
        new PostViewDto(
            id,
            new AuthorId(UUID.randomUUID()),
            new Title("Introduction to Backend"),
            new Slug("introduction-to-backend"),
            new Summary("A brief introduction to backend development."),
            new Markdown("# Backend Development\nThis is a post about backend development."),
            PostStatus.PUBLISHED,
            new PostCategoryViewDto(new CategoryId(UUID.randomUUID()), new CategoryName("Backend")),
            Set.of(new PostTagViewDto(new TagId(UUID.randomUUID()), new TagName("Java"))),
            new ImageUrl("https://img/1.png"),
            Instant.parse("2025-10-24T10:00:00Z"),
            Instant.parse("2025-10-24T11:00:00Z"),
            Instant.parse("2025-10-24T12:00:00Z"),
            null);

    when(reader.getById(id)).thenReturn(Optional.of(dto));

    // when
    PostViewDto result = handler.handle(query);

    // then
    assertSame(dto, result);

    verify(reader, times(1)).getById(id);
    verifyNoMoreInteractions(reader);
  }
}
