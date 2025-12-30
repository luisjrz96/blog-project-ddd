package com.luisjrz96.blog.adapters.web.controllers.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.openapitools.jackson.nullable.JsonNullable;

import com.luisjrz96.blog.adapters.web.controllers.blog.tag.TagViewMapper;
import com.luisjrz96.blog.adapters.web.dto.TagStatus;
import com.luisjrz96.blog.adapters.web.dto.TagView;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;

class TagViewMapperTest {

  private final TagViewMapper mapper = Mappers.getMapper(TagViewMapper.class);

  @Test
  void toOffsetDateTime_givenInstant_convertsToUtcOffsetDateTime() {
    Instant now = Instant.parse("2023-01-02T03:04:05Z");

    OffsetDateTime result = mapper.toOffsetDateTime(now);

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
  void toView_mapsAllFields_andConvertsInstantsToOffsetDateTimeJsonNullable() {
    UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Instant created = Instant.parse("2023-01-02T03:04:05Z");
    Instant updated = Instant.parse("2024-02-03T04:05:06Z");

    TagViewDto dto = Mockito.mock(TagViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    Mockito.when(dto.id().value()).thenReturn(id);
    Mockito.when(dto.name().value()).thenReturn("tag-name");
    Mockito.when(dto.slug().value()).thenReturn("tag-slug");
    Mockito.when(dto.status().name()).thenReturn("ACTIVE");
    Mockito.when(dto.createdAt()).thenReturn(created);
    Mockito.when(dto.updatedAt()).thenReturn(updated);

    TagView view = mapper.toView(dto);

    assertEquals(id.toString(), view.getId());
    assertEquals("tag-name", view.getName());
    assertEquals("tag-slug", view.getSlug());
    assertEquals(TagStatus.ACTIVE, view.getStatus());

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
  void toViewList_mapsList_ofDtos() {
    UUID id = UUID.fromString("22222222-2222-2222-2222-222222222222");
    Instant now = Instant.parse("2023-01-02T03:04:05Z");

    TagViewDto dto = Mockito.mock(TagViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    Mockito.when(dto.id().value()).thenReturn(id);
    Mockito.when(dto.name().value()).thenReturn("list-name");
    Mockito.when(dto.slug().value()).thenReturn("list-slug");
    Mockito.when(dto.status().name()).thenReturn("ACTIVE");
    Mockito.when(dto.createdAt()).thenReturn(now);
    Mockito.when(dto.updatedAt()).thenReturn(now);

    List<TagView> result = mapper.toViewList(List.of(dto));

    assertEquals(1, result.size());
    TagView view = result.getFirst();

    assertEquals(id.toString(), view.getId());
    assertEquals("list-name", view.getName());
    assertEquals("list-slug", view.getSlug());
    assertEquals(TagStatus.ACTIVE, view.getStatus());
    assertNotNull(view.getCreatedAt());
    assertEquals(OffsetDateTime.ofInstant(now, ZoneOffset.UTC), view.getCreatedAt());
  }
}
