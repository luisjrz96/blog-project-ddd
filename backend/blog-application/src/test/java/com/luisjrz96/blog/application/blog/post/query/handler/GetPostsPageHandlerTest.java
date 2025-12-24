package com.luisjrz96.blog.application.blog.post.query.handler;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.luisjrz96.blog.application.blog.post.port.PostViewReader;
import com.luisjrz96.blog.application.blog.post.query.PostsPageQuery;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostCategoryViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostTagViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.application.shared.Page;
import com.luisjrz96.blog.application.shared.PageRequest;
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

class GetPostsPageHandlerTest {

  @Test
  void handle_shouldReturnPostPageWithStatusFromReader() {
    // given
    PostViewReader reader = mock(PostViewReader.class);
    GetPostsPageHandler handler = new GetPostsPageHandler(reader);

    PageRequest pageRequest = PageRequest.of(0, 2);
    PostsPageQuery query = new PostsPageQuery(PostStatus.PUBLISHED, pageRequest);

    PostViewDto dto1 =
        new PostViewDto(
            new PostId(UUID.randomUUID()),
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

    PostViewDto dto2 =
        new PostViewDto(
            new PostId(UUID.randomUUID()),
            new AuthorId(UUID.randomUUID()),
            new Title("Introduction to Frontend"),
            new Slug("introduction-to-frontend"),
            new Summary("A brief introduction to frontend development."),
            new Markdown("# Frontend Development\nThis is a post about frontend development."),
            PostStatus.PUBLISHED,
            new PostCategoryViewDto(
                new CategoryId(UUID.randomUUID()), new CategoryName("Frontend")),
            Set.of(new PostTagViewDto(new TagId(UUID.randomUUID()), new TagName("Javascript"))),
            new ImageUrl("https://img/2.png"),
            Instant.parse("2025-10-24T10:00:00Z"),
            Instant.parse("2025-10-24T11:00:00Z"),
            Instant.parse("2025-10-24T12:00:00Z"),
            null);

    Page<PostViewDto> expectedPage = new Page<>(List.of(dto1, dto2), 2, 0, 2);

    when(reader.getPageWithStatus(PostStatus.PUBLISHED, pageRequest)).thenReturn(expectedPage);

    // when
    Page<PostViewDto> result = handler.handle(query);

    // then
    assertSame(expectedPage, result);

    verify(reader, times(1)).getPageWithStatus(PostStatus.PUBLISHED, pageRequest);
    verifyNoMoreInteractions(reader);
  }

  @Test
  void handle_shouldReturnPostPageFromReader() {
    // given
    PostViewReader reader = mock(PostViewReader.class);
    GetPostsPageHandler handler = new GetPostsPageHandler(reader);

    PageRequest pageRequest = PageRequest.of(0, 2);
    PostsPageQuery query = new PostsPageQuery(null, pageRequest);

    PostViewDto dto1 =
        new PostViewDto(
            new PostId(UUID.randomUUID()),
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

    PostViewDto dto2 =
        new PostViewDto(
            new PostId(UUID.randomUUID()),
            new AuthorId(UUID.randomUUID()),
            new Title("Introduction to Frontend"),
            new Slug("introduction-to-frontend"),
            new Summary("A brief introduction to frontend development."),
            new Markdown("# Frontend Development\nThis is a post about frontend development."),
            PostStatus.DRAFT,
            new PostCategoryViewDto(
                new CategoryId(UUID.randomUUID()), new CategoryName("Frontend")),
            Set.of(new PostTagViewDto(new TagId(UUID.randomUUID()), new TagName("Javascript"))),
            new ImageUrl("https://img/2.png"),
            Instant.parse("2025-10-24T10:00:00Z"),
            Instant.parse("2025-10-24T11:00:00Z"),
            Instant.parse("2025-10-24T12:00:00Z"),
            null);

    Page<PostViewDto> expectedPage = new Page<>(List.of(dto1, dto2), 2, 0, 2);

    when(reader.getPage(pageRequest)).thenReturn(expectedPage);

    // when
    Page<PostViewDto> result = handler.handle(query);

    // then
    assertSame(expectedPage, result);

    verify(reader, times(1)).getPage(pageRequest);
    verifyNoMoreInteractions(reader);
  }
}
