package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileCreated;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileUpdated;
import com.luisjrz96.blog.domain.blog.post.Markdown;
import com.luisjrz96.blog.domain.shared.AuthorId;
import com.luisjrz96.blog.domain.shared.ImageUrl;
import com.luisjrz96.blog.domain.shared.SocialLink;
import com.luisjrz96.blog.domain.shared.SocialNetwork;
import com.luisjrz96.blog.domain.shared.Url;

@ExtendWith(MockitoExtension.class)
class AuthorProfileProjectionHandlerTest {

  @Mock private JdbcTemplate jdbc;
  @Mock private ObjectMapper objectMapper;
  @InjectMocks private AuthorProfileProjectionHandler projector;

  @Test
  void shouldInsertAuthorProfileOnCreatedEvent() throws JsonProcessingException {
    // given
    Instant now = Instant.parse("2025-01-01T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    AuthorProfileCreated event =
        new AuthorProfileCreated(
            new AuthorId(uuid),
            new Markdown("ABCD"),
            new ImageUrl("https://avatars/avatar1.jpg"),
            new Url("https://resumes/resume1.pdf"),
            new Url("https://portfolios/p1.html"),
            List.of(new SocialLink(SocialNetwork.LINKEDIN, new Url("https://linkedin.in/jhondoe"))),
            now);

    int version = 1;

    // when
    when(objectMapper.writeValueAsString(any()))
        .thenReturn(
            """
                [{"url": {"value": "https://linkedin.com/in/luis"},
                  "socialNetwork": "LINKEDIN"
                }]
                """);
    projector.project(event, version);

    verify(jdbc)
        .update(
            contains("INSERT INTO author_profile_view"),
            eq(String.valueOf(uuid)),
            eq("ABCD"),
            eq("https://avatars/avatar1.jpg"),
            eq("https://resumes/resume1.pdf"),
            eq("https://portfolios/p1.html"),
            contains("LINKEDIN"),
            eq(Timestamp.from(now)),
            eq(version));
  }

  @Test
  void shouldUpdateAuthorProfileOnUpdatedEvent() throws JsonProcessingException {
    // given
    Instant now = Instant.parse("2025-01-01T10:00:00Z");
    UUID uuid = UUID.randomUUID();
    AuthorProfileUpdated event =
        new AuthorProfileUpdated(
            new AuthorId(uuid),
            new Markdown("ABCD"),
            new ImageUrl("https://avatars/avatar1.jpg"),
            new Url("https://resumes/resume1.pdf"),
            new Url("https://portfolios/p1.html"),
            List.of(new SocialLink(SocialNetwork.LINKEDIN, new Url("https://linkedin.in/jhondoe"))),
            now);

    int version = 2;

    // when
    when(objectMapper.writeValueAsString(any()))
        .thenReturn(
            """
                [{"url": {"value": "https://linkedin.com/in/luis"},
                  "socialNetwork": "LINKEDIN"
                }]
                """);
    projector.project(event, version);

    verify(jdbc)
        .update(
            contains("UPDATE author_profile_view"),
            eq("ABCD"),
            eq("https://avatars/avatar1.jpg"),
            eq("https://resumes/resume1.pdf"),
            eq("https://portfolios/p1.html"),
            contains("LINKEDIN"),
            eq(Timestamp.from(now)),
            eq(version),
            eq(uuid.toString()),
            eq(version));
  }
}
