package com.luisjrz96.blog.adapters.persistence.blog.authorprofile;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisjrz96.blog.adapters.persistence.exception.EventSerializationException;
import com.luisjrz96.blog.adapters.persistence.exception.UnknownEventException;
import com.luisjrz96.blog.domain.DomainEvent;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileCreated;
import com.luisjrz96.blog.domain.blog.authorprofile.events.AuthorProfileUpdated;

@Service
public class AuthorProfileProjectionHandler {

  private final JdbcTemplate jdbc;
  private final ObjectMapper objectMapper;

  public AuthorProfileProjectionHandler(JdbcTemplate jdbc, ObjectMapper objectMapper) {
    this.jdbc = jdbc;
    this.objectMapper = objectMapper;
  }

  public void project(DomainEvent event, int newVersion) {
    switch (event) {
      case AuthorProfileCreated e -> applyCreated(e, newVersion);
      case AuthorProfileUpdated e -> applyUpdated(e, newVersion);
      default -> throw new UnknownEventException(event.getClass().getName());
    }
  }

  private void applyCreated(AuthorProfileCreated e, int newVersion) {
    try {
      String socialLinksJson =
          Optional.ofNullable(objectMapper.writeValueAsString(e.socialLinks())).orElse("[]");

      jdbc.update(
          """
                INSERT INTO author_profile_view
                    (author_id, bio_markdown, avatar_url, resume_url, portfolio_url, social_links, created_at,
                    updated_at, last_version_applied)
                    VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, NULL, ?)
              """,
          String.valueOf(e.authorId().value()),
          e.bio().value(),
          e.avatar().value(),
          e.resumeUrl().value(),
          e.portfolioUrl().value(),
          socialLinksJson,
          Timestamp.from(e.createdAt()),
          newVersion);

    } catch (JsonProcessingException ex) {
      throw new EventSerializationException(
          String.format(
              "Failing to convert socialLinks to json for actor %s",
              e.authorId().value().toString()));
    }
  }

  private void applyUpdated(AuthorProfileUpdated e, int newVersion) {
    try {
      String socialLinksJson =
          Optional.ofNullable(objectMapper.writeValueAsString(e.socialLinks())).orElse("[]");
      jdbc.update(
          """
                UPDATE author_profile_view
                    SET bio_markdown = ?,
                        avatar_url = ?,
                        resume_url = ?,
                        portfolio_url = ?,
                        social_links = ?::jsonb,
                        updated_at = ?,
                        last_version_applied = ?
                WHERE author_id = ?
                AND last_version_applied < ?
              """,
          e.bio().value(),
          e.avatar().value(),
          e.resumeUrl().value(),
          e.portfolioUrl().value(),
          socialLinksJson,
          Timestamp.from(e.updatedAt()),
          newVersion,
          String.valueOf(e.authorId().value()),
          newVersion);
    } catch (JsonProcessingException ex) {
      throw new EventSerializationException(
          String.format(
              "Failing to convert socialLinks to json for actor %s",
              e.authorId().value().toString()));
    }
  }
}
