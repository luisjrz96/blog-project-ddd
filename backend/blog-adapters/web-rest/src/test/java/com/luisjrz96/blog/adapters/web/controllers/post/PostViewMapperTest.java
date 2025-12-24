package com.luisjrz96.blog.adapters.web.controllers.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.openapitools.jackson.nullable.JsonNullable;

import com.luisjrz96.blog.adapters.web.dto.PostDetailView;
import com.luisjrz96.blog.adapters.web.dto.PostDetailViewCategory;
import com.luisjrz96.blog.adapters.web.dto.PostDetailViewTag;
import com.luisjrz96.blog.adapters.web.dto.PostStatus;
import com.luisjrz96.blog.adapters.web.dto.PostSummaryView;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostCategoryViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostTagViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;

class PostViewMapperTest {

  private final PostViewMapper mapper = Mappers.getMapper(PostViewMapper.class);

  @Test
  void toOffsetDateTime_givenInstant_convertsToUtcOffsetDateTime() {
    Instant now = Instant.parse("2023-01-02T03:04:05Z");

    OffsetDateTime result = mapper.toOffset(now);

    OffsetDateTime expected = OffsetDateTime.ofInstant(now, ZoneOffset.UTC);
    assertEquals(expected, result);
  }

  @Test
  void mapInstant_givenInstant_returnsDefinedJsonNullableWithConvertedValue() {
    Instant now = Instant.parse("2023-01-02T03:04:05Z");

    JsonNullable<OffsetDateTime> result = mapper.map(now);

    assertTrue(result.isPresent());
    OffsetDateTime expected = OffsetDateTime.ofInstant(now, ZoneOffset.UTC);
    assertEquals(expected, result.get());
  }

  @Test
  void toPostDetailView_mapsAllFields_andConvertsInstantsToJsonNullable() {
    UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    Instant created = Instant.parse("2023-01-02T03:04:05Z");
    Instant updated = Instant.parse("2024-02-03T04:05:06Z");

    PostViewDto dto = Mockito.mock(PostViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    PostCategoryViewDto categoryDto =
        Mockito.mock(PostCategoryViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    PostTagViewDto tagDto = Mockito.mock(PostTagViewDto.class, Mockito.RETURNS_DEEP_STUBS);

    Mockito.when(dto.id().value()).thenReturn(id);
    Mockito.when(dto.title().value()).thenReturn("post-title");
    Mockito.when(dto.slug().value()).thenReturn("post-slug");
    Mockito.when(dto.summary().value()).thenReturn("post-summary");
    Mockito.when(dto.body().value()).thenReturn("post-body");
    Mockito.when(dto.status().name()).thenReturn("PUBLISHED");
    Mockito.when(dto.coverImage().value()).thenReturn("https://image.url/cover.png");
    Mockito.when(dto.createdAt()).thenReturn(created);
    Mockito.when(dto.updatedAt()).thenReturn(updated);

    Mockito.when(categoryDto.id().value())
        .thenReturn(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
    Mockito.when(categoryDto.name().value()).thenReturn("category-name");
    Mockito.when(dto.category()).thenReturn(categoryDto);

    Mockito.when(tagDto.id().value())
        .thenReturn(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));
    Mockito.when(tagDto.name().value()).thenReturn("tag-name");
    Mockito.when(dto.tags()).thenReturn(Set.of(tagDto));

    PostDetailView view = mapper.toPostDetailView(dto);

    assertEquals(id.toString(), view.getId());
    assertEquals("post-title", view.getTitle());
    assertEquals("post-slug", view.getSlug());
    assertEquals("post-summary", view.getSummary());
    assertEquals("post-body", view.getBody());

    PostDetailViewCategory cat = view.getCategory();
    assertEquals(String.valueOf(categoryDto.id().value()), cat.getId());
    assertEquals(String.valueOf(categoryDto.name().value()), cat.getName());

    List<PostDetailViewTag> tags = view.getTags();
    assertEquals(1, tags.size());
    assertEquals(String.valueOf(tagDto.id().value()), tags.getFirst().getId());
    assertEquals(tagDto.name().value(), tags.getFirst().getName());

    assertEquals(PostStatus.fromValue(dto.status().name()), view.getStatus());

    OffsetDateTime createdOpt = view.getCreatedAt();
    JsonNullable<OffsetDateTime> updatedOpt = view.getUpdatedAt();

    assertNotNull(createdOpt);
    assertTrue(updatedOpt.isPresent());

    OffsetDateTime expectedCreated = OffsetDateTime.ofInstant(created, ZoneOffset.UTC);
    OffsetDateTime expectedUpdated = OffsetDateTime.ofInstant(updated, ZoneOffset.UTC);

    assertEquals(expectedCreated, createdOpt);
    assertEquals(expectedUpdated, updatedOpt.get());
  }

  @Test
  void toSummaryView_andList_mapsSummaryFields() {
    UUID id = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    PostViewDto dto = Mockito.mock(PostViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    Mockito.when(dto.id().value()).thenReturn(id);
    Mockito.when(dto.title().value()).thenReturn("summary-title");
    Mockito.when(dto.slug().value()).thenReturn("summary-slug");
    Mockito.when(dto.summary().value()).thenReturn("summary-text");
    Mockito.when(dto.status().name()).thenReturn("DRAFT");
    Mockito.when(dto.coverImage().value()).thenReturn("https://image.url/cover.png");

    PostSummaryView summary = mapper.toSummaryView(dto);

    assertEquals(id.toString(), summary.getId());
    assertEquals("summary-title", summary.getTitle());
    assertEquals("summary-slug", summary.getSlug());
    assertEquals("summary-text", summary.getSummary());
    assertEquals(PostStatus.fromValue(dto.status().name()), summary.getStatus());

    List<PostSummaryView> list = mapper.toSummaryViewList(List.of(dto));
    assertEquals(1, list.size());
    assertEquals(summary.getId(), list.getFirst().getId());
  }

  @Test
  void toPostDetailTagInnerList_mapsList_ofTagDtos() {
    PostTagViewDto t1 = Mockito.mock(PostTagViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    PostTagViewDto t2 = Mockito.mock(PostTagViewDto.class, Mockito.RETURNS_DEEP_STUBS);

    Mockito.when(t1.id().value())
        .thenReturn(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"));
    Mockito.when(t1.name().value()).thenReturn("t1");
    Mockito.when(t2.id().value())
        .thenReturn(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"));
    Mockito.when(t2.name().value()).thenReturn("t2");

    List<PostDetailViewTag> mapped = mapper.toPostDetailTagInnerList(List.of(t1, t2));

    assertEquals(2, mapped.size());
    assertEquals(String.valueOf(t1.id().value()), mapped.get(0).getId());
    assertEquals(t1.name().value(), mapped.get(0).getName());
    assertEquals(String.valueOf(t2.id().value()), mapped.get(1).getId());
    assertEquals(t2.name().value(), mapped.get(1).getName());
  }
}
