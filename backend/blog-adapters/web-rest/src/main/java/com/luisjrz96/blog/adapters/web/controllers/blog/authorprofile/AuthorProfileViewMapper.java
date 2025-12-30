package com.luisjrz96.blog.adapters.web.controllers.blog.authorprofile;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.luisjrz96.blog.adapters.web.dto.AuthorProfileView;
import com.luisjrz96.blog.adapters.web.dto.SocialLink;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.Url;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AuthorProfileViewMapper {

  @Mapping(target = "authorId", expression = "java(authorProfileViewDto.id().value().toString())")
  @Mapping(target = "bio", expression = "java(authorProfileViewDto.bio().value())")
  AuthorProfileView toView(AuthorProfileViewDto authorProfileViewDto);

  default OffsetDateTime toOffset(Instant instant) {
    return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  default URI toURI(ImageUrl imageUrl) {
    return URI.create(imageUrl.value());
  }

  default URI toURI(Url url) {
    return URI.create(url.value());
  }

  default List<SocialLink> map(List<com.luisjrz96.blog.domain.shared.SocialLink> socialLinks) {
    return socialLinks.stream()
        .map(
            e ->
                new SocialLink(
                    SocialLink.PlatformEnum.fromValue(e.socialNetwork().name()),
                    URI.create(e.url().value())))
        .toList();
  }
}
