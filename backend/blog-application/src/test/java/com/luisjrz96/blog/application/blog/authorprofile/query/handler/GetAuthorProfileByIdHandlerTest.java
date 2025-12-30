package com.luisjrz96.blog.application.blog.authorprofile.query.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.luisjrz96.blog.application.blog.authorprofile.port.AuthorProfileViewReader;
import com.luisjrz96.blog.application.blog.authorprofile.query.AuthorProfileViewDto;
import com.luisjrz96.blog.application.blog.authorprofile.query.GetAuthorProfileByIdQuery;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.SocialNetwork;
import com.luisjrz96.blog.domain.shared.Url;

@ExtendWith(MockitoExtension.class)
class GetAuthorProfileByIdHandlerTest {

  @Mock private AuthorProfileViewReader reader;

  @Test
  void handle_shouldReturnAuthorProfileViewDtoFromReader() {
    GetAuthorProfileByIdHandler handler = new GetAuthorProfileByIdHandler(reader);

    AuthorId authorId = new AuthorId(UUID.randomUUID());
    GetAuthorProfileByIdQuery query = new GetAuthorProfileByIdQuery(authorId);

    AuthorProfileViewDto dto =
        new AuthorProfileViewDto(
            authorId,
            new Markdown("ABCD"),
            new ImageUrl("https://avatars/avatar1.jpg"),
            new Url("https://resumes/resume4.pdf"),
            new Url("https://portfolio/portfolio4.html"),
            List.of(new SocialLink(SocialNetwork.LINKEDIN, new Url("https://linkedin.in/jhondoe"))),
            Instant.now(),
            Instant.now());

    when(reader.findById(authorId)).thenReturn(Optional.of(dto));
    AuthorProfileViewDto result = handler.handle(query);

    assertEquals(dto, result);
    verify(reader, times(1)).findById(authorId);
    verifyNoMoreInteractions(reader);
  }
}
