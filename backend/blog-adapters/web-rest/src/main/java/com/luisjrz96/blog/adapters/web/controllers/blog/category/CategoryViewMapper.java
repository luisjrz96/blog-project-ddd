package com.luisjrz96.blog.adapters.web.controllers.blog.category;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

import com.luisjrz96.blog.adapters.web.dto.CategoryView;
import com.luisjrz96.blog.application.blog.category.query.CategoryViewDto;
import com.luisjrz96.blog.domain.shared.ImageUrl;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CategoryViewMapper {

  @Mapping(target = "id", expression = "java(categoryViewDto.id().value().toString())")
  @Mapping(target = "name", expression = "java(categoryViewDto.name().value())")
  @Mapping(target = "slug", expression = "java(categoryViewDto.slug().value())")
  @Mapping(
      target = "status",
      expression =
          "java(com.luisjrz96.blog.adapters.web.dto.CategoryStatus.fromValue(categoryViewDto.status().name()))")
  CategoryView toView(CategoryViewDto categoryViewDto);

  List<CategoryView> toViewList(List<CategoryViewDto> dtos);

  default URI toURI(ImageUrl imageUrl) {
    return URI.create(imageUrl.value());
  }

  default JsonNullable<URI> map(ImageUrl imageUrl) {
    if (imageUrl != null) {
      return JsonNullable.of(toURI(imageUrl));
    } else {
      return JsonNullable.undefined();
    }
  }

  default OffsetDateTime toOffsetDateTime(Instant instant) {
    if (instant != null) {
      return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
    return null;
  }

  default JsonNullable<OffsetDateTime> map(Instant instant) {
    if (instant != null) {
      return JsonNullable.of(toOffsetDateTime(instant));
    } else {
      return JsonNullable.undefined();
    }
  }
}
