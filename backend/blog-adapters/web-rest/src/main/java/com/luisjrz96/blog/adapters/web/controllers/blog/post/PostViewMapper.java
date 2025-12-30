package com.luisjrz96.blog.adapters.web.controllers.blog.post;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;

import com.luisjrz96.blog.adapters.web.dto.PostDetailView;
import com.luisjrz96.blog.adapters.web.dto.PostDetailViewCategory;
import com.luisjrz96.blog.adapters.web.dto.PostDetailViewTag;
import com.luisjrz96.blog.adapters.web.dto.PostSummaryView;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostCategoryViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostTagViewDto;
import com.luisjrz96.blog.application.blog.post.query.handler.dto.PostViewDto;
import com.luisjrz96.blog.domain.shared.ImageUrl;

import jakarta.validation.Valid;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PostViewMapper {

  // ----------- FIELD MAPPINGS -----------
  @Mapping(target = "id", expression = "java(postViewDto.id().value().toString())")
  @Mapping(target = "title", expression = "java(postViewDto.title().value())")
  @Mapping(target = "slug", expression = "java(postViewDto.slug().value())")
  @Mapping(target = "summary", expression = "java(postViewDto.summary().value())")
  @Mapping(target = "body", expression = "java(postViewDto.body().value())")
  @Mapping(
      target = "category",
      expression = "java(toPostDetailCategoryView(postViewDto.category()))")
  @Mapping(target = "tags", expression = "java(toPostDetailViewTagList(postViewDto.tags()))")
  @Mapping(
      target = "status",
      expression =
          "java(com.luisjrz96.blog.adapters.web.dto.PostStatus.fromValue(postViewDto.status().name()))")
  PostDetailView toPostDetailView(PostViewDto postViewDto);

  // ----------- SUMMARY -----------
  @Mapping(target = "id", expression = "java(post.id().value().toString())")
  @Mapping(target = "title", expression = "java(post.title().value())")
  @Mapping(target = "slug", expression = "java(post.slug().value())")
  @Mapping(target = "summary", expression = "java(post.summary().value())")
  @Mapping(
      target = "status",
      expression =
          "java(com.luisjrz96.blog.adapters.web.dto.PostStatus.fromValue(post.status().name()))")
  PostSummaryView toSummaryView(PostViewDto post);

  // ----------- LIST / SUMMARY -----------
  List<@Valid PostSummaryView> toSummaryViewList(List<PostViewDto> items);

  List<@Valid PostDetailViewTag> toPostDetailTagInnerList(List<PostTagViewDto> items);

  // ----------- HELPERS -----------
  default PostDetailViewCategory toPostDetailCategoryView(PostCategoryViewDto category) {
    if (category == null) return null;
    return new PostDetailViewCategory(
        String.valueOf(category.id().value()), String.valueOf(category.name().value()));
  }

  default List<PostDetailViewTag> toPostDetailViewTagList(Set<PostTagViewDto> tags) {
    if (tags == null) return Collections.emptyList();
    if (tags.isEmpty()) return Collections.emptyList();
    return tags.stream().map(this::toPostDetailTagView).toList();
  }

  default PostDetailViewTag toPostDetailTagView(PostTagViewDto postTagViewDto) {
    return new PostDetailViewTag(
        String.valueOf(postTagViewDto.id().value()), postTagViewDto.name().value());
  }

  default OffsetDateTime toOffset(Instant instant) {
    return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

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

  default JsonNullable<OffsetDateTime> map(Instant instant) {
    if (instant != null) {
      return JsonNullable.of(toOffset(instant));
    } else {
      return JsonNullable.undefined();
    }
  }
}
