package com.luisjrz96.blog.adapters.web.controllers.blog.category;

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

import com.luisjrz96.blog.adapters.web.dto.CategoryStatus;
import com.luisjrz96.blog.adapters.web.dto.CategoryView;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;

class CategoryViewMapperTest {

  private final CategoryViewMapper mapper = Mappers.getMapper(CategoryViewMapper.class);

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
  void toView_mapsAllFields_andConvertsInstantsToJsonNullable() {
    UUID id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    Instant created = Instant.parse("2023-01-02T03:04:05Z");
    Instant updated = Instant.parse("2024-02-03T04:05:06Z");
    Instant archived = Instant.parse("2025-03-04T05:06:07Z");

    CategoryViewDto dto = Mockito.mock(CategoryViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    Mockito.when(dto.id().value()).thenReturn(id);
    Mockito.when(dto.name().value()).thenReturn("category-name");
    Mockito.when(dto.slug().value()).thenReturn("category-slug");
    Mockito.when(dto.status().name()).thenReturn("ACTIVE");
    Mockito.when(dto.defaultImage().value()).thenReturn("http://example.com/image.png");
    Mockito.when(dto.createdAt()).thenReturn(created);
    Mockito.when(dto.updatedAt()).thenReturn(updated);
    Mockito.when(dto.archivedAt()).thenReturn(archived);

    CategoryView view = mapper.toView(dto);

    assertEquals(id.toString(), view.getId());
    assertEquals("category-name", view.getName());
    assertEquals("category-slug", view.getSlug());
    assertEquals(CategoryStatus.fromValue(dto.status().name()), view.getStatus());

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
    UUID id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    Instant now = Instant.parse("2023-01-02T03:04:05Z");

    CategoryViewDto dto = Mockito.mock(CategoryViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    Mockito.when(dto.id().value()).thenReturn(id);
    Mockito.when(dto.name().value()).thenReturn("list-name");
    Mockito.when(dto.slug().value()).thenReturn("list-slug");
    Mockito.when(dto.status().name()).thenReturn("ACTIVE");
    Mockito.when(dto.defaultImage().value()).thenReturn("http://example.com/list-image.png");
    Mockito.when(dto.createdAt()).thenReturn(now);
    Mockito.when(dto.updatedAt()).thenReturn(now);

    List<CategoryView> result = mapper.toViewList(List.of(dto));

    assertEquals(1, result.size());
    CategoryView view = result.getFirst();

    assertEquals(id.toString(), view.getId());
    assertEquals("list-name", view.getName());
    assertEquals("list-slug", view.getSlug());
    assertEquals(CategoryStatus.fromValue(dto.status().name()), view.getStatus());
    assertNotNull(view.getCreatedAt());
    assertEquals(OffsetDateTime.ofInstant(now, ZoneOffset.UTC), view.getCreatedAt());
  }
}
