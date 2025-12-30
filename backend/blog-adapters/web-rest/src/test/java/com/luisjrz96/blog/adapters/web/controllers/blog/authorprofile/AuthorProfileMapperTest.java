package com.luisjrz96.blog.adapters.web.controllers.blog.authorprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import com.luisjrz96.blog.adapters.web.dto.AuthorProfileView;
import com.luisjrz96.blog.adapters.web.dto.SocialLink;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialNetwork;
import com.luisjrz96.blog.domain.shared.Url;

class AuthorProfileMapperTest {

  private final AuthorProfileViewMapper mapper = Mappers.getMapper(AuthorProfileViewMapper.class);

  @Test
  void toOffset_givenInstant_convertsToUtcOffsetDateTime() {
    Instant instant = Instant.parse("2025-01-01T10:00:00Z");

    OffsetDateTime result = mapper.toOffset(instant);

    assertEquals(OffsetDateTime.ofInstant(instant, ZoneOffset.UTC), result);
  }

  @Test
  void toOffset_givenNull_returnsNull() {
    assertNull(mapper.toOffset(null));
  }

  @Test
  void toURI_givenImageUrl_returnsURI() {
    ImageUrl img = new ImageUrl("https://example.com/avatar.png");

    URI result = mapper.toURI(img);

    assertEquals(URI.create("https://example.com/avatar.png"), result);
  }

  @Test
  void toURI_givenUrl_returnsURI() {
    Url url = new Url("https://example.com/resume.pdf");

    URI result = mapper.toURI(url);

    assertEquals(URI.create("https://example.com/resume.pdf"), result);
  }

  @Test
  void map_givenDomainSocialLinks_mapsToDtoSocialLinks() {
    var domainLinks =
        List.of(
            new com.luisjrz96.blog.domain.shared.SocialLink(
                SocialNetwork.LINKEDIN, new Url("https://linkedin.com/in/jhondoe")),
            new com.luisjrz96.blog.domain.shared.SocialLink(
                SocialNetwork.GITHUB, new Url("https://github.com/jhondoe")));

    List<SocialLink> result = mapper.map(domainLinks);

    assertEquals(2, result.size());

    SocialLink first = result.getFirst();
    assertEquals(SocialLink.PlatformEnum.LINKEDIN, first.getPlatform());
    assertEquals(URI.create("https://linkedin.com/in/jhondoe"), first.getUrl());

    SocialLink second = result.get(1);
    assertEquals(SocialLink.PlatformEnum.GITHUB, second.getPlatform());
    assertEquals(URI.create("https://github.com/jhondoe"), second.getUrl());
  }

  @Test
  void toView_mapsAuthorId_andBio() {
    UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");

    AuthorProfileViewDto dto = mock(AuthorProfileViewDto.class, Mockito.RETURNS_DEEP_STUBS);
    when(dto.id().value()).thenReturn(uuid);
    when(dto.bio().value()).thenReturn("my bio");
    when(dto.avatar().value()).thenReturn("https://avatars/avatar1.jpg");
    when(dto.resumeUrl().value()).thenReturn("https://resumes/resum1.pdf");
    when(dto.portfolioUrl().value()).thenReturn("https://portfolio/p1.html");

    AuthorProfileView view = mapper.toView(dto);

    assertNotNull(view);
    assertEquals(uuid.toString(), view.getAuthorId());
    assertEquals("my bio", view.getBio());
  }
}
