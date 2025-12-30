package com.luisjrz96.blog.adapters.web.controllers.blog.tag;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

import com.luisjrz96.blog.adapters.web.dto.TagView;
import com.luisjrz96.blog.application.blog.tag.query.TagViewDto;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TagViewMapper {

  @Mapping(target = "id", expression = "java(String.valueOf(tagViewDto.id().value()))")
  @Mapping(target = "name", expression = "java(tagViewDto.name().value())")
  @Mapping(target = "slug", expression = "java(tagViewDto.slug().value())")
  @Mapping(
      target = "status",
      expression =
          "java(com.luisjrz96.blog.adapters.web.dto.TagStatus.fromValue(tagViewDto.status().name()))")
  TagView toView(TagViewDto tagViewDto);

  List<TagView> toViewList(List<TagViewDto> dtos);

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
